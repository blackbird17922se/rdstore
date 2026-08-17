package com.dsd.rdstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dsd.rdstore.dto.categoria.CategoriaRequestDTO;
import com.dsd.rdstore.dto.categoria.CategoriaResponseDTO;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Categoria;
import com.dsd.rdstore.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class) // le dice a JUnit: Utiliza Mockito para preparar los mocks de esta clase de
                                    // pruebas.
public class CategoriaServiceTest {

    @Mock // Mockito crea un repositorio ficticio, no es el real
    private CategoriaRepository categoriaRepository;

    /*
     * CategoriaService sí es el objeto real que queremos probar,
     * pero Mockito le coloca nuestro repository falso dentro.
     * 
     * CategoriaService REAL
     * │
     * └── CategoriaRepository MOCK
     */
    @InjectMocks
    private CategoriaService categoriaService;

    @Test // le dice a JUnit: Este método es una prueba que debes ejecutar.
    // Cuando ejecuto crearCategoria y el nombre no existe, debería crear la
    // categoría
    void crearCategoria_deberiaCrearCategoriaCuandoNombreNoExiste() {

        /***********************************************
         * ARRANGE: Preparar escenario
         *************************************/
        var nombre = "Papeleria";

        // aqui simula lo q le enviarioamos desde Postman y el controller
        CategoriaRequestDTO dto = new CategoriaRequestDTO(nombre);

        // Como es ficticio categoriaRepository, este no sabe q responder porque no
        // consulta la bd
        // entonces usamos esta sintaxis when.
        // se puede leer literalmente:
        // Cuando el repository reciba existsByNombreIgnoreCase(nombre), entonces
        // devuelve false.
        when(categoriaRepository
                .existsByNombreIgnoreCase(nombre))
                .thenReturn(false);

        // debemos simular lo que devolvería el repository. recuerda q es un repository
        // ficticio
        Categoria categoriaGuardada = new Categoria();
        categoriaGuardada.setId(1L); // Supongamos que la base de datos guardó esta categoría y le asignó el ID 1
        categoriaGuardada.setNombre(nombre);

        // ny(Categoria.class) que significa: Cuando save() reciba cualquier objeto
        // Categoria...
        when(categoriaRepository.save(any(Categoria.class)))
                // ...entonces devuelve esta categoría que hemos preparado.
                .thenReturn(categoriaGuardada);

        /***********************************************
         * ACT: Ejecutar
         *************************************/
        // Aqui se ejecuta mi categoria service real, pero usando el repositorio BD
        // ficticio
        CategoriaResponseDTO resultado = categoriaService.crearCategorias(dto);

        /***********************************************
         * ASSERT: Comprobar
         *************************************/
        // assert significa
        assertNotNull(resultado); // Espero que el método realmente me devuelva algo. no null
        assertEquals(1L, resultado.id()); // Espero que el ID obtenido sea 1.
        assertEquals(nombre, resultado.nombre()); // Espero que el nombre sea nombre.

        // Comprueba que tu service realmente llamó al repository.
        /*
         * Es decir: Durante la ejecución, ¿CategoriaService preguntó si Papelería ya
         * existía?
         */
        verify(categoriaRepository)
                .existsByNombreIgnoreCase(nombre);

        verify(categoriaRepository)
                .save(any(Categoria.class));

    }

