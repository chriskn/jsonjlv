package com.github.chriskn.jsonjlv.prefrences;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.codehaus.jackson.map.ObjectMapper;


public class PreferencesModelConverter {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    private final ObjectMapper mapper = new ObjectMapper();

	public PreferencesModel getDefaultModel() {
		PreferencesModel defaultModel = new PreferencesModel();
		defaultModel.setIncomingPort(4444);
	    defaultModel.setOutgoingPort(4445);
        defaultModel.setOutgoingIp("127.0.0.1");
		defaultModel.setAutoStart(true);
		return defaultModel;
	}

	public PreferencesModel jsonToModel(String json) {
		try {
		    PreferencesModel model = mapper.readValue(json, PreferencesModel.class);
			return model;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return new PreferencesModel();
	}

	public String modelToJson(PreferencesModel model) {
		try {
			return mapper.writeValueAsString(model);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return "";
	}
}
