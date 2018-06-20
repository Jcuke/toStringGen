package com.jcuke.generator.single;/**
 * Created by Administrator on 2018/6/20.
 */

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @program: toStringGen
 * @description: do generate
 * @author: wangjinwen
 * @create: 2018-06-20 09:53
 */
public class GenerateImpl {

    public static final void doIt(PsiClass psiClass, final List<PsiField> fields, boolean isSingle){
        StringBuilder sb = new StringBuilder();
        sb.append("@Override\npublic String toString(){");
        sb.append("return \"{");
        int fieldCount = 0;

        for (PsiField field : psiClass.getFields()) {

            if(isSingle && fields != null && fields.size() > 0){
                if(!fields.contains(field)){
                    continue;
                }
            }

            String fieldText = field.getText();
            fieldText = fieldText.replaceAll("  ", "").trim();
            if (fieldText.split(" ").length > 3) {
                //排除static final、serialVersionUID等特殊属性
                continue;
            }
            String fieldClassType = fieldText.split(" ")[1];

            boolean isNumberType = false;
            //数值类型的包装类
            boolean isNumberObjectType = false;

            //数组类型的属性值显示时不用加引号
            List<String> numberTypes = Arrays.asList(new String[]{"int", "float", "double", "short", "byte", "integer", "float", "boolean"});
            for (String numberType : numberTypes) {
                if (fieldClassType.equalsIgnoreCase(numberType)) {
                    isNumberType = true;
                }
                if(fieldClassType.equals(numberType.toUpperCase())){
                    isNumberObjectType = true;
                }
            }

            String fname = field.getName();

            if (isNumberType) {
                if(isNumberObjectType){
                    String printValue = fname + " == null ? null : \\\" + " + fname + " + \\\"";
                    sb.append((fieldCount != 0 ? "," : "") + "\\\"" + fname + "\\\"" + " : " + "\" + " + printValue + " +\"");
                } else {
                    //数值类型的属性
                    sb.append((fieldCount != 0 ? "," : "") + "\\\"" + fname + "\\\"" + " : " + "\" + " + fname + " +\"");
                }

            } else {
                String classType = "java.util." + fieldClassType;
                //如果不是数值类型，需要加引号
                boolean isCollectionType = true;
                try {
                    //对集合对象的处理
                    Class fieldClass = Class.forName(classType);
                    //是否从Collections中继承
                    if (Collection.class.isAssignableFrom(fieldClass)) {
                        String printValue = "\"+(" + fname + " == null ? \"[]\" :\"[\" + com.sun.deploy.util.StringUtils.join(" + fname + ", \",\").replaceAll(\",\", \"\\\",\\\"\") + \"]\")+ \"";
                        sb.append((fieldCount != 0 ? "," : "") + "\\\"" + fname + "\\\"" + " : " + printValue);
                    } else {
                        isCollectionType = false;
                    }
                } catch (ClassNotFoundException e) {
                    isCollectionType = false;
                }

                //不是集合类型的属性
                if (!isCollectionType) {

                    //数组类型的属性
                    if (fieldClassType.contains("[]")) {
                        String printValue = "[\\\"\" + StringUtils.join(java.util.Arrays.asList(" + fname + " == null ? new Object[]{} : " + fname + "), \",\").replaceAll(\",\", \"\\\",\\\"\") +\"\\\"]";
                        sb.append((fieldCount != 0 ? "," : "") + "\\\"" + fname + "\\\"" + " : " + printValue);

                        //简单字符串类型
                    } else if ("String".equals(fieldClassType)) {
                        String printValue = fname + " == null ? null : \"\\\"\" + " + fname + " + \"\\\"\"";
                        sb.append((fieldCount != 0 ? "," : "") + "\\\"" + fname + "\\\"" + " : " + "\" + (" + printValue + ") +\"");

                        //Boolean 类型
                    } else if ("Boolean".equals(fieldClassType)) {
                        String printValue = fname + " == null ? null : \"\\\"\" + " + fname + " + \"\\\"\"";
                        sb.append((fieldCount != 0 ? "," : "") + "\\\"" + fname + "\\\"" + " : " + "\" + (" + printValue + ") +\"");

                        //boolean 类型
                    } else if ("boolean".equals(fieldClassType)) {
                        sb.append((fieldCount != 0 ? "," : "") + "\\\"" + fname + "\\\"" + " : " + "\" + " + fname + " +\"");

                        //键值对类型,只有这一种类型的属性依赖JSONObject的jar
                    } else {
                        boolean isKvType = true;
                        try {
                            //是否从Map中继承
                            if (Map.class.isAssignableFrom(Class.forName(classType))) {
                                String printValue = "\"+com.alibaba.fastjson.JSONObject.toJSON(" + fname + " == null ? new Object() : " + fname + ")+\"";
                                sb.append((fieldCount != 0 ? "," : "") + "\\\"" + fname + "\\\"" + " : " + printValue);
                            } else {
                                isKvType = false;
                            }
                        } catch (ClassNotFoundException e) {
                            isKvType = false;
                        }
                        //其它定义对象类型
                        if (!isKvType) {
                            //自定义对象统一走自身的toString，尽可能避免用户装 com.alibaba.fastjson.JSONObject的依赖
                            //String printValue = fname + " == null ? null : \"\\\"\" + "+ fname +" + \"\\\"\"";
                            String printValue = fname + " == null ? null : " + fname;
                            sb.append((fieldCount != 0 ? "," : "") + "\\\"" + fname + "\\\"" + " : " + "\" + (" + printValue + ") +\"");
                        }
                    }
                }
            }
            fieldCount++;
        }
        sb.append("}");
        sb.append("\";}");

        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiMember toString = elementFactory.createMethodFromText(sb.toString(), psiClass);
        PsiElement method = psiClass.add(toString);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(method);
        CodeStyleManager.getInstance(psiClass.getProject()).reformat(method);
    }

    public static final void generateToStringImpl(final PsiClass psiClass, final List<PsiField> fields, final boolean isSingle) {

        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                //删除已存在的方法
                //没有文档，经测试，猜测，第二个参数是表示查找范围是否包含父类
                PsiMethod[] pms = psiClass.findMethodsByName("toString", false);
                if (pms.length > 0) {
                    for (PsiMethod pm : pms) {
                        pm.delete();
                    }
                }
                doIt(psiClass, fields, isSingle);
            }
        }.execute();
    }

}
