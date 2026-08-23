package com.dsd.rdstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.dsd.rdstore.model.Rol;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.security.JwtService;

public class JwtServiceTest {

    private JwtService jwtService;

    private Usuario usuarioAdmin;

    private static final String SECRET_KEY = "rdstore_clave_jwt_pruebas_2026_muy_segura_123456789";

    private static final long EXPIRATION_TIME = 3600000L;

    @BeforeEach 
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                SECRET_KEY);

        ReflectionTestUtils.setField(
                jwtService,
                "expirationTime",
                EXPIRATION_TIME);

        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");

        usuarioAdmin = new Usuario();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setNombre("Laura");
        usuarioAdmin.setApellido("Perez");
        usuarioAdmin.setNombreUsuario("mauro92");
        usuarioAdmin.setActivo(true);
        usuarioAdmin.setRol(rol);
    }

    @Test
    void generarToken_deberiaGenerarTokenValido() {

        // Act
        String token = jwtService.generarToken(usuarioAdmin);

        // Assets
        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtService.validarToken(token));

    }

    @Test
    void validarTokenDebeRetornarFalseCuandoTokenEsAlterado() {

        String token = jwtService.generarToken(usuarioAdmin);

        String tokenAlterado = token + "abc";

        boolean resultado =
            jwtService.validarToken(tokenAlterado);

        assertFalse(resultado);
    }

    @Test
    void extraerDatosDebeRetornarNombreUsuarioYRol() {

        // Arrange
        String token = jwtService.generarToken(usuarioAdmin);

        // act
        String nombreUsuario = jwtService.extraerNombreUsuario(token);

        String rol = jwtService.extraerRol(token);

        // Asserts
        assertEquals(usuarioAdmin.getNombreUsuario(), nombreUsuario);
        assertEquals(usuarioAdmin.getRol().getNombre(), rol);

    }

}
