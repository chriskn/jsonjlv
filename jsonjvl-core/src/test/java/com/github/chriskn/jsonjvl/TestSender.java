package com.github.chriskn.jsonjvl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class TestSender {

    private String ip; 
    private int port; 
    
    public TestSender(String ip,int port) throws UnknownHostException, IOException{
       this.ip = ip; 
       this.port = port; 
    }
    
    public void sendJson() throws IOException{
        Socket sender = new Socket(ip,port); 
        DataOutputStream stream = new DataOutputStream(sender.getOutputStream());
        byte[] data = TestUtils.TEST_JSON_STRING.getBytes(StandardCharsets.UTF_8); 
        System.out.println(data.length);
        stream.writeInt(data.length);
        stream.flush();
        stream.write(data);
        stream.flush();
        sender.close();
    }
    
    
    
}
