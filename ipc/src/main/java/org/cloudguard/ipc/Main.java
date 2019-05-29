package org.cloudguard.ipc;

import java.util.Date;

public class Main {
    public static void main(String[] args) throws Exception {
        Date date = new Date();

        System.out.println(date);
        System.out.println(date.getTime());
        System.out.println(new Date(date.getTime()));
    }
}
