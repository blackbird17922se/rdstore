package com.dsd.rdstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dsd.rdstore.dto.usuario.UsuarioEstadoDTO;
import com.dsd.rdstore.dto.usuario.UsuarioPasswordDTO;
import com.dsd.rdstore.dto.usuario.UsuarioRequestDTO;
import com.dsd.rdstore.dto.usuario.UsuarioResponseDTO;
import com.dsd.rdstore.dto.usuario.UsuarioUpdateDTO;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.exception.InvalidCredentialsException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Rol;
import com.dsd.rdstore.model.Usuario;
import com.dsd.rdstore.repository.RolRepository;
import com.dsd.rdstore.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UsuarioService usuarioService;


    private static final String NOMBRE = "Laura";
    private static final String APELLIDO = "Perez";
    private static final String NOMBRE_USUARIO = "lauPe";
    private static final String CONTRASENA = "123456";

    @Test
    void listarUsuariosEsperamosListaDeUsuarios(){

        String nombre1 = "Laura", apellido1 = "Perez", nombreUsuario1 = "lauPe";
        String nombre2 = "Jorge", apellido2 = "Bareto", nombreUsuario2 = "Jbareto";

        Rol rol1 = new Rol();
        rol1.setId(1L);
        rol1.setNombre("ADMIN");

        Rol rol2 = new Rol();
        rol2.setId(2L);
        rol2.setNombre("VENDEDOR");


        Usuario usuario1 = mapUsuario(1L, NOMBRE, APELLIDO, NOMBRE_USUARIO,
            true, rol1);

        Usuario usuario2 = mapUsuario(2L, nombre2, apellido2, nombreUsuario2,
            false, rol2);


        when(usuarioRepository.findAll())
            .thenReturn(List.of(usuario1, usuario2));

        // Act
        List<UsuarioResponseDTO> resultado = usuarioService.listarUsuarios();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(1L, resultado.get(0).id());
        assertEquals(nombre1, resultado.get(0).nombre());
        assertEquals(apellido1, resultado.get(0).apellido());
        assertEquals(nombreUsuario1, resultado.get(0).nombreUsuario());
        assertEquals(true, resultado.get(0).activo());
        assertEquals("ADMIN", resultado.get(0).nombreRol());

        assertEquals(2L, resultado.get(1).id());
        assertEquals(nombre2, resultado.get(1).nombre());
        assertEquals(apellido2, resultado.get(1).apellido());
        assertEquals(nombreUsuario2, resultado.get(1).nombreUsuario());
        assertEquals(false, resultado.get(1).activo());
        assertEquals("VENDEDOR", resultado.get(1).nombreRol());

        verify(usuarioRepository).findAll();

    }
    
    @Test
    void listarUsuariosEsperamosListaVacia(){

        when(usuarioRepository.findAll())
            .thenReturn(List.of());

        List<UsuarioResponseDTO> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());

        verify(usuarioRepository).findAll();
        
    }

    @Test
    void crearUsuarioEsperamosCreacionUsuario(){

        // Arrange
        String contrasenaEncriptada = "HASH_BCRYPT_SIMULADO";

        UsuarioRequestDTO usuarioRequestDTO = new UsuarioRequestDTO(
            NOMBRE, APELLIDO, NOMBRE_USUARIO, CONTRASENA, 1L);

        when(usuarioRepository
            .existsByNombreUsuarioIgnoreCase(NOMBRE_USUARIO))
            .thenReturn(false);


        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");

        Usuario usuarioGuardado = mapUsuario(1L, NOMBRE, APELLIDO, NOMBRE_USUARIO,
            true, rol);

        when(rolRepository.findById(rol.getId()))
            .thenReturn(Optional.of(rol));
        
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuarioGuardado);

        when(passwordEncoder.encode(CONTRASENA))
            .thenReturn(contrasenaEncriptada);

        //ATC
        UsuarioResponseDTO usuarioCreado = usuarioService.crearUsuario(usuarioRequestDTO);

        assertNotNull(usuarioCreado);
        assertEquals(1L, usuarioCreado.id());
        assertEquals(NOMBRE, usuarioCreado.nombre());
        assertEquals(APELLIDO, usuarioCreado.apellido());
        assertEquals(NOMBRE_USUARIO, usuarioCreado.nombreUsuario());
        assertTrue(usuarioCreado.activo());
        assertEquals(1L, usuarioCreado.idRol());
        assertEquals("ADMIN", usuarioCreado.nombreRol());

        verify(usuarioRepository)
            .existsByNombreUsuarioIgnoreCase(NOMBRE_USUARIO);

        verify(rolRepository)
            .findById(1L);

        verify(passwordEncoder)
            .encode(CONTRASENA);


        /* Aquí estás diciendo: Quiero un capturador preparado para 
        capturar objetos de tipo Usuario. */
        ArgumentCaptor<Usuario> captor =
            ArgumentCaptor.forClass(Usuario.class);


        /* Esta línea hace dos cosas:
        1. Verifica que save() fue llamado
        2. Guarda el Usuario que recibió save() */
        verify(usuarioRepository)
            .save(captor.capture());

        // Dame el objeto que capturaste.
        Usuario usuarioEnviadoAGuardar = captor.getValue();

        assertEquals(NOMBRE, usuarioEnviadoAGuardar.getNombre());
        assertEquals(APELLIDO, usuarioEnviadoAGuardar.getApellido());
        assertEquals(NOMBRE_USUARIO, usuarioEnviadoAGuardar.getNombreUsuario());

        // IMPORTANTE, aqui evalua q el proceso de passwordEncoder si devolvio el
        // HASH_BCRYPT_SIMULADO y que lo envio al usuario creado
        assertEquals(contrasenaEncriptada, usuarioEnviadoAGuardar.getContrasena());
        assertEquals(true, usuarioEnviadoAGuardar.getActivo());
        assertEquals(1L, usuarioEnviadoAGuardar.getRol().getId());

    }

    @Test
    void crearUsuarioNoDebeCrearNombreUsuarioDuplicado() {

        // Arrange
        String nombre = "Laura";
        String apellido = "Perez";
        String nombreUsuario = "lauPe";
        String contrasena = "123456";

        UsuarioRequestDTO usuarioRequestDTO = new UsuarioRequestDTO(
                nombre, apellido, nombreUsuario, contrasena, 1L);
        
        
        when(usuarioRepository
            .existsByNombreUsuarioIgnoreCase(nombreUsuario))
            .thenReturn(true);

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class, 
            () -> usuarioService.crearUsuario(usuarioRequestDTO));

        assertEquals(
            "Ya existe un usuario con el nombre de usuario: " + nombreUsuario, 
            exception.getMessage());

        verify(usuarioRepository)
            .existsByNombreUsuarioIgnoreCase(nombreUsuario);

        // ni siquiera deio llegar a  buscar rol ni cifrar contraseña
        verify(rolRepository, never())
            .findById(anyLong());

        verify(passwordEncoder, never())
            .encode(contrasena);

        verify(usuarioRepository, never())
            .save(any(Usuario.class));

    }

    @Test
    void crearUsuarioNoDebeCrearPorqueRolInexistente() {

        // Arrange
        UsuarioRequestDTO usuarioRequestDTO = new UsuarioRequestDTO(
                NOMBRE, APELLIDO, NOMBRE_USUARIO, CONTRASENA, 15L);


        when(usuarioRepository
            .existsByNombreUsuarioIgnoreCase(NOMBRE_USUARIO))
            .thenReturn(false);

        when(rolRepository
            .findById(usuarioRequestDTO.idRol()))
            .thenReturn(Optional.empty());
            

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class, 
            () -> usuarioService.crearUsuario(usuarioRequestDTO));

        assertEquals(
                "Rol no encontrado con identificador: " + usuarioRequestDTO.idRol(),
                exception.getMessage());

        verify(usuarioRepository)
                .existsByNombreUsuarioIgnoreCase(NOMBRE_USUARIO);

        verify(rolRepository)
                .findById(usuarioRequestDTO.idRol());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(usuarioRepository, never())
                .save(any(Usuario.class));

    }


    // obtenerUsuarioPorId
    @Test
    void obtenerUsuarioPorIdDebeRetornarUsuarioCuandoExiste() {

        // Arrange
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");

        Usuario usuario = mapUsuario(1L, NOMBRE, APELLIDO, NOMBRE_USUARIO,
            true, rol);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        // Act
        UsuarioResponseDTO resultado = usuarioService.obtenerUsuarioPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals(NOMBRE, resultado.nombre());
        assertEquals(APELLIDO, resultado.apellido());
        assertEquals(NOMBRE_USUARIO, resultado.nombreUsuario());
        assertTrue(resultado.activo());
        assertEquals(1L, resultado.idRol());
        assertEquals("ADMIN", resultado.nombreRol());

        verify(usuarioRepository).findById(1L);
    }

    @Test
    void obtenerUsuarioPorIdDebeLanzarExcepcionCuandoNoExiste() {

        // Arrange
        Long id = 99L;

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.obtenerUsuarioPorId(id));

        assertEquals(
                "Usuario no encontrado con identificador: " + id,
                exception.getMessage());

        verify(usuarioRepository).findById(id);
    }
    

    @Test
    void actualizarUsuarioRetornaExitoso(){

        Long id = 23L;
        Rol rol = mapRol(1L, "ADMIN");

        UsuarioUpdateDTO dto = new UsuarioUpdateDTO(
            "Pedro", "Fernandez", rol.getId());

        // Existente
        Usuario usuarioExistente = mapUsuario(
                id,
                NOMBRE,
                APELLIDO,
                NOMBRE_USUARIO,
                true,
                rol);


        when(usuarioRepository.findById(id))
            .thenReturn(Optional.of(usuarioExistente));

        when(rolRepository.findById(rol.getId()))
            .thenReturn(Optional.of(rol));
            
        // thenAnswer: Cuando hagan save(), devuelve exactamente 
        // el mismo objeto que recibira cuando usuarioRepository ejecute save
        // cuando se ejecute usuarioService.actualizarUsuario(dto, id)
        when(usuarioRepository.save(any(Usuario.class)))
            // invocacion.getArgument(0): Dame el primer argumento que recibió save().
            .thenAnswer(invocacion -> invocacion.getArgument(0));
        

        // Act
        UsuarioResponseDTO respuesta = usuarioService.actualizarUsuario(dto, id);

        // Assert
        assertNotNull(respuesta);
        assertEquals(id, respuesta.id());
        assertEquals("Pedro", respuesta.nombre());
        assertEquals("Fernandez", respuesta.apellido());

        // Estos dos son importantes:
        // actualizar no debe alterar username ni estado
        assertEquals(NOMBRE_USUARIO, respuesta.nombreUsuario());
        assertTrue(respuesta.activo());

        assertEquals(rol.getId(), respuesta.idRol());
        assertEquals(rol.getNombre(), respuesta.nombreRol());

        verify(usuarioRepository)
            .findById(id);
        verify(rolRepository)
            .findById(rol.getId());
        verify(usuarioRepository)
            .save(any(Usuario.class));
        
    }

    @Test
    void cambiarEstadoUsuarioEsperaCambioEstado(){

        // Arrange
        Long id = 23L;
        Rol rol = mapRol(1L, "ADMIN");

        UsuarioEstadoDTO dto = new UsuarioEstadoDTO(false);

        Usuario usuarioExistente = mapUsuario(
            id, NOMBRE, APELLIDO,
            NOMBRE_USUARIO, true, rol
        );

        when(usuarioRepository.findById(id))
            .thenReturn(Optional.of(usuarioExistente));

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

        // Act
        UsuarioResponseDTO respuesta = usuarioService.cambiarEstadoUsuario(id, dto);
        
        // Assert
        assertNotNull(respuesta);
        assertFalse(respuesta.activo());

        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).save(any(Usuario.class));

    }


    /* cambiarContrasenaUsuario()
        ├── contraseña correcta → cambia
        └── contraseña actual incorrecta → excepción
    */
    @Test
    void cambiarContrasenaUsuarioEsperaCambioContrasena(){

        // Arrange
        Long id = 23L;
        Rol rol = mapRol(1L, "ADMIN");

        String contrasenaActualEncriptada = "HASH_BCRYPT_ACTUAL";
        String contrasenaNuevaEncriptada = "HASH_BCRYPT_NUEVO";

        UsuarioPasswordDTO dto = new UsuarioPasswordDTO(
            "123456", "654321");

        Usuario usuarioExistente = mapUsuario(
            id, NOMBRE, APELLIDO,
            NOMBRE_USUARIO, true, rol
        );
        usuarioExistente.setContrasena(contrasenaActualEncriptada);

        when(usuarioRepository.findById(id))
            .thenReturn(Optional.of(usuarioExistente));

        when(passwordEncoder.matches(
                dto.contrasenaActual(),
                contrasenaActualEncriptada))
            .thenReturn(true);
        
        when(passwordEncoder.encode(dto.nuevaContrasena()))
            .thenReturn(contrasenaNuevaEncriptada);

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));


        // Act
        UsuarioResponseDTO respuesta = usuarioService.cambiarContrasenaUsuario(id, dto);

        
        // Assert
        assertNotNull(respuesta);

        ArgumentCaptor<Usuario> captor =
            ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepository).save(captor.capture());

        Usuario usuarioEditado = captor.getValue();

        
        assertEquals(contrasenaNuevaEncriptada, usuarioEditado.getContrasena());

        verify(usuarioRepository).findById(id);
        verify(passwordEncoder).matches(
            dto.contrasenaActual(),
            contrasenaActualEncriptada
        );
        verify(passwordEncoder).encode(dto.nuevaContrasena());

    }

    @Test
    void cambiarContrasenaUsuarioEsperaContrasenaInvalida(){

        // Arrange
        Long id = 23L;
        Rol rol = mapRol(1L, "ADMIN");
        String contrasenaActualEncriptada = "HASH_BCRYPT_ACTUAL";


        UsuarioPasswordDTO dto = new UsuarioPasswordDTO(
            "123456", "654321");

        Usuario usuarioExistente = mapUsuario(
            id, NOMBRE, APELLIDO,
            NOMBRE_USUARIO, true, rol
        );
        usuarioExistente.setContrasena(contrasenaActualEncriptada);

        when(usuarioRepository.findById(id))
            .thenReturn(Optional.of(usuarioExistente));

        when(passwordEncoder.matches(
            dto.contrasenaActual(), contrasenaActualEncriptada))
            .thenReturn(false);


        // ATC + Asserts
        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class, 
            () -> usuarioService.cambiarContrasenaUsuario(id, dto));


        assertEquals("Credenciales inválidas", exception.getMessage());

        verify(usuarioRepository).findById(id);
        verify(passwordEncoder).matches(dto.contrasenaActual(), contrasenaActualEncriptada);
        verify(passwordEncoder, never()).encode(dto.nuevaContrasena());
        verify(usuarioRepository, never()).save(any(Usuario.class));

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

    private Rol mapRol(Long id, String nombre){
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre(nombre);
        return rol;
    }


    /*

    String nombre2 = "Jorge", apellido2 = "Bareto", 
            nombreUsuario2 = "Jbareto";
    
    Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(2L);
        usuarioExistente.setNombre(nombre2);
        usuarioExistente.setApellido(apellido2);
        usuarioExistente.setNombreUsuario(nombreUsuario2);
        usuarioExistente.setRol(rol);
        usuarioExistente.setActivo(true);

        when(usuarioRepository.findById(2L))
            .thenReturn(Optional.of(usuarioExistente));


                    DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class, 
            ()-> usuarioService)

    */


}
