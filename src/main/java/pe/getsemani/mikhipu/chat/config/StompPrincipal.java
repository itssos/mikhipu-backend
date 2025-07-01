package pe.getsemani.mikhipu.chat.config;

import java.security.Principal;

// Clase de Principal personalizada
public class StompPrincipal implements Principal {
    private String name;
    public StompPrincipal(String name) { this.name = name; }
    @Override public String getName() { return name; }
}
