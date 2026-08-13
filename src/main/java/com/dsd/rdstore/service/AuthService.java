package com.dsd.rdstore.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.auth.LoginRequestDTO;
import com.dsd.rdstore.dto.auth.LoginResponseDTO;
import com.dsd.rdstore.exception.InvalidCredentialsException;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.repository.UsuarioRepository;
import com.dsd.rdstore.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String TIPO_TOKEN = "Bearer";


    public LoginResponseDTO login(LoginRequestDTO dto){

        // Consulta que el usuario exista
        Usuario usuario = usuarioRepository
            .findByNombreUsuario(dto.nombreUsuario())

            /* .orElseThrow(InvalidCredentialsException::new); es equivalente a:
            .orElseThrow(() -> new InvalidCredentialsException()); */
            .orElseThrow(InvalidCredentialsException::new);


        /*
        * Compara la contraseña recibida en texto plano
        * con el hash BCrypt almacenado en la base de datos.
        * Retorna true si corresponden.
        */
        boolean coincide = passwordEncoder.matches(
            dto.contrasena(), 
            usuario.getContrasena()
        );

        if (!coincide) {
            throw new InvalidCredentialsException();
        }

        if (!usuario.getActivo()) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generarToken(usuario);

        return new LoginResponseDTO(
            token,
            TIPO_TOKEN, 
            usuario.getNombreUsuario(), 
            usuario.getRol().getNombre()
        );

    }
    
}
