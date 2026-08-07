package com.dsd.rdstore.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

    @NotBlank(message = "El nombre de usuario es obligatorio")
    String nombreUsuario,

    @NotBlank(message =  "La contraseña es obligatoria")
    String contrasena
) {}
