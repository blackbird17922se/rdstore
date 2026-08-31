package com.dsd.rdstore.dto.usuario.perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarContrasenaDTO(
    @NotBlank String contrasenaActual,

    @NotBlank
    @Size(min = 6)
    String nuevaContrasena
) {}