package com.github.chriskn.jsonjlv.ui.properties;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.github.chriskn.jsonjlv.internal.Activator;
import com.github.chriskn.jsonjlv.prefrences.PreferencesModel;
import com.github.chriskn.jsonjlv.prefrences.PreferencesModelConverter;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        PreferencesModelConverter converter = new PreferencesModelConverter();
        PreferencesModel model = converter.getDefaultModel();
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(PreferencePage.NAME, converter.modelToJson(model));
    }

}
