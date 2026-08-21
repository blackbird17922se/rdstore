package com.dsd.rdstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dsd.rdstore.dto.auth.LoginRequestDTO;
import com.dsd.rdstore.dto.auth.LoginResponseDTO;
import com.dsd.rdstore.exception.InvalidCredentialsException;
import com.dsd.rdstore.model.Rol;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.repository.UsuarioRepository;
import com.dsd.rdstore.security.JwtService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private final String NOMBRE_USUARIO_ADMIN = "mauro92";
    private static final String NOMBRE = "Laura";
    private static final String APELLIDO = "Perez";
    private static final String CONTRASENA = "123456";

    private final Rol rolAdmin = mapRol(1L, "ADMIN");

    private final String contrasenaActualEncriptada = "HASH_BCRYPT_ACTUAL";
    private final String token = "JWT_SIMULADO";

    private final  Usuario usuarioAdmin = mapUsuario(
            1L, NOMBRE, APELLIDO, 
            NOMBRE_USUARIO_ADMIN,
            true, rolAdmin, contrasenaActualEncriptada);



    @Test
    void loginEsperaLoginCorrecto(){

        Rol rol = mapRol(1L, "ADMIN");

        Usuario usuarioGuardado = mapUsuario(
            1L, NOMBRE, APELLIDO, 
            NOMBRE_USUARIO_ADMIN,
            true, rol);

        // en el servicio, passwordEncoder.matches( recibe
        // dto.contrasena(), y usuario.getContrasena(), entonces 
        // pasarle contrasenaActualEncriptada al matches hace q falle
        usuarioGuardado.setContrasena(contrasenaActualEncriptada);

        LoginRequestDTO dto = 
            new LoginRequestDTO(NOMBRE_USUARIO_ADMIN, CONTRASENA);

        when(usuarioRepository
            .findByNombreUsuario(NOMBRE_USUARIO_ADMIN))
            .thenReturn(Optional.of(usuarioGuardado));

        when(passwordEncoder.matches(
                dto.contrasena(), 
                usuarioGuardado.getContrasena()))
            .thenReturn(true);

        when(jwtService.generarToken(usuarioGuardado))
            .thenReturn(token);

        LoginResponseDTO respuesta = authService.login(dto);

        // ACT
        assertNotNull(respuesta);
        assertEquals(NOMBRE_USUARIO_ADMIN, respuesta.nombreUsuario());
        assertEquals("ADMIN", respuesta.rol());
        assertEquals(token, respuesta.token());
        assertEquals("Bearer", respuesta.tipo());

        verify(usuarioRepository).findByNombreUsuario(NOMBRE_USUARIO_ADMIN);
        verify(passwordEncoder).matches(dto.contrasena(), contrasenaActualEncriptada);
        verify(jwtService).generarToken(usuarioGuardado);
        
    } 

    @Test
    void loginEsperaCredencialesInvalidas(){

        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO(
            NOMBRE_USUARIO_ADMIN, "98745");

        when(usuarioRepository
            .findByNombreUsuario(dto.nombreUsuario()))
            .thenReturn(Optional.of(usuarioAdmin));

        when(passwordEncoder.matches(
            dto.contrasena(), 
            usuarioAdmin.getContrasena()))
        .thenReturn(false);


        // Act + Assert
        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class, 
            () -> authService.login(dto)
        );

        assertEquals("Credenciales inválidas", 
            exception.getMessage());

        verify(usuarioRepository)
            .findByNombreUsuario(NOMBRE_USUARIO_ADMIN);
        verify(passwordEncoder).matches(
            dto.contrasena(), 
            usuarioAdmin.getContrasena());
        verify(jwtService, never())
            .generarToken(any(Usuario.class));
    }


    private Usuario mapUsuario(Long id, String nombre, String apellido,
            String nombreUsuario, Boolean activo, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setActivo(activo);
        usuario.setRol(rol);

        return usuario;
    }

    private Usuario mapUsuario(Long id, String nombre, String apellido,
            String nombreUsuario, Boolean activo, Rol rol, String contrasena) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setActivo(activo);
        usuario.setRol(rol);
        usuario.setContrasena(contrasena);

        return usuario;
    }



    private Rol mapRol(Long id, String nombre){
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre(nombre);
        return rol;
    }

}
