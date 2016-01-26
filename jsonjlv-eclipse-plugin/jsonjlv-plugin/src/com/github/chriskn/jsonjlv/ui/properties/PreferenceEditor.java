package com.github.chriskn.jsonjlv.ui.properties;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.github.chriskn.jsonjlv.internal.Activator;
import com.github.chriskn.jsonjvl.prefrences.PreferencesModel;
import com.github.chriskn.jsonjvl.prefrences.PreferencesModelConverter;

public class PreferenceEditor extends FieldEditor {

    // private static final String IPADDRESS_PATTERN =
    // "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    // "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    // "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    // "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static final String PORTNUMBER_PATTERN = "[\\d]*";

    private PreferencesModelConverter converter = new PreferencesModelConverter();
    private PreferencesModel model;
    private Composite topComposite;
    private Text incomingPortFieldControl;
    private Text outgoingPortFieldControl;
    private Text outgoingIpFieldControl;
    private Button autoStartSwitcherControl;

    private PreferenceManager preferenceManager;

    public PreferenceEditor(String name, Composite parent) {
        init(name, "");
        preferenceManager = Activator.getDefault().getPreferenceManager();
        createControl(parent);
    }

    @Override
    public void adjustForNumColumns(int numColumns) {
        ((GridData) topComposite.getLayoutData()).horizontalSpan = numColumns;
    }

    @Override
    public int getNumberOfControls() {
        return 1;
    }

    @Override
    public void doFillIntoGrid(Composite parent, int numColumns) {
        topComposite = parent;
        createIncomingPortFieldControl(topComposite);
        createOutgoingPortFieldControl(topComposite);
        createOutgoingIpFieldControl(topComposite);
        createAutoStartSwitcherControl(topComposite);
    }

    @Override
    public void doLoad() {
        model = (PreferencesModel) converter.jsonToModel(getPreferenceStore().getString(PreferencePage.NAME));
        init();
    }

    @Override
    public void doLoadDefault() {
        model = (PreferencesModel) converter.getDefaultModel();
        init();
    }

    @Override
    public void doStore() {
        preferenceManager.setValue(model);
    }

    private void init() {
        incomingPortFieldControl.setText(String.valueOf(model.getIncomingPort()));
        outgoingPortFieldControl.setText(String.valueOf(model.getOutgoingPort()));
        outgoingIpFieldControl.setText(model.getOutgoingIp());
        autoStartSwitcherControl.setSelection(model.isAutoStart());
    }

    private void createIncomingPortFieldControl(Composite parent) {
        incomingPortFieldControl = createTextFieldControl(parent, "Incoming Port:");
        final PortValueHandler valueHandler = new PortValueHandler() {
            @Override
            protected void valueChanged() {
                model.setIncomingPort(Integer.parseInt(incomingPortFieldControl.getText()));
            }
        };
        addListeners(incomingPortFieldControl, valueHandler);
    }

    private void createOutgoingPortFieldControl(Composite parent) {
        outgoingPortFieldControl = createTextFieldControl(parent, "Outgoing Port:");
        final PortValueHandler valueHandler = new PortValueHandler() {
            @Override
            protected void valueChanged() {
                model.setOutgoingPort(Integer.parseInt(outgoingPortFieldControl.getText()));
            }
        };
        addListeners(outgoingPortFieldControl, valueHandler);
    }

    private void createOutgoingIpFieldControl(Composite parent) {
        outgoingIpFieldControl = createTextFieldControl(parent, "Outgoing IP:");
        final IpValueHandler valueHandler = new IpValueHandler() {
            @Override
            protected void valueChanged() {
                model.setOutgoingIp(outgoingIpFieldControl.getText());
            }
        };
        addListeners(outgoingIpFieldControl, valueHandler);
    }

    private void createAutoStartSwitcherControl(Composite parent) {
        autoStartSwitcherControl = createCheckBoxControl(parent, "Automatic start");
        autoStartSwitcherControl.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                model.setAutoStart(autoStartSwitcherControl.getSelection());
            }
        });
    }

    private void addListeners(final Text field, final ValueHandler valueHandler) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                valueHandler.valueChanged();
            }
        });
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                valueHandler.valueChanged();
            }
        });
        field.addVerifyListener(new VerifyListener() {
            @Override
            public void verifyText(final VerifyEvent e) {
                e.doit = valueHandler.isValid(e.text);
            }
        });
    }

    private static Text createTextFieldControl(Composite parent, String name) {
        Composite composite = new Composite(parent, SWT.FILL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.horizontalIndent = -5;
        composite.setLayoutData(layoutData);

        Label label = new Label(composite, SWT.NONE);
        label.setText(name);

        Text textField = new Text(composite, SWT.BORDER);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textField.setLayoutData(layoutData);
        return textField;
    }

    private static Button createCheckBoxControl(Composite parent, String name) {
        Composite composite = new Composite(parent, SWT.FILL);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);

        GridData layoutData = new GridData(SWT.BEGINNING, SWT.NONE, true, false);
        layoutData.horizontalIndent = -5;
        composite.setLayoutData(layoutData);

        Button checkBoxControl = new Button(composite, SWT.CHECK);
        checkBoxControl.setText(name);
        return checkBoxControl;
    }

    private abstract class ValueHandler {

        protected abstract void valueChanged();

        protected abstract boolean isValid(String value);

    }

    private abstract class PortValueHandler extends ValueHandler {

        protected abstract void valueChanged();

        protected boolean isValid(String value) {
            return value.matches(PORTNUMBER_PATTERN);
        }
    }

    private abstract class IpValueHandler extends ValueHandler {

        protected abstract void valueChanged();

        protected boolean isValid(String value) {
            // TODO validate
            return true;
        }
    }

}