    @Test
    void crearCategoria_deberiaLanzarExcepcionCuandoNombreYaExiste() {

        var nombre = "Papelería";

        // Arrange
        CategoriaRequestDTO dto = new CategoriaRequestDTO(nombre);

        // Simula que el nombre ya existe
        when(categoriaRepository
                .existsByNombreIgnoreCase(nombre))
                .thenReturn(true);

        // Act + Assert
        /*
         * Esto puedes leerlo así: Ejecuta categoriaService.crearCategoria(dto)
         * y espero que lance una DuplicateResourceException (el q yo cree).
         */
        DuplicateResourceException exception = assertThrows(
                /*
                * DuplicateResourceException.class significa “la clase/tipo de
                * excepción que espero que sea lanzada”.
                * Se puede leer así:
                *       Ejecuta usuarioService.crearUsuario(...) y comprueba que 
                *       lance una excepción de tipo DuplicateResourceException.
                */
                DuplicateResourceException.class,
                // Este es el código que quiero ejecutar y comprobar.
                () -> categoriaService.crearCategorias(dto));

        // comrpobamos que genero una excepcion con el mensaje correspondiente
        assertEquals(
                "Ya existe una categoría con el nombre: " + nombre,
                exception.getMessage());

        verify(categoriaRepository)
                .existsByNombreIgnoreCase(nombre);

        // Comprueba que save jamás fue llamado (porque hubo excepcion)
        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }

    @Test
    void listarCategorias_deberiaRetornarListaDeCategorias() {
        // Arrange

        String nombre1 = "Papelería", nombre2 = "Tecnología";

        // Creamos dos entidades falsas:
        Categoria categoria1 = new Categoria();
        categoria1.setId(1L);
        categoria1.setNombre(nombre1);

        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNombre(nombre2);

        /*
         * Cuando mi service llame a findAll(), no vayas a ninguna base de datos;
         * devuélveme estas dos categorías.
         */
        when(categoriaRepository.findAll())
                .thenReturn(List.of(categoria1, categoria2));

        // act: Aquí ejecutas tu CategoriaService real.
        List<CategoriaResponseDTO> resultado = categoriaService.listarCategorias();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size()); // 2 categorías

        // resultado esperado, resultado devuelto
        assertEquals(1L, resultado.get(0).id());
        assertEquals(nombre1, resultado.get(0).nombre());

        assertEquals(2L, resultado.get(1).id());
        assertEquals(nombre2, resultado.get(1).nombre());

