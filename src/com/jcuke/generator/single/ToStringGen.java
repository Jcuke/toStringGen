package com.jcuke.generator.single;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

/**
 * Created by cuke on 2018/6/16.
 */
public class ToStringGen extends AnAction {

    public void actionPerformed(AnActionEvent e) {

        PsiClass psiClass = getPsiClassFromContext(e);

        GenerateDialog dlg = new GenerateDialog(psiClass);
        dlg.show();
        if (dlg.isOK()) {
            System.out.println("ok click");
            generateToString(psiClass, dlg.getFields());
        }
    }

    private void generateToString(final PsiClass psiClass, final List<PsiField> fields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {

            @Override
            protected void run() throws Throwable {
                GenerateImpl.generateToStringImpl(psiClass, fields, true);
            }
        }.execute();
    }

    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elemetnAt = psiFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elemetnAt, PsiClass.class);
        return psiClass;
    }


}
