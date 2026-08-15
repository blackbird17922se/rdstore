package com.dsd.rdstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.usuario.UsuarioEstadoDTO;
import com.dsd.rdstore.dto.usuario.UsuarioPasswordDTO;
import com.dsd.rdstore.dto.usuario.UsuarioRequestDTO;
import com.dsd.rdstore.dto.usuario.UsuarioResponseDTO;
import com.dsd.rdstore.dto.usuario.UsuarioUpdateDTO;
import com.dsd.rdstore.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(
            @Valid @RequestBody UsuarioRequestDTO dto) {

        UsuarioResponseDTO nuevoUsuario = usuarioService.crearUsuario(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
        @PathVariable Long id,
        @Valid @RequestBody UsuarioUpdateDTO dto
    ){
        UsuarioResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(dto, id);

        return ResponseEntity.ok(usuarioActualizado);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponseDTO> cambiarEstadoUsuario(
            @PathVariable Long id, @Valid @RequestBody UsuarioEstadoDTO dto) {

        UsuarioResponseDTO usuarioActualizado = usuarioService.cambiarEstadoUsuario(id, dto);

        return ResponseEntity.ok(usuarioActualizado);

    }

    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<UsuarioResponseDTO> cambiarContrasenaUsuario(
            @PathVariable Long id, @Valid @RequestBody UsuarioPasswordDTO dto) {

        UsuarioResponseDTO usuarioActualizado = usuarioService.cambiarContrasenaUsuario(id, dto);

        return ResponseEntity.ok(usuarioActualizado);

    }



}
