package org.cloudguard.server;

import org.cloudguard.commons.ClientUtil;
import org.cloudguard.commons.Response;

import java.io.IOException;
import java.util.Scanner;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        System.out.println("ServerTest");

        String email = prompt("Please enter your email: ");
        String password = prompt("Please enter your password: ");

        Response response = ClientUtil.login(email, password);
        System.out.println(response);
    }

    private static String prompt(String message) {
        Scanner scanIn = new Scanner(System.in);
        System.out.print(message);
        return scanIn.nextLine();
    }
}
