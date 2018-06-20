package com.jcuke.generator.multiple;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.jcuke.generator.single.GenerateImpl;

/**
 * Created by Administrator on 2018/6/20.
 */
public class ToStringGen extends AnAction {
    public void actionPerformed(AnActionEvent e) {

        VirtualFile[] virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile psiFile = PsiManager.getInstance(e.getProject()).findFile(virtualFile);
            final PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile.getOriginalElement(), PsiClass.class);
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
                    GenerateImpl.generateToStringImpl(psiClass, null, false);
                }
            }.execute();
        }
    }

}
