package com.jcuke.generator.single;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * Created by cuke on 2018/6/16.
 */
public class GenerateDialog extends DialogWrapper {

    private CollectionListModel<PsiField> myFields;
    private LabeledComponent<JPanel> myComponent;

    public GenerateDialog(PsiClass psiClass){
        super(psiClass.getProject());
        setTitle("Select Fields for ToString()");
        myFields = new CollectionListModel<PsiField>(psiClass.getAllFields());
        //myFields.addListDataListener(new ListDataListener() {
        //    @Override
        //    public void intervalAdded(ListDataEvent e) {
        //        System.out.println(1);
        //    }
        //
        //    @Override
        //    public void intervalRemoved(ListDataEvent e) {
        //        System.out.println(2);
        //    }
        //
        //    @Override
        //    public void contentsChanged(ListDataEvent e) {
        //        System.out.println(3);
        //    }
        //});
        JList fieldList = new JList(myFields);
        fieldList.setCellRenderer(new DefaultPsiElementCellRenderer());
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fieldList);
        decorator.disableAddAction();
        JPanel panel = decorator.createPanel();
        myComponent = LabeledComponent.create(panel, "select Field to generate toString()");

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myComponent;
    }

    public List<PsiField> getFields() {
        return myFields.getItems();
    }
}
