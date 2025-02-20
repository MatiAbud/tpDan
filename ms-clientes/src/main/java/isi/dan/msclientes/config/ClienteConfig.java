package isi.dan.msclientes.config;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class ClienteConfig {
    
    @Value("${cliente.maximoDescubierto}")
    private BigDecimal maximoDescubierto;

        @PostConstruct
    public void validate() {
        if (maximoDescubierto.signum()<=0) {
            throw new IllegalArgumentException("El máximo descubierto debe ser mayor que cero.");
        }
    }

    public BigDecimal getMaximoDescubierto() {
        return maximoDescubierto;
    }
}
