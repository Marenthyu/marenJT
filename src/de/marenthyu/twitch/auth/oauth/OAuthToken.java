package de.marenthyu.twitch.auth.oauth;

import de.marenthyu.twitch.auth.oauth.exceptions.InvalidTwitchTokenException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class OAuthToken implements Serializable {
    private String oAuthString;
    private String userID;
    private String refreshToken;
    private Date expirationDate;
    private ArrayList<String> scopes;
    private final static String VALIDATE_URL = "https://id.twitch.tv/oauth2/validate";

    public OAuthToken(String oAuthString, String userID, String refreshToken, Date expirationDate, ArrayList<String> scopes) {
        this.oAuthString = oAuthString;
        this.userID = userID;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
        this.scopes = scopes;
    }

    public OAuthToken(String oAuthString) throws InvalidTwitchTokenException {
        this.oAuthString = oAuthString;
        // contact API to get to know more

        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(VALIDATE_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "OAuth " + oAuthString);
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.err.println("[TWITCH][AUTH] Token was not valid, got " + responseCode + " code as response!");
                throw new InvalidTwitchTokenException();
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            try {
                JSONObject jsonResponse = new JSONObject(response.toString());

                // System.out.println("Got OAuth Token Twitch Info: " + response.toString());
                this.userID = jsonResponse.getString("user_id");
                this.expirationDate = new Date();
                this.expirationDate.setTime(this.expirationDate.getTime() + (jsonResponse.getLong("expires_in") * 1000));
                JSONArray newScopes = jsonResponse.getJSONArray("scopes");
                this.scopes = new ArrayList<String>();
                for (Object scope : newScopes) {
                    this.scopes.add((String) scope);
                }
                System.out.println("[TWITCH][AUTH] Token Info updated!");

            } catch (JSONException e) {
                System.err.println("[TWITCH][AUTH] Got invalid JSON response. Assuming bad token.");
                throw new InvalidTwitchTokenException();
            }

        } catch (IOException e) {
            // This shouldn't happen, ever. Assume that we are offline and just accept that we can't check that info.
            e.printStackTrace();
            return;
        }
    }

    public String getOAuthString() {
        return oAuthString;
    }

    public void setOAuthString(String oAuthString) {
        this.oAuthString = oAuthString;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public ArrayList<String> getScopes() {
        return scopes;
    }

    public void setScopes(ArrayList<String> scopes) {
        this.scopes = scopes;
    }

    public boolean isValid() {
        Date now = new Date();
        return now.before(this.expirationDate);
    }

    @Override
    public String toString() {
        return "OAuthToken{" +
                "oAuthString='" + "xxxxx[redacted]" + '\'' +
                ", userID='" + userID + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expirationDate=" + expirationDate +
                ", scopes=" + scopes.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OAuthToken)) return false;

        OAuthToken that = (OAuthToken) o;

        return getOAuthString() != null ? getOAuthString().equals(that.getOAuthString()) : that.getOAuthString() == null;
    }

    @Override
    public int hashCode() {
        return getOAuthString() != null ? getOAuthString().hashCode() : 0;
    }

}
