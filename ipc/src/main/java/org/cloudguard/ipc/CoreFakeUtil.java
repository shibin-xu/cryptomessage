package org.cloudguard.ipc;

import java.util.*;
import java.security.*;

import static org.cloudguard.crypto.RSAEncryptUtil.getKeyAsString;

public class CoreFakeUtil {

        
    public static void FakeFill(Map<String, String> contacts, List<Speech> archive, PublicKey publicKey) {
        try {  
            contacts.clear();
            archive.clear();
            String selfKey = getKeyAsString(publicKey);
            String aliceKey = "ab324b324b12b41";
            String bobKey = "b543b6b324b1331";
            String charlesKey = "c908c8624b11232";
            String spamKey = "dd45124b11232da";
            contacts.put(aliceKey,"Alice");
            contacts.put(bobKey,"Bob");
            contacts.put(charlesKey,"Charles");
            contacts.put(spamKey,spamKey);
            System.out.println("FakeFill contacts ");
            Speech aliceOne = new Speech(0, aliceKey, contacts.get(aliceKey), "Hello this is alice", 123);
            aliceOne.Destination(selfKey);
            aliceOne.Verify(true, true);
            Speech aliceTwo = new Speech(1, selfKey, contacts.get(aliceKey), "Hi alice, i am client", 126);
            aliceTwo.Destination(aliceKey);
            aliceTwo.Verify(true, false);
            Speech aliceThree = new Speech(2, aliceKey, contacts.get(aliceKey), "Cool", 127);
            aliceThree.Destination(selfKey);
            aliceThree.Verify(true, false);
            archive.add(aliceOne);
            archive.add(aliceTwo);
            archive.add(aliceThree);
            Speech charlesOne = new Speech(0, charlesKey, contacts.get(charlesKey), "Hello this is charles", 123);
            charlesOne.Destination(selfKey);
            charlesOne.Verify(true, true);
            archive.add(charlesOne);
            
            Speech spamOne = new Speech(0, spamKey, contacts.get(spamKey), "spaaam", 111);
            spamOne.Destination(selfKey);
            spamOne.Verify(false, false);
            archive.add(spamOne);
            System.out.println("FakeFill archive ");
        } catch(Exception e) {
            System.out.println("e fake: " + e);
        }
        System.out.println("FakeFill done ");
    }
    
    public static Speech FakeRecvSpeech(String senderKey, String senderBody, 
    PublicKey publicKey, Map<String, String> contacts, Date date) {

        try {
            String publicKeyString = getKeyAsString(publicKey);
            String speechBody = "Body: "+senderBody;
            long speechTime = date.getTime();

            String senderKeyString = senderKey;
            if(!contacts.containsKey(publicKeyString))
            {
                String[] allKeys = new String[contacts.keySet().size()];
                contacts.keySet().toArray(allKeys);
                senderKeyString = allKeys[new Random().nextInt(allKeys.length)];
            }

            String senderAlias = contacts.get(senderKeyString);

            Speech speech = new Speech(1, senderKeyString, senderAlias, speechBody, speechTime);
            speech.Destination(publicKeyString);
            speech.Verify(true, false);
            return speech;
            
        } catch (Exception e) {

            System.out.println("e6 = " + e);
        }
        return null;
    }
}
