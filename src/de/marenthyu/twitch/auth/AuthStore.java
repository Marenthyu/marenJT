package de.marenthyu.twitch.auth;

import de.marenthyu.twitch.auth.oauth.OAuthToken;
import de.marenthyu.twitch.auth.oauth.OAuthTokenFactory;
import de.marenthyu.twitch.auth.oauth.exceptions.InvalidTwitchTokenException;

import java.io.*;
import java.nio.file.Paths;

public class AuthStore {
    private static OAuthToken lastToken;
    private static boolean initialized = false;
    private static OAuthTokenFactory tokenFactory;
    private static final String saveFileName = Paths.get(System.getProperty("user.dir"), "TwitchOAuthToken.ser").toAbsolutePath().toString();

    public static void init(String clientID) {
        if (initialized) {
            return;
        }

        boolean needNewToken = true;
        tokenFactory = new OAuthTokenFactory(clientID, "http://localhost:" + Constants.REDIRECT_PORT);
        try {
            FileInputStream fis = new FileInputStream(saveFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            System.out.println("[TWITCH][AUTH] Got token from disk, validating it");
            try {
                lastToken = new OAuthToken(((OAuthToken) ois.readObject()).getOAuthString());
                needNewToken = !lastToken.isValid();
            } catch (InvalidTwitchTokenException e) {
                System.err.println("[TWITCH][AUTH] Token was invalid, requesting new Token.");
                needNewToken = true;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[TWITCH][AUTH] No token found on disk, requesting new one.");
        }
        if (needNewToken) {
            tokenFactory.askUserForOAuth((token) -> {
                AuthStore.lastToken = token;
                AuthStore.initialized = true;
                AuthStore.writeLastTokenToDisk();
            });
        } else {
            AuthStore.initialized = true;
            AuthStore.writeLastTokenToDisk();
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void writeLastTokenToDisk() {
        try {
            FileOutputStream fout = new FileOutputStream(saveFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(lastToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static OAuthToken getToken() {
        return lastToken;
    }
}
