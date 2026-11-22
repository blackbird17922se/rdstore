package com.dsd.rdstore.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.boot.validation.autoconfigure.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Define la zona horaria de negocio utilizada por las validaciones temporales
 * de Jakarta Validation (@PastOrPresent, @FutureOrPresent, etc.).
 *
 * No modifica el reloj ni la zona horaria del sistema operativo de Azure.
 */
@Configuration
public class ValidationClockConfig {

    private static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");

    @Bean
    public ValidationConfigurationCustomizer validationClockCustomizer() {
        return configuration -> configuration.clockProvider(
                () -> Clock.system(ZONA_COLOMBIA));
    }
}
