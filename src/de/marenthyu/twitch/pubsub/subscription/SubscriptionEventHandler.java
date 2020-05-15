package de.marenthyu.twitch.pubsub.subscription;

import de.marenthyu.twitch.pubsub.bits.BitsEvent;

public abstract class SubscriptionEventHandler {
    public int minimumAmount = 1, maximumAmount = Integer.MAX_VALUE;
    public boolean usesStreak = false;
    public boolean onlyGifts = false;

    public SubscriptionEventHandler(boolean onlyGifts) {
        this.onlyGifts = onlyGifts;
    }
    public SubscriptionEventHandler(int minimumAmount) {
        this.minimumAmount = minimumAmount;
        this.usesStreak = false;
    }
    public SubscriptionEventHandler(int minimumAmount, boolean usesStreak) {
        this.minimumAmount = minimumAmount;
        this.usesStreak = usesStreak;
    }
    public SubscriptionEventHandler(int minimumAmount, int maximumAmount) {
        this.maximumAmount = maximumAmount;
        this.minimumAmount = minimumAmount;
        this.usesStreak = false;
    }
    public SubscriptionEventHandler(int minimumAmount, int maximumAmount, boolean usesStreak) {
        this.maximumAmount = maximumAmount;
        this.minimumAmount = minimumAmount;
        this.usesStreak = usesStreak;
    }
    public SubscriptionEventHandler() {

    }
    abstract public void matched(SubscriptionEvent event);
}
