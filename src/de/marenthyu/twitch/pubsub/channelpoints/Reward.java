package de.marenthyu.twitch.pubsub.channelpoints;

public class Reward {
    String id, channelID, title, prompt, backgroundColor;
    int cost, maxPerStream;
    boolean userInputRequired, subOnly, enabled, paused, inStock, limitedPerStream, skipsRedemptionRequestQueue;

    public String getId() {
        return id;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getTitle() {
        return title;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public int getCost() {
        return cost;
    }

    public boolean isUserInputRequired() {
        return userInputRequired;
    }

    public boolean isSubOnly() {
        return subOnly;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isInStock() {
        return inStock;
    }

    public int getMaxPerStream() {
        return maxPerStream;
    }

    public boolean isLimitedPerStream() {
        return limitedPerStream;
    }

    public boolean skipsRedemptionRequestQueue() {
        return skipsRedemptionRequestQueue;
    }

    public Reward(String id, String channelID, String title, String prompt, String backgroundColor, int cost, boolean userInputRequired, boolean subOnly, boolean enabled, boolean paused, boolean inStock, boolean limitedPerStream, int maxPerStream, boolean skipsRedemptionRequestQueue) {
        this.id = id;
        this.channelID = channelID;
        this.title = title;
        this.prompt = prompt;
        this.backgroundColor = backgroundColor;
        this.cost = cost;
        this.userInputRequired = userInputRequired;
        this.subOnly = subOnly;
        this.enabled = enabled;
        this.paused = paused;
        this.inStock = inStock;
        this.limitedPerStream = limitedPerStream;
        this.maxPerStream = maxPerStream;
        this.skipsRedemptionRequestQueue = skipsRedemptionRequestQueue;
    }
}
