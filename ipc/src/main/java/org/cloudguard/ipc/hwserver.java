package org.cloudguard.ipc;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class hwserver
{

    public static void main(String[] args) throws Exception
    {
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                byte[] raw = socket.recv(0);


                String rawString = new String(raw, ZMQ.CHARSET);

                // Print the message
                System.out.println(
                        "Received: " + rawString
                );

                // Send a response
                String response0 = "[{\"cmd\":\"jsUnknown\",\"payload\":\"unknown\"}]";
                String response1 = "[{\"cmd\":\"jsConnected\",\"payload\":\"good\"}]";
                String response2 = "[{\"cmd\":\"jsDisconnected\",\"payload\":\"bad\"}]";
                String response3 = "[{\"cmd\":\"jsMsg\",\"payload\":\"Hello someone\"}]";
                if(rawString.charAt(10) == 'C')
                    socket.send(response1.getBytes(ZMQ.CHARSET), 0);
                else if(rawString.charAt(10) == 'D')
                    socket.send(response2.getBytes(ZMQ.CHARSET), 0);
                else if(rawString.charAt(10) == 'S')
                    socket.send(response3.getBytes(ZMQ.CHARSET), 0);
                else
                    socket.send(response0.getBytes(ZMQ.CHARSET), 0);

            }
        }
    }
}