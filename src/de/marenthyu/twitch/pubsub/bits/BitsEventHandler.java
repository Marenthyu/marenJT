package de.marenthyu.twitch.pubsub.bits;

public abstract class BitsEventHandler {
    public int minimumAmount = 1, maximumAmount = Integer.MAX_VALUE;
    public BitsEventHandler(int minimumAmount) {
        this.minimumAmount = minimumAmount;
    }
    public BitsEventHandler(int minimumAmount, int maximumAmount) {
        this.maximumAmount = maximumAmount;
        this.minimumAmount = minimumAmount;
    }
    public BitsEventHandler() {

    }
    abstract public void matched(BitsEvent event);
}
