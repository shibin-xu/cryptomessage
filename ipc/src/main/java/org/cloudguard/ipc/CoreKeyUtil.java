package org.cloudguard.ipc;

import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.security.spec.InvalidKeySpecException;



public class CoreKeyUtil {

    public static PrivateKey GetPrivateKey(String fileName) throws 
        IOException, 
        NoSuchAlgorithmException,
        InvalidKeySpecException
    {
        byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
        if(keyBytes.length < 80)
        {
            return null;
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey GetPublicKey(String fileName) throws 
        IOException, 
        NoSuchAlgorithmException,
        InvalidKeySpecException
    {
        byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
        if(keyBytes.length < 80)
        {
            return null;
        }

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
    public static PublicKey GetPublicKeyFromText(String text) throws 
        NoSuchAlgorithmException,
        InvalidKeySpecException
    {
        byte[] keyBytes = Base64.decodeBase64(text);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static void SavePublicKey(PublicKey publicKey, String filename) throws IOException
    {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(filename);
        if(fos != null)
        {
            fos.write(x509EncodedKeySpec.getEncoded());
            fos.close();
        }
    }
    public static void SavePrivateKey(PrivateKey privateKey, String filename) throws IOException
    {    
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(filename);
        if(fos != null)
        {
            fos.write(pkcs8EncodedKeySpec.getEncoded());
            fos.close();
        }
    }
}
