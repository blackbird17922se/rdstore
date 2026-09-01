package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.usuario.UsuarioEstadoDTO;
import com.dsd.rdstore.dto.usuario.UsuarioPasswordDTO;
import com.dsd.rdstore.dto.usuario.UsuarioRequestDTO;
import com.dsd.rdstore.dto.usuario.UsuarioResponseDTO;
import com.dsd.rdstore.dto.usuario.UsuarioUpdateDTO;
import com.dsd.rdstore.dto.usuario.perfil.CambiarContrasenaDTO;
import com.dsd.rdstore.dto.usuario.perfil.PerfilUsuarioResponseDTO;
import com.dsd.rdstore.dto.usuario.perfil.PerfilUsuarioUpdateDTO;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.exception.InvalidCredentialsException;
import com.dsd.rdstore.exception.NegocioExcepcion;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Rol;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.repository.RolRepository;
import com.dsd.rdstore.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> listarUsuarios() {

        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> mapResponse(usuario))
                .toList();
    }


    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto){

        String nombreUsuario = dto.nombreUsuario().trim();

        if (usuarioRepository.existsByNombreUsuarioIgnoreCase(nombreUsuario)) {
            throw new DuplicateResourceException(
                    "Ya existe un usuario con el nombre de usuario: "
                            + nombreUsuario);
}
        
        Rol rol = rolRepository.findById(dto.idRol())
            .orElseThrow(() -> 
                new ResourceNotFoundException("Rol", dto.idRol()));

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setContrasena(
            passwordEncoder.encode(dto.contrasena())
        );
        usuario.setActivo(true);
        usuario.setRol(rol);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return mapResponse(usuarioGuardado);
    }


    public UsuarioResponseDTO obtenerUsuarioPorId (Long id){

        Usuario usuario = validarUsuarioExistentePorId(id);

        return mapResponse(usuario);
    }


    public UsuarioResponseDTO actualizarUsuario(UsuarioUpdateDTO dto, Long id){

        Usuario usuario = validarUsuarioExistentePorId(id);

        Rol rol = rolRepository.findById(dto.idRol())
                        .orElseThrow(() -> 
                            new ResourceNotFoundException("Rol", dto.idRol()));


        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setRol(rol);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return mapResponse(usuarioActualizado);

    }


    public UsuarioResponseDTO cambiarEstadoUsuario(Long id, UsuarioEstadoDTO dto){

        Usuario usuario = validarUsuarioExistentePorId(id);

        usuario.setActivo(dto.activo());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return mapResponse(usuarioActualizado);
        
    }


    public UsuarioResponseDTO cambiarContrasenaUsuario(Long id, UsuarioPasswordDTO dto){

        Usuario usuario = validarUsuarioExistentePorId(id);

        if (!passwordEncoder.matches(
                dto.contrasenaActual(),
                usuario.getContrasena())) {

            throw new InvalidCredentialsException();
        }

        usuario.setContrasena(passwordEncoder.encode(dto.nuevaContrasena()));

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return mapResponse(usuarioActualizado);
        
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

    private Usuario validarUsuarioExistentePorId(Long id){

        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", id)); 

    }

    public PerfilUsuarioResponseDTO obtenerPerfil(Authentication authentication) {
        String nombreUsuario = authentication.getName();

        Usuario usuario = validarUsuarioActivo(nombreUsuario);

        return new PerfilUsuarioResponseDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getNombreUsuario(),
            usuario.getRol().getNombre()
        );
    }

    
    @Transactional
    public PerfilUsuarioResponseDTO actualizarPerfil(
        PerfilUsuarioUpdateDTO request,
        Authentication authentication
    ) {
        String nombreUsuario = authentication.getName();

        Usuario usuario = validarUsuarioActivo(nombreUsuario);

        usuario.setNombre(request.nombre().trim());
        usuario.setApellido(request.apellido().trim());

        usuarioRepository.save(usuario);

        return new PerfilUsuarioResponseDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getNombreUsuario(),
            usuario.getRol().getNombre()
        );
    }


    @Transactional
    public void cambiarContrasena(
        CambiarContrasenaDTO request,
        Authentication authentication
    ) {
        String nombreUsuario = authentication.getName();

        Usuario usuario = validarUsuarioActivo(nombreUsuario);

        boolean coincide = passwordEncoder.matches(
            request.contrasenaActual(),
            usuario.getContrasena()
        );

        if (!coincide) {
            throw new NegocioExcepcion(
                "La contraseña actual no es correcta"
            );
        }

        if (passwordEncoder.matches(
            request.nuevaContrasena(),
            usuario.getContrasena()
        )) {
            throw new NegocioExcepcion(
                "La nueva contraseña debe ser diferente a la actual"
            );
        }

        usuario.setContrasena(
            passwordEncoder.encode(
                request.nuevaContrasena()
            )
        );

        usuarioRepository.save(usuario);
    }


    private Usuario validarUsuarioActivo(String nombreUsuario){
        return usuarioRepository.findByNombreUsuarioAndActivoTrue(nombreUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", nombreUsuario)); 
    }

}
