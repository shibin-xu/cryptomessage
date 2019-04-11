package org.cloudvault.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CookieConsumer extends Thread {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CookieConsumer.class);
    private static final int MINUTE = 60000;

    private ConcurrentMap<String, String> cookies;

    public CookieConsumer(ConcurrentMap<String, String> cookies) {
        this.cookies = cookies;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ConcurrentMap<String, String> oldCookies = new ConcurrentHashMap<String, String>();
                for (String cookie : cookies.keySet()) {
                    oldCookies.put(cookie, cookies.get(cookie));
                }

                Thread.sleep(60 * MINUTE);

                for (String cookie : oldCookies.keySet()) {
                    logger.info("Remove cookie " + cookie + "\n\n");
                    cookies.remove(cookie);
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }

        }
    }
}
