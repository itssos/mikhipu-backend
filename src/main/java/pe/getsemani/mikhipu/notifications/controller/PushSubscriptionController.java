package pe.getsemani.mikhipu.notifications.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.getsemani.mikhipu.notifications.dto.PushSubscriptionRequest;
import pe.getsemani.mikhipu.notifications.entity.PushSubscription;
import pe.getsemani.mikhipu.notifications.repository.PushSubscriptionRepository;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushSubscriptionController {
    private final PushSubscriptionRepository repo;

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody PushSubscriptionRequest req, Principal principal) {
        // Busca si ya existe la subscripción por userId y endpoint
        Optional<PushSubscription> exists = repo.findByUserIdAndEndpoint(req.userId, req.subscription.endpoint);
        if (exists.isPresent()) {
            // Puedes actualizar las keys por si cambiaron (opcional)
            PushSubscription sub = exists.get();
            sub.setP256dh(req.subscription.keys.p256dh);
            sub.setAuth(req.subscription.keys.auth);
            repo.save(sub);
            // O simplemente retornar, si no quieres actualizar nada
            // return;
        } else {
            PushSubscription sub = new PushSubscription();
            sub.setUserId(req.userId);
            sub.setEndpoint(req.subscription.endpoint);
            sub.setP256dh(req.subscription.keys.p256dh);
            sub.setAuth(req.subscription.keys.auth);
            repo.save(sub);
        }
    }
}