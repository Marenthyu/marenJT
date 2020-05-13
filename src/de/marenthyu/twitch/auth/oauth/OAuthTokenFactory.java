package de.marenthyu.twitch.auth.oauth;

import de.marenthyu.twitch.auth.Constants;
import de.marenthyu.twitch.auth.oauth.exceptions.CantCreateServerException;
import de.marenthyu.twitch.auth.oauth.exceptions.DesktopNotSupportedException;
import de.marenthyu.twitch.auth.oauth.exceptions.InvalidTwitchTokenException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class OAuthTokenFactory {
    private final String CLIENT_ID;
    private final String REDIRECT_URI;

    public OAuthTokenFactory(String CLIENT_ID, String redirectURI) {
        this.CLIENT_ID = CLIENT_ID;
        this.REDIRECT_URI = redirectURI;
    }

    public void askUserForOAuth(OAuthTokenFactoryCallback callback) {
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add("channel:read:redemptions");
        askUserForOAuth(callback, scopes);
    }

    public void askUserForOAuth(OAuthTokenFactoryCallback callback, ArrayList<String> scopes) {
        System.out.println("[TWITCH][AUTH]Requesting OAuth Token from user...");
        if (Desktop.isDesktopSupported()) {
            try {
                StringBuilder scopeString = new StringBuilder();
                boolean first = true;
                for (String scope:scopes) {
                    if (first) {
                        first = false;
                    } else {
                        scopeString.append("+");
                    }
                    scopeString.append(scope);
                }
                URI uri = new URI(
                        "https://id.twitch.tv/oauth2/authorize" +
                                "?client_id=" + CLIENT_ID +
                                "&redirect_uri=" + REDIRECT_URI +
                                "&response_type=token" +
                                "&scope=" + scopeString.toString());
                Desktop.getDesktop().browse(
                        uri
                );
                //System.out.println("Open browser... " + uri.toString());
                //System.out.println("Opening Socket to await token");
                ServerSocket server;
                try {
                    server = new ServerSocket(Constants.REDIRECT_PORT);
                } catch (IOException e) {
                    throw new CantCreateServerException(e);
                }
                new Thread(() -> {
                    System.out.println("[TWITCH][AUTH]Waiting for callback...");
                    try {
                        OAuthToken token = new OAuthToken(getAccesTokenFromCallback(server));
                        callback.callback(token);
                    } catch (IOException | InvalidTwitchTokenException e) {
                        // Getting an invalid token back shouldn't really be possible.
                        e.printStackTrace();
                    }
                }).start();

            } catch (IOException | URISyntaxException e) {
                // This has no reason to happen, fail quietly.
                e.printStackTrace();
            }
        } else {
            throw new DesktopNotSupportedException("[TWITCH][AUTH]Desktop seems to not be accessible, so the user can't be sent to log in.");
        }
    }

    private void respond(Socket s, String response, String headers) throws IOException {
        PrintWriter res = new PrintWriter(s.getOutputStream(), true);
        res.println(headers);
        if (headers.equals(Constants.OK_HEADERS)) {
            res.println();
            res.println(response);
        }
        res.close();
    }

    private String getAccesTokenFromCallback(ServerSocket server) throws IOException {
        boolean expectingRequests = true;
        String token = "";
        while (expectingRequests) {
            Socket s = server.accept();
            // System.out.println("Got a connection! Reading request...");
            BufferedReader request = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line = request.readLine();
            boolean isPOST = false, isGET = false;
            String requestPath = "";
            int contentLength = 0;
            if (line == null) {
                continue;
            }
            while ( !line.equals("")) {
                if (line.startsWith("GET ")) {
                    isGET = true;
                    requestPath = line.split(" ")[1];
                } else if (line.startsWith("POST ")) {
                    isPOST = true;
                    requestPath = line.split(" ")[1];
                } else if (line.startsWith("Content-Length: ")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }
                line = request.readLine();
            }
            if (isPOST) {
                StringBuilder postData = new StringBuilder();
                char[] buf = new char[contentLength];
                int read = request.read(buf, 0, contentLength);
                postData.append(buf);
                String postString = postData.toString();
                // System.out.println("Got all of the " + read + " post data: " + postString);

                if (postString.startsWith("#access_token=")) {
                    token = postString.split("&")[0].split("=")[1];
                    expectingRequests = false;
                } else {
                    System.out.println("[TWITCH][AUTH]Post data did not start with the expected value, waiting for more requests.");
                }
            }
            if ("/".equals(requestPath)) {
                if (isGET) {
                    respond(s, Constants.REDIRECTION_HTML, Constants.OK_HEADERS);
                    System.out.println("[TWITCH][AUTH]Response sent. Awaiting second request with post data...");
                } else {
                    respond(s, "", Constants.EMPTY_HEADERS);
                }
            } else {
                respond(s, "", Constants.GONE_HEADERS);
            }
            s.close();
        }
        System.out.println("[TWITCH][AUTH]Got token, continuing...");
        return token;
    }

    public static void main(String[] args) {
        // arg[0] is the client ID. meant for testing.
        OAuthTokenFactory test = new OAuthTokenFactory(args[0], "http://localhost:" + Constants.REDIRECT_PORT);
        try {
            test.askUserForOAuth(token -> System.out.println("Got user token: " + token));
        } catch (DesktopNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
