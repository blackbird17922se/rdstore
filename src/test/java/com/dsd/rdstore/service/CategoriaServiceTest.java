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

@ExtendWith(MockitoExtension.class) 
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void crearCategoria_deberiaCrearCategoriaCuandoNombreNoExiste() {

        /***********************************************
         * ARRANGE: Preparar escenario
         *************************************/
        var nombre = "Papeleria";

        CategoriaRequestDTO dto = new CategoriaRequestDTO(nombre);

        when(categoriaRepository
                .existsByNombreIgnoreCase(nombre))
                .thenReturn(false);

        Categoria categoriaGuardada = new Categoria();
        categoriaGuardada.setId(1L);
        categoriaGuardada.setNombre(nombre);

        when(categoriaRepository.save(any(Categoria.class)))
                .thenReturn(categoriaGuardada);

        /***********************************************
         * ACT: Ejecutar
         *************************************/
        CategoriaResponseDTO resultado = categoriaService.crearCategorias(dto);

        /***********************************************
         * ASSERT: Comprobar
         *************************************/
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals(nombre, resultado.nombre());


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

        when(categoriaRepository
                .existsByNombreIgnoreCase(nombre))
                .thenReturn(true);

        // Act + Assert
        DuplicateResourceException exception = assertThrows(

                DuplicateResourceException.class,
                () -> categoriaService.crearCategorias(dto));


        assertEquals(
                "Ya existe una categoría con el nombre: " + nombre,
                exception.getMessage());

        verify(categoriaRepository)
                .existsByNombreIgnoreCase(nombre);

        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }

    @Test
    void listarCategorias_deberiaRetornarListaDeCategorias() {
        // Arrange

        String nombre1 = "Papelería", nombre2 = "Tecnología";

        Categoria categoria1 = new Categoria();
        categoria1.setId(1L);
        categoria1.setNombre(nombre1);

        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNombre(nombre2);

        when(categoriaRepository.findAll())
                .thenReturn(List.of(categoria1, categoria2));

        // act
        List<CategoriaResponseDTO> resultado = categoriaService.listarCategorias();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(1L, resultado.get(0).id());
        assertEquals(nombre1, resultado.get(0).nombre());

        assertEquals(2L, resultado.get(1).id());
        assertEquals(nombre2, resultado.get(1).nombre());

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

        when(categoriaRepository.findById(id))
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

        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(id);
        categoriaExistente.setNombre("Papelería");

        when(categoriaRepository.findById(id))
                .thenReturn(Optional.of(categoriaExistente));

        when(categoriaRepository
                .existsByNombreIgnoreCaseAndIdNot("Tecnología", id))
                .thenReturn(false);

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
