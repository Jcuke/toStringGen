package com.jcuke.tostringGen;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cuke on 2018/6/16.
 */
public class ToStringGen extends AnAction {

    public void actionPerformed(AnActionEvent e) {

        PsiClass psiClass = getPsiClassFromContext(e);

        GenerateDialog dlg = new GenerateDialog(psiClass);
        dlg.show();
        if(dlg.isOK()){
            System.out.println("ok click");
            generateToString(psiClass, dlg.getFields());
        }
    }

    private void generateToString(final PsiClass psiClass, final List<PsiField> fields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {

            @Override
            protected void run() throws Throwable {
                generateToStringImpl(psiClass, fields);
            }
        }.execute();
    }

    private void generateToStringImpl(PsiClass psiClass, List<PsiField> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("@Override\npublic String toString(){");
        sb.append("return \"{");
        int fieldCount = 0;
        for (PsiField field : fields) {
            String fieldText = field.getText();
            fieldText = fieldText.replaceAll("  ", "").trim();
            String fieldClassType = fieldText.split(" ")[1];

            boolean haveQuotationMark = true;

            //以下类型在json中显示为数值，不加引号
            List<String> numberTypes = Arrays.asList(new String[]{"int", "float", "double", "short", "byte", "integer", "float"});
            for (String numberType : numberTypes) {
                if(fieldClassType.equalsIgnoreCase(numberType)){
                    haveQuotationMark = false;
                }
            }
            if(haveQuotationMark){
                sb.append((fieldCount != 0 ? "," : "") + "\\\"" + field.getName() + "\\\"" + " : "+ "\" + \"\\\"\"+ "+ field.getName() +" +\"\\\"\" +\"");
            } else {
                sb.append((fieldCount != 0 ? "," : "") + "\\\"" + field.getName() + "\\\"" + " : "+ "\" + "+ field.getName() +" +\"");
            }
            fieldCount++;
        }
        sb.append("}");
        sb.append("\";}");

        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiMember toString = elementFactory.createMethodFromText(sb.toString(), psiClass);
        PsiElement method = psiClass.add(toString);
        //JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(method);
        //JavaCodeStyleManager.getInstance(psiClass.getProject())
        CodeStyleManager.getInstance(psiClass.getProject()).reformat(method);
    }

    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if(psiFile == null || editor == null){
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elemetnAt = psiFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elemetnAt,  PsiClass.class);
        return psiClass;
    }
}
