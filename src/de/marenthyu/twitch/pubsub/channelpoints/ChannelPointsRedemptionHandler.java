package de.marenthyu.twitch.pubsub.channelpoints;

public abstract class ChannelPointsRedemptionHandler {
    public String beginningPattern;
    public ChannelPointsRedemptionHandler(String beginningPattern) {
        this.beginningPattern = beginningPattern;
    }
    abstract public void matched(String input, String prompt);
}
