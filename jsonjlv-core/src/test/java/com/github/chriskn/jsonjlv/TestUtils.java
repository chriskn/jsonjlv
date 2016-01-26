package com.github.chriskn.jsonjlv;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

public class TestUtils {
    
    public static final String TEST_JSON_STRING = "{\"severity\":\"ERROR\",\"exception\":{\"stacktrace\":[{\"file\":\"file1\",\"method\":\"method1\",\"line\":\"1\",\"class\":\"class1\"},{\"file\":\"file2\",\"method\":\"method2\",\"line\":\"2\",\"class\":\"class2\"}],\"type\":\"java.lang.IllegalArgumentException\",\"message\":\"Dummy message\"},\"bundle-name\":\"Dummy\",\"bundle-version\":\"1\",\"time\":123445678,\"message\":\"This is a test\",\"bundle-state\":\"RESOLVED\"}";

    public static int getBlockedPort() throws IOException {
        for (int port = 1; port < 5000; port++) {
            ServerSocket s = null;
            try {
                s = new ServerSocket(port);
            } catch (BindException ex) {
                return port;
            } finally {
                if (s != null) {
                    s.close();
                }
            }
        }
        return -1;
    }

    public static int getAvailablePort(Integer...notAllowed ) throws IOException {
        for (int port = 1; port < 5000; port++) {
            if (Arrays.asList(notAllowed).contains(port)){
                continue; 
            }
            ServerSocket s = null;
            try {
                s = new ServerSocket(port);
                s.close();
                return port;
            } catch (BindException ex) {
                continue;
            } finally {
                if (s != null) {
                    s.close();
                   
                }
            }
        }
        return -1;
    }

    public static JSONObject createJsonException(Throwable exception) {
        JSONObject jsonException = new JSONObject();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTraceString = sw.toString();
        pw.close();
        try {
            sw.close();
        } catch (IOException e) {
        }
        jsonException.put("type", exception.getClass().getName());
        jsonException.put("message", exception.getMessage());
        jsonException.put("stacktrace", stackTraceString);
        return jsonException;
    }

    public static JSONArray createJsonStacktrace(StackTraceElement[] stackTrace) {
        JSONArray jsonStackTrace = new JSONArray();
        for (int i = 0; i < stackTrace.length; i++) {
            JSONObject jsonStackTraceElement = new JSONObject();
            jsonStackTraceElement.put("class", stackTrace[i].getClassName());
            jsonStackTraceElement.put("method", stackTrace[i].getMethodName());
            jsonStackTraceElement.put("line", stackTrace[i].getLineNumber() + "");
            jsonStackTraceElement.put("file", stackTrace[i].getFileName());
            jsonStackTrace.put(jsonStackTraceElement);
        }
        return jsonStackTrace;
    }

    public static JSONObject createJsonLoggingEvent(String severity, String message, long time, String name,
            String version, String state) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("severity", severity);
        jsonObject.put("message", message);
        jsonObject.put("time", time);
        jsonObject.put("bundle-name", name);
        jsonObject.put("bundle-version", version);
        jsonObject.put("bundle-state", state);
        return jsonObject;
    }

}
