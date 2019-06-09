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
            String aliceKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn0XjGCBqN4jvOB/wa2DK4FKm9DVY4Q+sH+ySNKkvwA0QVZ/zsmU1FwnUoBoRsgL3LwJLSevD73EEwhWbzbinA1AphW45pWka+JBDhJYL5dnHE2e9auL4Hy7oCvGywl52hpnd0vYPQu7uwuM5dZR2uB2w8jzqaZoTssEYC598Wr0DxigNKGsR6Wu5/9KTUtU4wYZlORKni8w6kJLO0KfKVPq8iA4GNsQXP6W1uz4U6r1q3pWPANXJRblFqjNKFRnnYzgw605lFKxhpY6WUxX06xsgdpNPBRjqtHBDv+t0IdUu5Mh+Ocf5D9XULNb723+E15wHrYaS64F5Ns6cOpkjTwIDAQAB";
            String bobKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiUyC4n38R9S6VUY8qWxi+/twgLkZOtB2QTPu199Sk2jdra0F9KdHiv3Olg4IAc56flyKUYhBGm4SD1osjNNHHWjPK2X/nCPEiIczG2a2CD1C3RaMZa1dc72cgLK/ssRrugLHF0Tw4FtPSRs3MGdwXcf2LGsoVQpPIe1YlgfFTUbWCdrQ3UOW8pLUduacrrcEAZScHPBVXqBidDO3w5RRVn5J3qff/7p8OgeICRpaNc48T8/ZWEzHNUx9IXzPZshrBKyi7nhh5Rk6KTZGB/L+8a8LXcknVqbbwntIyFBN2+09aMy1LFQ4Pyy4V3IR88A7nOn4tE1PgwIvm6mme4d7jwIDAQAB";
            String charlesKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnSOsxw80mBE2ZjSiTUye0ehnc6RSJiwNPYiEzd8KWqw/BNmkxlu26l1EPppzFsm9OLDYFSoF/d7O8kJbDb4BHl8k1cETrHcTnzISarjyoIl3aiA91RYpmNi6OB6z4JW2m7iTYHTFu0H9dyJRWaOAuDXeyKfRzX2ykFRYNr2ebDWOZA6ZJL6iOICCmlE5itR1cQuUQxMwiehcpK/e/vZ0H9I4POhqxgb5LJkVw5P8TD8UeRe08s2CX6MveTsrBjBEkxoDcCyrMAfWjD6Ec2kGrOdjcedZRO3Uw38vXRXmldmOE5Dtxnc293aYrzXFMm5ZsyHtiEyz7ofA5lJz/2wyOwIDAQAB";
            String spamKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0b2DduYDFNyKcysDKDY3RRUB1bNs+fBdtzwDYJZhVH0eeFnpEmE3MJvJ2klcCRCeKhqziqK3Bfnx9psKull5nEOPi3dDiW54aHfw91rD05rIHa7dLyERvwevdqr8DOCM5GfkSgDgRxs4etIkrkiF+oujf97B7ivYvWgO2/6hBxOuviedk98Q01HyHNOnmaOtax/3mkM8iNV8fykLyiPTU3XNeCJmka7j9Z+mPvsbf9ZcNSrmeLrZg4jyMT+8lign2APK7QXiY5t5G4LsMhMW6Q4sOe8Dn6aNrNpcQkSLe/JMWk76LVUpDBS6k4gJc/bM5p7OJZn6sUJ5c0QQYgqaQIDAQAB";
            contacts.put(aliceKey,"Alice");
            contacts.put(bobKey,"Bob");
            contacts.put(charlesKey,"Charles");
            contacts.put(spamKey,spamKey.substring(48, 60));
            System.out.println("FakeFill contacts ");
            Speech aliceOne = new Speech(0, aliceKey, contacts.get(aliceKey), "Hello this is alice", 123);
            aliceOne.Destination(selfKey);
            aliceOne.Verify(true, true);
            Speech aliceTwo = new Speech(100, selfKey, contacts.get(aliceKey), "Hi alice, i am client", 126);
            aliceTwo.Destination(aliceKey);
            aliceTwo.Verify(true, false);
            Speech aliceThree = new Speech(1, aliceKey, contacts.get(aliceKey), "Cool", 127);
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
