package pe.getsemani.mikhipu.notifications.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PushSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int userId;

    @Column(nullable = false, length = 2048)
    private String endpoint;

    @Column(nullable = false, length = 2048)
    private String p256dh;

    @Column(nullable = false, length = 2048)
    private String auth;
}