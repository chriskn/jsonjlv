package com.github.chriskn.jsonjvl.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.github.chriskn.jsonjvl.internal.Activator;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static final String NAME = "jsonjvl-plugin.generalsettings";

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        parent.setLayout(createFieldEditorParentLayout());
        FieldEditor generalPreferenceEditor = new PreferenceEditor(NAME, parent);
        addField(generalPreferenceEditor);
    }

    private static Layout createFieldEditorParentLayout() {
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 15;
        return layout;
    }

    @Override
    protected void performApply() {
        super.performApply();
        try {
            Activator.getDefault().startServer();
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    private void showErrorDialog(Throwable e) {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        List<Status> childStatuses = new ArrayList<>();
        for (StackTraceElement element : e.getStackTrace()) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, element.toString());
            childStatuses.add(status);
        }
        MultiStatus info = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, childStatuses.toArray(new Status[] {}),
                e.toString(), e);
        ErrorDialog.openError(shell, "Error while starting server", e.getLocalizedMessage(), info);
    }
}