        // comprueba que tu service efectivamente consultó el repository.
        verify(categoriaRepository).findAll();

    }

    @Test
    void listarCategorias_deberiaRetornarListaVaciaCuandoNoExistenCategorias() {

        // Arrange
        when(categoriaRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<CategoriaResponseDTO> resultado = categoriaService.listarCategorias();

        // Assert
        // “no encontró registros” no significa necesariamente null, por eso el not null
        // porque por lo menos debe retornar la lista vacia []
        assertNotNull(resultado); 
        assertEquals(0, resultado.size());

        verify(categoriaRepository).findAll();
    }

    @Test
    void obtenerCategoriaPorId_deberiaRetornarCategoriaCuandoExiste() {

        // Arrange
        Long id = 1L;

        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre("Papelería");

        // Cuando busques el ID 1, responde como si la base de datos hubiera encontrado
        // esta categoría.
        when(categoriaRepository.findById(id))
                // Como el Optional sí tiene contenido, orElseThrow() no lanza nada y entrega la
                // categoría.
                .thenReturn(Optional.of(categoria));

        // Act
        CategoriaResponseDTO resultado = categoriaService.obtenerCategoriaPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Papelería", resultado.nombre());

        verify(categoriaRepository).findById(id);
    }

    @Test
    void obtenerCategoriaPorId_deberiaLanzarExcepcionCuandoNoExiste() {

        // Arrange
        Long id = 99L;

        when(categoriaRepository.findById(id))
                // aqui simula q no encontro nada por tanto genera excepcion
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.obtenerCategoriaPorId(id));

        assertEquals(
                "Categoría no encontrado con identificador: " + id,
                exception.getMessage());

        verify(categoriaRepository).findById(id);
    }

    @Test
    void actualizarCategoria_deberiaActualizarCategoriaCuandoDatosSonValidos() {

        // Arrange
        Long id = 1L;

        CategoriaRequestDTO dto = new CategoriaRequestDTO("Tecnología");


        // representa cómo está actualmente en la BD.
        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(id);
        categoriaExistente.setNombre("Papelería");

        when(categoriaRepository.findById(id))
                .thenReturn(Optional.of(categoriaExistente));

        /*
         * buscar nombre = "Tecnología"
         * PERO
         * ignorar id = 1
         * Así permitimos que una categoría mantenga su propio nombre cuando sea
         * editada.
         */
        when(categoriaRepository
                .existsByNombreIgnoreCaseAndIdNot("Tecnología", id))
                .thenReturn(false);


        // representa lo que suponemos que devuelve JPA después del save().
        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setId(id);
        categoriaActualizada.setNombre("Tecnología");


        when(categoriaRepository.save(any(Categoria.class)))
                .thenReturn(categoriaActualizada);

        // Act
        CategoriaResponseDTO resultado = categoriaService.actualizarCategoria(id, dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Tecnología", resultado.nombre());

        verify(categoriaRepository).findById(id);

        verify(categoriaRepository)
                .existsByNombreIgnoreCaseAndIdNot("Tecnología", id);

        verify(categoriaRepository)
                .save(any(Categoria.class));
    }

    @Test
    void actualizarCategoria_deberiaLanzarExcepcionCuandoCategoriaNoExiste() {

        // Arrange
        Long id = 99L;

        CategoriaRequestDTO dto = new CategoriaRequestDTO("Tecnología");

        when(categoriaRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.actualizarCategoria(id, dto));

        assertEquals(
                "Categoría no encontrado con identificador: " + id,
                exception.getMessage());

        verify(categoriaRepository).findById(id);

        // indica q existsByNombreIgnoreCaseAndIdNot no se llamo nunca
        // porque si no se encontro id, no se debio llamar esto
        // Comprueba que existsByNombreIgnoreCaseAndIdNot() jamás fue llamado,
        // sin importar qué nombre o ID hubiera recibido.
        verify(categoriaRepository, never())
                .existsByNombreIgnoreCaseAndIdNot(anyString(), anyLong());

        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }

    /** la categoría existe, pero el nuevo nombre ya está usado por otra categoría */
    @Test
    void actualizarCategoria_deberiaLanzarExcepcionCuandoNombreYaExisteEnOtraCategoria() {

        // Arrange
        Long id = 1L;

        CategoriaRequestDTO dto = new CategoriaRequestDTO("Tecnología");

        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(id);
        categoriaExistente.setNombre("Papelería");

        when(categoriaRepository.findById(id))
                .thenReturn(Optional.of(categoriaExistente));

        when(categoriaRepository
                .existsByNombreIgnoreCaseAndIdNot("Tecnología", id))
                .thenReturn(true);

        // Act + Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> categoriaService.actualizarCategoria(id, dto));

        assertEquals(
                "Ya existe una categoría con el nombre: Tecnología",
                exception.getMessage());

        verify(categoriaRepository).findById(id);

        verify(categoriaRepository)
                .existsByNombreIgnoreCaseAndIdNot("Tecnología", id);

        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }

    @Test
    void eliminarCategoria_deberiaEliminarCategoriaCuandoExiste() {

        // Arrange
        Long id = 1L;

        when(categoriaRepository.existsById(id))
                .thenReturn(true);

        // Act
        categoriaService.eliminarCategoria(id);

        // Assert
        verify(categoriaRepository).existsById(id);
        verify(categoriaRepository).deleteById(id);
    }


    @Test
    void eliminarCategoria_deberiaLanzarExcepcionCuandoCategoriaNoExiste() {

        // Arrange
        Long id = 99L;

        when(categoriaRepository.existsById(id))
                .thenReturn(false);

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.eliminarCategoria(id));

        assertEquals(
                "Categoría no encontrado con identificador: 99",
                exception.getMessage());

        verify(categoriaRepository).existsById(id);

        verify(categoriaRepository, never())
                .deleteById(anyLong());
    }
}
