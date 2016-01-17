package com.github.chriskn.jsonjvl.ui.properties;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import com.github.chriskn.jsonjvl.prefrences.PreferencesModel;
import com.github.chriskn.jsonjvl.prefrences.PreferencesModelConverter;

public final class PreferenceManager {

    private IPreferenceStore store;

    private PreferencesModel preferencesModel;

    private final PreferencesModelConverter converter = new PreferencesModelConverter();

    public PreferenceManager(IPreferenceStore store) {
        this.store = store;
        preferencesModel = converter.jsonToModel(store.getString(PreferencePage.NAME));
    }

    public void addPropertyChangeListener(IPropertyChangeListener listener) {
        if (listener != null) {
            store.addPropertyChangeListener(listener);
        }
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener) {
        if (listener != null) {
            store.removePropertyChangeListener(listener);
        }
    }

    public void setValue(PreferencesModel model) {
        update(model);
        String data = converter.modelToJson(model);
        store.setValue(PreferencePage.NAME, data);
    }

    public int getIncommingPortNumber() {
        return preferencesModel.getIncomingPort();
    }

    public int getOutgoingPortNumber() {
        return preferencesModel.getOutgoingPort();
    }

    public String getOutgoingIp() {
        return preferencesModel.getOutgoingIp();
    }

    public boolean isServerAutoStart() {
        return preferencesModel.isAutoStart();
    }

    private void update(PreferencesModel model) {
        preferencesModel = model;

    }
}
