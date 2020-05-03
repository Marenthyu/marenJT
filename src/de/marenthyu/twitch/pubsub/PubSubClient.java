package de.marenthyu.twitch.pubsub;

import de.marenthyu.twitch.auth.oauth.OAuthToken;
import de.marenthyu.twitch.pubsub.channelpoints.ChannelPointsRedemptionHandler;
import de.marenthyu.twitch.pubsub.channelpoints.Redemption;
import de.marenthyu.twitch.pubsub.channelpoints.Reward;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PubSubClient extends WebSocketClient {
    private final OAuthToken oauth;
    private static final String PUBSUB_URL = "wss://pubsub-edge.twitch.tv";
    private final ArrayList<String> topics;
    private boolean pongReceived;
    private final ArrayList<ChannelPointsRedemptionHandler> channelPointsRedemptionHandlers = new ArrayList<>();

    public PubSubClient(OAuthToken oauth, ArrayList<String> topics) throws URISyntaxException {
        super(new URI(PUBSUB_URL));
        this.oauth = oauth;
        this.topics = topics;
        this.connect();
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[TWITCH][PUBSUB][PING] Connected to PubSub. Sending PING");
        this.ping();
        for (String topic : topics) {
            this.listenToTopic(topic);
        }
    }

    private void ping() {
        this.pongReceived = false;
        this.send("{\"type\":\"PING\"}");
        System.out.println("[TWITCH][PUBSUB][PING] PING!");
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(11);
            } catch (InterruptedException e) {
                System.out.println("[TWITCH][PUBSUB][PING] PONG Timeout timer got interrupted");
                e.printStackTrace();
            }
            if (!pongReceived) {
                System.err.println("[TWITCH][PUBSUB][PING] PONG not received in time, reconnecting...");
                reconnect();
            }
        }).start();
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONObject msg = new JSONObject(message);
            String type = msg.getString("type");
            switch (type) {
                case "PONG": {
                    System.out.println("[TWITCH][PUBSUB][PING] PONG!");
                    pongReceived = true;
                    new Thread(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(250 + (int) (Math.random() * 5));
                        } catch (InterruptedException e) {
                            System.out.println("[TWITCH][PUBSUB][PING] PING Timeout timer got interrupted");
                            e.printStackTrace();
                        }
                        ping();
                    }).start();
                    break;
                }
                case "RECONNECT": {
                    System.out.println("[TWITCH][PUBSUB][RECONNECT] Server RECONNECT received, reconnecting...");
                    this.reconnect();
                    break;
                }
                case "MESSAGE": {
                    JSONObject data = msg.getJSONObject("data");
                    String dataMessage = data.getString("message");
                    String topic = data.getString("topic");
                    // System.out.println("[TWITCH][PUBSUB][MESSAGE][" + topic + "] " + dataMessage);
                    if (topic.startsWith("channel-points")) {
                        handleChannelPointsRedemption(dataMessage);
                    }
                    break;
                }
                case "RESPONSE": {
                    String error = msg.getString("error");
                    String nonce = msg.getString("nonce");
                    if (error.equals("")) {
                        System.out.println("[TWITCH][PUBSUB][RESPONSE] " + nonce);
                    } else {
                        System.err.println("[TWITCH][PUBSUB][RESPONSE] ERROR listening to topic: " + error + " - NONE: " + nonce);
                    }
                    break;
                }
                default: {
                    System.err.println("[TWITCH][PUBSUB][UNKNOWN] Unknown type received - handle it, dummy! -> " + msg.toString());
                }
            }
        } catch (JSONException e) {
            System.err.println("[TWITCH][PUBSUB] Error parsing WebSocket Response as json. Terminating.");
            e.printStackTrace();
            this.close();
        }
    }

    private void handleChannelPointsRedemption(String dataMessage) {
        JSONObject jsonMessage = new JSONObject(dataMessage);
        String type = jsonMessage.getString("type");
        if (!type.equals("reward-redeemed")) {
            System.err.println("[TWITCH][PUBSUB][MESSAGE] Unknown channel points type '" + type + "'");
        } else {
            JSONObject data = jsonMessage.getJSONObject("data");
            JSONObject redemption = data.getJSONObject("redemption");
            String user = redemption.getJSONObject("user").getString("display_name");
            JSONObject reward = redemption.getJSONObject("reward");
            String title = reward.getString("title");
            String prompt = reward.getString("prompt");
            //System.out.println("[TWITCH][PUBSUB][DEBUG] " + reward.toString());
            String input = "";
            if (redemption.keySet().contains("user_input")) {
                input = redemption.getString("user_input");
            }
            System.out.println(String.format("[TWITCH][PUBSUB][MESSAGE] %s redeemed '%s' with Input '%s'", user, title, input));
            Reward rewardObj = new Reward(reward.getString("id"), reward.getString("channel_id"), reward.getString("title"),
                    reward.getString("prompt"), reward.getString("background_color"), reward.getInt("cost"),
                    reward.getBoolean("is_user_input_required"), reward.getBoolean("is_sub_only"), reward.getBoolean("is_enabled"),
                    reward.getBoolean("is_paused"), reward.getBoolean("is_in_stock"), reward.getJSONObject("max_per_stream").getBoolean("is_enabled"),
                    reward.getJSONObject("max_per_stream").getInt("max_per_stream"), reward.getBoolean("should_redemptions_skip_request_queue"));
            Redemption redemptionObj;
            try {
                redemptionObj = new Redemption(redemption.getString("id"), redemption.getJSONObject("user").getString("id"),
                        redemption.getJSONObject("user").getString("login"), redemption.getJSONObject("user").getString("display_name"),
                        redemption.getString("channel_id"), reward.getBoolean("is_user_input_required") ? redemption.getString("user_input") : "", redemption.getString("status"), rewardObj,
                        Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(redemption.getString("redeemed_at")))));
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("[TWITCH][PUBSUB] Twitch sent an invalid timestamp. The crap. This shouldn't happen.");
                return;
            }
            for (ChannelPointsRedemptionHandler h : channelPointsRedemptionHandlers) {
                if (title.startsWith(h.beginningPattern) || prompt.startsWith(h.beginningPattern)) {
                    new Thread(() -> h.matched(redemptionObj)).start();
                }
            }
        }

    }

    public void addChannelPointsRedemptionHandler(ChannelPointsRedemptionHandler handler) {
        channelPointsRedemptionHandlers.add(handler);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[TWITCH][PUBSUB] PubSub Connection closed. Here's info:");
        System.out.println(String.format("[TWITCH][PUBSUB] code: %d, reason: %s, remote: %b", code, reason, remote));
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void listenToTopic(String topic) {
        JSONObject request = new JSONObject();
        request.put("type", "LISTEN");
        JSONObject data = new JSONObject();
        data.put("auth_token", this.oauth.getOAuthString());
        JSONArray topics = new JSONArray();
        topics.put(topic);
        data.put("topics", topics);
        request.put("data", data);
        System.out.println("[TWITCH][PUBSUB][LISTEN] " + topic);
        this.send(request.toString());
    }
}
