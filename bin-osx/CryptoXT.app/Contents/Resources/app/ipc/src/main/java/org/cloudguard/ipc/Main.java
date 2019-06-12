package org.cloudguard.ipc;


public class Main {
    
    protected static Core core;

    public static void main(String[] args) throws Exception {
        core = new Core();
        core.Start();
    }
}
