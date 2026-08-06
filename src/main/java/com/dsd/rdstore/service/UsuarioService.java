package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.usuario.UsuarioRequestDTO;
import com.dsd.rdstore.dto.usuario.UsuarioResponseDTO;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Rol;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.repository.RolRepository;
import com.dsd.rdstore.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public List<UsuarioResponseDTO> listarUsuarios() {

        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getNombreUsuario(),
                        usuario.getActivo(),
                        usuario.getRol().getId(),
                        usuario.getRol().getNombre()))
                .toList();
    }

    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto){
        
        Rol rol = rolRepository.findById(dto.idRol())
            .orElseThrow(() -> 
                new ResourceNotFoundException("Rol", dto.idRol()));

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setNombreUsuario(dto.nombreUsuario());
        usuario.setContrasena(dto.contrasena());
        usuario.setActivo(true);
        usuario.setRol(rol);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
            usuarioGuardado.getId(),
            usuarioGuardado.getNombre(),
            usuarioGuardado.getApellido(),
            usuarioGuardado.getNombreUsuario(),
            usuarioGuardado.getActivo(),
            usuarioGuardado.getRol().getId(),
            usuarioGuardado.getRol().getNombre()
        );
    }

}
