package de.marenthyu.twitch.pubsub.subscription;

import java.util.Date;

public class SubscriptionEvent {
    // Optional, null if an anon gift
    String userName, userID, displayName;
    // Always Set
    String channelName, channelID, subPlan, subPlanName, context, subMessage;
    Integer cumulativeMonths;
    Date time;
    // Optional.
    Integer streakMonths;
    // If a gift, these are set, else null
    String recipientID, recipientName, recipientDisplayName;

    public SubscriptionEvent(String userName, String userID, String displayName, String channelName, String channelID, String subPlan, String subPlanName, String context, String subMessage, Integer cumulativeMonths, Integer streakMonths, Date time, String recipientID, String recipientName, String recipientDisplayName) {
        this.userName = userName;
        this.userID = userID;
        this.displayName = displayName;
        this.channelName = channelName;
        this.channelID = channelID;
        this.subPlan = subPlan;
        this.subPlanName = subPlanName;
        this.context = context;
        this.subMessage = subMessage;
        this.cumulativeMonths = cumulativeMonths;
        this.streakMonths = streakMonths;
        this.time = time;
        this.recipientID = recipientID;
        this.recipientName = recipientName;
        this.recipientDisplayName = recipientDisplayName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getSubPlan() {
        return subPlan;
    }

    public String getSubPlanName() {
        return subPlanName;
    }

    public String getContext() {
        return context;
    }

    public String getSubMessage() {
        return subMessage;
    }

    public Date getTime() {
        return time;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getRecipientDisplayName() {
        return recipientDisplayName;
    }

    public Integer getCumulativeMonths() {
        return cumulativeMonths;
    }

    public Integer getStreakMonths() {
        return streakMonths;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SubscriptionEvent{");
        switch (this.context) {
            case "sub": {
                sb.append("Subscription by ");
                break;
            } case "resub": {
                sb.append("Resub by ");
                break;
            } case "subgift": {
                sb.append("Sub Gift from ");
                sb.append(this.displayName);
                sb.append(" to ");
                break;
            }
            case "anonsubgift": {
                sb.append("Anonymous Sub Gift to ");
                break;
            }
            default: {
                sb.append("Unknown Sub Type - ");
            }
        }
        switch (this.context) {
            case "sub":
            case "resub":
            {
                sb.append(this.displayName);
                break;
            }
            case "subgift":
            case "anonsubgift":
            {
                sb.append(this.recipientDisplayName);
                break;
            }
        }
        sb.append(" for ").append(this.streakMonths).append(" months, ").append(this.cumulativeMonths).append(" month cumulative.");
        sb.append(" Other Data: channelName='").append(channelName).append('\'');
        sb.append(", channelID='").append(channelID).append('\'');
        sb.append(", subPlan='").append(subPlan).append('\'');
        sb.append(", subPlanName='").append(subPlanName).append('\'');
        sb.append(", subMessage='").append(subMessage).append('\'');
        sb.append(", time=").append(time);
        sb.append('}');
        return sb.toString();
    }
}
