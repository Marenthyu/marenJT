package de.marenthyu.twitch.pubsub.channelpoints;

import java.util.Date;

public class Redemption {
    String id, userID, userLogin, userDisplayName, channelId, input, status;
    Reward reward;
    Date redemptionTime;

    public String getId() {
        return id;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getInput() {
        return input;
    }

    public String getStatus() {
        return status;
    }

    public Reward getReward() {
        return reward;
    }

    public Date getRedemptionTime() {
        return redemptionTime;
    }

    public Redemption(String id, String userID, String userLogin, String userDisplayName, String channelId, String input, String status, Reward reward, Date redemptionTime) {
        this.id = id;
        this.userID = userID;
        this.userLogin = userLogin;
        this.userDisplayName = userDisplayName;
        this.channelId = channelId;
        this.input = input;
        this.status = status;
        this.reward = reward;
        this.redemptionTime = redemptionTime;
    }
}
