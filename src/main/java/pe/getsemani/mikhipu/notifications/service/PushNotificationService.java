package pe.getsemani.mikhipu.notifications.service;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;
import pe.getsemani.mikhipu.notifications.entity.PushSubscription;

import java.util.List;

@Service
public class PushNotificationService {

    private static final String PUBLIC_KEY = "BHgg9m5VQogi6Qh2hFiaTOLimzjAy0h6kziVtJq0OcrzExERckuX1q2VscFuUNimRtkWODaa45cjlge_V3VZqdQ";
    private static final String PRIVATE_KEY = "SkacB6cfJ8I7Gyr4W0DVuRyWAbhLljGnvHIoaDuquv4";
    private static final String SUBJECT = "mailto:contact@mikhipu.com";

    public void sendPushToUser(List<PushSubscription> subs, String payloadJson) {
        try {
            PushService pushService = new PushService();
            pushService.setPublicKey(Utils.loadPublicKey(PUBLIC_KEY));
            pushService.setPrivateKey(Utils.loadPrivateKey(PRIVATE_KEY));
            pushService.setSubject(SUBJECT);

            for (PushSubscription sub : subs) {
                Notification notification = new Notification(
                        sub.getEndpoint(),
                        sub.getP256dh(),
                        sub.getAuth(),
                        payloadJson.getBytes()
                );
                try {
                    HttpResponse response = pushService.send(notification);
                    System.out.println("Push enviado, code: " + response.getStatusLine().getStatusCode());
                } catch (Exception e) {
                    System.out.println("Error enviando push: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
