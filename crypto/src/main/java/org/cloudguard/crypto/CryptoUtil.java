package org.cloudguard.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class CryptoUtil {
    public static final int BUFFER_SIZE = 1048576;

    /**
     * Init java security to add BouncyCastle as a security provider
     */
    public static void init() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
