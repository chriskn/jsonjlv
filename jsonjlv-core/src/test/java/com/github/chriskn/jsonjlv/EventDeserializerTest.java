package com.github.chriskn.jsonjlv;

import static org.junit.Assert.*;

import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class EventDeserializerTest {

    private EventDeserializer sut;

    @Before
    public void setup() {
        sut = new EventDeserializer();
    }

    @Test
    public void test_DeserializeException_NoException() {
        Exception e = sut.deserializeException(new JSONObject());
        assertNull(e);
    }

    @Test
    public void test_deserializeException() {
        String dummyMessage = "This is a test";
        JSONObject jsonException = TestUtils.createJsonException(new NullPointerException(dummyMessage));
        JSONObject parent = new JSONObject();
        parent.put("exception", jsonException);
        Exception e = sut.deserializeException(parent);

        assertTrue(e instanceof NullPointerException);
        assertEquals(dummyMessage, e.getMessage());
    }

    @Test
    public void test_deserializeStacktrace_noStacktrace() {
        StackTraceElement[] elements = sut.deserializeStacktrace(new JSONObject());
        assertTrue(elements.length == 0);
    }

    @Test
    public void test_deserializeStacktrace_emptyStacktrace() {
        JSONArray emptyJsonStacktrace = TestUtils.createJsonStacktrace(new StackTraceElement[0]);
        JSONObject parent = new JSONObject();
        parent.put("stacktrace", emptyJsonStacktrace);
        StackTraceElement[] elements = sut.deserializeStacktrace(parent);
        assertTrue(elements.length == 0);
    }

    @Test
    public void test_deserializeStacktrace() {
        StackTraceElement element1 = new StackTraceElement("class1", "method1", "file1", 1);
        StackTraceElement element2 = new StackTraceElement("class2", "method2", "file2", 2);
        StackTraceElement element3 = new StackTraceElement("class3", "method3", "file3", 3);
        JSONObject parent = new JSONObject();
        JSONArray jsonStacktrace = TestUtils.createJsonStacktrace(new StackTraceElement[] { element1, element2, element3 });
        parent.put("stacktrace", jsonStacktrace);
        StackTraceElement[] elements = sut.deserializeStacktrace(parent);

        assertTrue(elements.length == 3);
        assertTrue(element1.equals(elements[0]));
        assertTrue(element2.equals(elements[1]));
        assertTrue(element3.equals(elements[2]));
    }

    @Test
    public void test_deserialize_full() {
        String level = "ERROR";
        String message = "This is a test";
        long time = 123445678;
        String bundle = "Dummy";
        String version = "1";
        String state = "RESOLVED";
        String exceptionMessage = "Dummy message";
        JSONObject jsonLogEntry = TestUtils.createJsonLoggingEvent(level, message, time, bundle, version, state);
        JSONObject jsonException = TestUtils.createJsonException(new IllegalArgumentException(exceptionMessage));
        StackTraceElement element1 = new StackTraceElement("class1", "method1", "file1", 1);
        StackTraceElement element2 = new StackTraceElement("class2", "method2", "file2", 2);
        JSONArray jsonStacktrace = TestUtils.createJsonStacktrace(new StackTraceElement[] { element1, element2 });
        jsonException.put("stacktrace", jsonStacktrace);
        jsonLogEntry.put("exception", jsonException);

        LoggingEvent event = sut.deserialize(jsonLogEntry);
        LocationInfo info = event.getLocationInformation();
        String className = "Bundle: " + bundle + "\n Class: " + element1.getClassName();
        Throwable exception = event.getThrowableInformation().getThrowable();
        StackTraceElement[] stacktrace = exception.getStackTrace();

        assertEquals(level, event.getLevel().toString());
        assertEquals(message, event.getMessage());
        assertEquals(time, event.getTimeStamp());
        assertEquals(className, event.getLoggerName());
        assertEquals(className, event.getNDC());
        assertEquals(className, event.getFQNOfLoggerClass());
        assertEquals(className, info.getClassName());
        assertEquals(element1.getFileName(), info.getFileName());
        assertEquals(String.valueOf(element1.getLineNumber()), info.getLineNumber());
        assertEquals(element1.getMethodName(), info.getMethodName());
        assertEquals(exceptionMessage, exception.getMessage());
        assertTrue(exception instanceof IllegalArgumentException);
        assertTrue(stacktrace.length == 2);
        assertTrue(element1.equals(stacktrace[0]));
        assertTrue(element2.equals(stacktrace[1]));
    }

    @Test
    public void test_deserialize_min() {
        String level = "ERROR";
        String message = "This is a test";
        long time = 123445678;
        String bundle = "Dummy";
        String version = "1";
        String state = "RESOLVED";
        JSONObject jsonLogEntry = TestUtils.createJsonLoggingEvent(level, message, time, bundle, version, state);

        LoggingEvent event = sut.deserialize(jsonLogEntry);
        LocationInfo info = event.getLocationInformation();
        String className = "Bundle: " + bundle;

        assertEquals(event.getLevel().toString(), level);
        assertEquals(event.getMessage(), message);
        assertEquals(event.getTimeStamp(), time);
        assertEquals(event.getLoggerName(), className);
        assertEquals(event.getNDC(), className);
        assertEquals(event.getFQNOfLoggerClass(), className);
        assertEquals(info.getClassName(), className);
        assertTrue(info.getFileName().isEmpty());
        assertTrue(info.getLineNumber().isEmpty());
        assertTrue(info.getMethodName().isEmpty());
    }

 
}
