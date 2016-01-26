package com.github.chriskn.jsonjlv;

import java.lang.reflect.Constructor;
import java.util.Collections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventDeserializer {
    
    private final Logger logger = Logger.getLogger(this.getClass());

    public LoggingEvent deserialize(JSONObject jsonObject) {
        String message = jsonObject.getString("message");
        long timeStamp = jsonObject.getLong("time");
        Level level = Level.toLevel(jsonObject.getString("severity"));
        Throwable exception = deserializeException(jsonObject);
        String bundle = jsonObject.getString("bundle-name");
        String declaringClass = "Bundle: "+bundle;
        String method = "";
        String file = "";
        String line = "";
        if (exception != null && exception.getStackTrace().length > 0) {
            StackTraceElement firstElement = exception.getStackTrace()[0];
            declaringClass = declaringClass+"\n Class: "+firstElement.getClassName();
            method = firstElement.getMethodName();
            file = firstElement.getFileName();
            line = String.valueOf(firstElement.getLineNumber());
        }
        LocationInfo info = new LocationInfo(file, declaringClass, method, line);
        Logger logger = Logger.getLogger(declaringClass);
        return new LoggingEvent(declaringClass, logger, timeStamp, level, message, "",
                new ThrowableInformation(exception), declaringClass, info, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    protected Exception deserializeException(JSONObject jsonObject) {
        JSONObject jsonException = null; 
        try {
            jsonException = jsonObject.getJSONObject("exception"); 
        } catch (JSONException e) {
            // no exception data
            return null;
        }
        String eClass = jsonException.getString("type");
        String eMessage = jsonException.getString("message");
        Exception exception = null;
        try {
            Class<? extends Exception> clazz = (Class<? extends Exception>) Class.forName(eClass);
            Constructor<?> constructor = clazz.getConstructor(String.class);
            exception = (Exception) constructor.newInstance(eMessage);
            exception.setStackTrace(deserializeStacktrace(jsonException));
        } catch (Exception e) {
            logger.error("Error while deserializing exception", e);
        }
        return exception;
    }

    StackTraceElement[] deserializeStacktrace(JSONObject jsonObject) {
        JSONArray jsonStackTrace = null;
        try {
            jsonStackTrace = jsonObject.getJSONArray("stacktrace");
        } catch (JSONException e) {
            // no stacktrace
            return new StackTraceElement[0];
        }
        int lentgh = jsonStackTrace.length(); 
        StackTraceElement[] stacktrace = new StackTraceElement[lentgh];
        for (int i = 0; i < lentgh; i++) {
            JSONObject jsonStacktraceElement = (JSONObject) jsonStackTrace.get(i);
            String declaringClass = jsonStacktraceElement.getString("class");
            String methodName = jsonStacktraceElement.getString("method");
            int lineNumber = Integer.parseInt(jsonStacktraceElement.getString("line"));
            String fileName = jsonStacktraceElement.getString("file");
            StackTraceElement element = new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
            stacktrace[i] = element;
        }
        return stacktrace;
    }
}
