package com.dsd.rdstore.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.perfil.PerfilUpdateDTO;
import com.dsd.rdstore.dto.usuario.UsuarioPasswordDTO;
import com.dsd.rdstore.dto.usuario.UsuarioResponseDTO;
import com.dsd.rdstore.exception.InvalidCredentialsException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UsuarioResponseDTO obtenerPerfil(String nombreUsuario){

        Usuario usuario = obtenerUsuario(nombreUsuario);

        return mapResponse(usuario);
    }


    public UsuarioResponseDTO actualizarPerfil(
            String nombreUsuario, 
            PerfilUpdateDTO dto) {

        Usuario usuario = obtenerUsuario(nombreUsuario);

        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return mapResponse(usuarioActualizado);

    }


    public void cambiarContrasena(
        String nombreUsuario, 
        UsuarioPasswordDTO dto) {

        Usuario usuario = obtenerUsuario(nombreUsuario);


        if (!passwordEncoder.matches(
                dto.contrasenaActual(),
                usuario.getContrasena())) {

            throw new InvalidCredentialsException();
        }

        usuario.setContrasena(passwordEncoder.encode(dto.nuevaContrasena()));

        usuarioRepository.save(usuario);
    }


    private Usuario obtenerUsuario(String nombreUsuario) {

        return usuarioRepository.findByNombreUsuario(nombreUsuario)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Usuario",
                    nombreUsuario
                ));
    }


    private UsuarioResponseDTO mapResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getNombreUsuario(),
            usuario.getActivo(),
            usuario.getRol().getId(),
            usuario.getRol().getNombre()
        );
    }

}
