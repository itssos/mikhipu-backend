package pe.getsemani.mikhipu.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

@Configuration
public class CryptoConfig {
    @PostConstruct
    public void registerBC() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
}