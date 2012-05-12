package net.pwncraft.kaikz.fakeop;

public class FakeEntry {
    private String fakeCmd;
    private String message;
    private String invokeCmd;
    
    public FakeEntry(String s, String s2, String s3) {
        fakeCmd = s;
        message = s2;
        invokeCmd = s3;
    }
    
    public String getFakeCommand() {
        return fakeCmd;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getInvokeCommand() {
        return invokeCmd;
    }
}
