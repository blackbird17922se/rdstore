package com.dsd.rdstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.perfil.PerfilUpdateDTO;
import com.dsd.rdstore.dto.usuario.UsuarioPasswordDTO;
import com.dsd.rdstore.dto.usuario.UsuarioResponseDTO;
import com.dsd.rdstore.service.PerfilService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/perfil")
public class PerfilController {

    private final PerfilService perfilService;


    @GetMapping
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil(
            // Spring Security puede darte el usuario autenticado con Authentication
            Authentication authentication) {

        String nombreUsuario = authentication.getName();

        UsuarioResponseDTO response = perfilService.obtenerPerfil(nombreUsuario);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<UsuarioResponseDTO> actualizarPerfil(
            Authentication authentication,
            @Valid @RequestBody PerfilUpdateDTO dto) {

        String nombreUsuario = authentication.getName();

        UsuarioResponseDTO perfilActualizado = perfilService.actualizarPerfil(nombreUsuario, dto);

        return ResponseEntity.ok(perfilActualizado);
    }

    @PatchMapping("/contrasena")
    // ya que al actualizar la contraseña no retorna un dato importante
    // a consultar por el usuario, retorno void
    public ResponseEntity<Void> cambiarContrasena(
            Authentication authentication, 
            @Valid @RequestBody UsuarioPasswordDTO dto) {


        perfilService.cambiarContrasena(authentication.getName(), dto);

        return ResponseEntity.noContent().build();

    }

}
