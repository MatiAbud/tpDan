package isi.dan.msclientes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class ClienteConfig {
    
    @Value("${cliente.maximoDescubierto}")
    private int maximoDescubierto;

        @PostConstruct
    public void validate() {
        if (maximoDescubierto <= 0) {
            throw new IllegalArgumentException("El máximo descubierto debe ser mayor que cero.");
        }
    }

    public int getMaximoDescubierto() {
        return maximoDescubierto;
    }
}
