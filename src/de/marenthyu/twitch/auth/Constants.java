package de.marenthyu.twitch.auth;

public class Constants {
    public static final String REDIRECTION_HTML = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "    <head>\n" +
            "        <meta charset=\"UTF-8\">\n" +
            "        <title>OAuth local redirection...</title>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        Hi, you shouldn't see this for long.\n" +
            "        <noscript>\n" +
            "            <h1>Please enable javascript! I need to redirect the oauth token to the application please :(</h1>\n" +
            "        </noscript>\n" +
            "        <script lang=\"javascript\">\n" +
            "                let req = new XMLHttpRequest();\n" +
            "                req.open('POST', '/', false);\n" +
            "                req.setRequestHeader('Content-Type', 'text');\n" +
            "                req.send(document.location.hash);\n" +
            "                console.log(\"response headers: \" + req.getAllResponseHeaders());\n" +
            "                console.log(\"I guess i can close now?\");\n" +
            "                window.close();\n" +
            "        </script>\n" +
            "    </body>\n" +
            "</html>\n";
    public static final int REDIRECT_PORT = 5497;
    public static final String OK_HEADERS = "HTTP/1.0 200 OK\r\n" +
            "Host: localhost\r\n+" +
            "Server: Marenthyu Oauth Token Factory\r\n" +
            "Content-Type: text/html";
    public static final String GONE_HEADERS = "HTTP/1.0 410 Gone\r\n" +
            "Host: localhost\r\n+" +
            "Server: Marenthyu Oauth Token Factory";
    public static final String EMPTY_HEADERS = "HTTP/1.0 204 No Content\r\n" +
            "Host: localhost\r\n+" +
            "Server: Marenthyu Oauth Token Factory";

}
