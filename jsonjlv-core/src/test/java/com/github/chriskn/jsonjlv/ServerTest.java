package com.github.chriskn.jsonjlv;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;

public class ServerTest {

    Server sut;
    int incomingPort; 
    int outgoingPort; 
    String ip; 

    @Before
    public void setUp() throws Exception {
        ip = "127.0.0.1";
        incomingPort = TestUtils.getAvailablePort(); 
        outgoingPort = TestUtils.getAvailablePort(incomingPort); 
        sut = new Server(incomingPort, outgoingPort, ip); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createServer_invalidIncomingPort() throws Exception {
        new Server(-1, 10, "127.0.0.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createServer_invalidOutgoingPort() throws Exception {
        new Server(4445, -12, "127.0.0.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createServer_invalidIp_1() throws Exception {
        new Server(4445, 1224, "127.0.0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createServer_invalidIp_2() throws Exception {
        new Server(4445, 1224, "abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createServer_invalidIp_3() throws Exception {
        new Server(4445, 1224, "256.0.0.0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_createServer_invalidIp_4() throws Exception {
        new Server(4445, 1224, "255.0.0.00-1");
    }
    
    @Test(expected = IOException.class)
    public void test_createServer_blockedPort() throws Exception {
        new Server(TestUtils.getBlockedPort(), 1224, "127.0.0.1");
    }
    
    @Test
    public void test_initialisation(){
        assertTrue(sut.serverSocket.isBound());
        assertEquals(incomingPort,sut.serverSocket.getLocalPort());
    }
    
    @Test
    public void test_shutdown() throws InterruptedException{
        sut.start();
        sut.shutdown();
        Thread.sleep(1000);
        assertFalse(sut.isAlive());
        assertTrue(sut.executor.isShutdown()); 
    }
    
    @Test
    public void test_connectToServer() throws UnknownHostException, IOException, InterruptedException{
        sut.start();
        TestSender sender = new TestSender(ip,incomingPort); 
        sender.sendJson();
        Thread.sleep(2000);
        assertNotNull(sut.handler);
        assertEquals(ip, sut.handler.host);
        assertEquals(outgoingPort, sut.handler.port);
    }
    
    @After
    public void tearDown(){
        sut.shutdown();
    }
    

  
}
