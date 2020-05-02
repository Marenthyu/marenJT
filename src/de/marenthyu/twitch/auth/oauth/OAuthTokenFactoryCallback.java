package de.marenthyu.twitch.auth.oauth;

public interface OAuthTokenFactoryCallback {
    void callback(OAuthToken token);
}
