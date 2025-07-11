package pe.getsemani.mikhipu.notifications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.getsemani.mikhipu.notifications.entity.PushSubscription;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findByUserId(int userId);
    Optional<PushSubscription> findByUserIdAndEndpoint(int userId, String endpoint);
}