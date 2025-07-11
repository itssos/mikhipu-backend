package pe.getsemani.mikhipu.notifications.dto;

public class PushSubscriptionRequest {
    public int userId;
    public Subscription subscription;

    public static class Subscription {
        public String endpoint;
        public Keys keys;

        public static class Keys {
            public String p256dh;
            public String auth;
        }
    }
}