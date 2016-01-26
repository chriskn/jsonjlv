package com.github.chriskn.jsonjlv.prefrences;

import org.codehaus.jackson.annotate.JsonProperty;

public class PreferencesModel {

    @JsonProperty(value = "portin")
    private int incomingPort;

    @JsonProperty(value = "portout")
    private int outgoingPort;

    @JsonProperty(value = "ip")
    private String outgoingIp = ""; 

    @JsonProperty(value = "autostart")
    private boolean autoStart;

    public PreferencesModel() {

    }

    public int getIncomingPort() {
        return incomingPort;
    }

    public void setIncomingPort(int incomingPort) {
        this.incomingPort = incomingPort;
    }

    public int getOutgoingPort() {
        return outgoingPort;
    }

    public void setOutgoingPort(int outgoingPort) {
        this.outgoingPort = outgoingPort;
    }

    public String getOutgoingIp() {
        return outgoingIp;
    }

    public void setOutgoingIp(String outgoingIp) {
        this.outgoingIp = outgoingIp;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PreferencesModel [incomingPort=").append(incomingPort).append(", outgoingPort=")
                .append(outgoingPort).append(", outgoingIp=").append(outgoingIp).append(", autoStart=")
                .append(autoStart).append("]");
        return builder.toString();
    }

}
