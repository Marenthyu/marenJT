package de.marenthyu.twitch.auth.oauth.exceptions;

import java.io.IOException;

public class CantCreateServerException extends IOException {
    public CantCreateServerException(IOException cause) {
        super(cause);
    }
}
