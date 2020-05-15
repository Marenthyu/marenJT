package de.marenthyu.twitch.pubsub.bits;

import java.util.Date;

public class BitsEvent {
    Integer bitsUsed, totalBitsUsed, previousBadge, nextBadge;
    boolean isAnonymous;
    String userID, userName, channelID, chatMessage, context, messageID, messageType;
    Date time;

    public BitsEvent(Integer bitsUsed, Integer totalBitsUsed, Integer previousBadge, Integer nextBadge, boolean isAnonymous, String userID, String userName, String channelID, String chatMessage, String context, String messageID, String messageType, Date time) {
        this.bitsUsed = bitsUsed;
        this.totalBitsUsed = totalBitsUsed;
        this.previousBadge = previousBadge;
        this.nextBadge = nextBadge;
        this.isAnonymous = isAnonymous;
        this.userID = userID;
        this.userName = userName;
        this.channelID = channelID;
        this.chatMessage = chatMessage;
        this.context = context;
        this.messageID = messageID;
        this.messageType = messageType;
        this.time = time;
    }

    public Integer getBitsUsed() {
        return bitsUsed;
    }

    public Integer getTotalBitsUsed() {
        return totalBitsUsed;
    }

    public Integer getPreviousBadge() {
        return previousBadge;
    }

    public Integer getNextBadge() {
        return nextBadge;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public String getContext() {
        return context;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageType() {
        return messageType;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "BitsEvent{" +
                "bitsUsed=" + bitsUsed +
                ", userName='" + userName + '\'' +
                ", chatMessage='" + chatMessage + '\'' +
                ", time=" + time +
                '}';
    }
}
