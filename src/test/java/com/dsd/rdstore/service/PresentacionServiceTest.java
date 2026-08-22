package com.dsd.rdstore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dsd.rdstore.dto.presentacion.PresentacionEstadoDTO;
import com.dsd.rdstore.dto.presentacion.PresentacionRequestDTO;
import com.dsd.rdstore.dto.presentacion.PresentacionResponseDTO;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.model.Presentacion;
import com.dsd.rdstore.repository.PresentacionRepository;

@ExtendWith(MockitoExtension.class)
class PresentacionServiceTest {

    @Mock
    private PresentacionRepository presentacionRepository;

    @InjectMocks
    private PresentacionService presentacionService;

    @Test
    void crearPresentacionDebeCrearCorrectamente() {

        // Arrange
        PresentacionRequestDTO dto = new PresentacionRequestDTO("CAJA");

        when(presentacionRepository
                .existsByNombreIgnoreCase("CAJA"))
                .thenReturn(false);

        Presentacion guardada = new Presentacion();
        guardada.setId(1L);
        guardada.setNombre("CAJA");

        when(presentacionRepository.save(any(Presentacion.class)))
                .thenReturn(guardada);

        // Act
        PresentacionResponseDTO respuesta = presentacionService.crearPresentacion(dto);

        // Assert
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.id());
        assertEquals("CAJA", respuesta.nombre());

        verify(presentacionRepository)
                .save(any(Presentacion.class));
    }

    @Test
    void crearPresentacionNoDebePermitirDuplicados() {

        // Arrange
        PresentacionRequestDTO dto = new PresentacionRequestDTO("CAJA");

        when(presentacionRepository
                .existsByNombreIgnoreCase("CAJA"))
                .thenReturn(true);

        // Act + Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> presentacionService.crearPresentacion(dto));

        verify(presentacionRepository, never())
                .save(any(Presentacion.class));
    }

    @Test
    void actualizarPresentacionDebeActualizarCorrectamente() {

        // Arrange
        Long id = 1L;

        Presentacion existente = new Presentacion();
        existente.setId(id);
        existente.setNombre("UNIDAD");

        PresentacionRequestDTO dto = new PresentacionRequestDTO("CAJA");

        when(presentacionRepository.findById(id))
                .thenReturn(Optional.of(existente));

        when(presentacionRepository
                .existsByNombreIgnoreCaseAndIdNot("CAJA", id))
                .thenReturn(false);

        when(presentacionRepository.save(any(Presentacion.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        // Act
        PresentacionResponseDTO respuesta = presentacionService.actualizarPresentacion(id, dto);

        // Assert
        assertEquals(id, respuesta.id());
        assertEquals("CAJA", respuesta.nombre());

        verify(presentacionRepository)
                .save(any(Presentacion.class));
    }

    @Test
    void crearPresentacionDebeCrearActiva() {

        PresentacionRequestDTO dto = new PresentacionRequestDTO("CAJA");

        when(presentacionRepository
                .existsByNombreIgnoreCase("CAJA"))
                .thenReturn(false);

        when(presentacionRepository.save(any(Presentacion.class)))
                .thenAnswer(invocacion -> {

                    Presentacion presentacion = invocacion.getArgument(0);

                    presentacion.setId(1L);

                    return presentacion;
                });

        PresentacionResponseDTO respuesta = presentacionService.crearPresentacion(dto);

        assertNotNull(respuesta);
        assertEquals("CAJA", respuesta.nombre());
        assertTrue(respuesta.activo());
    }

    @Test
    void cambiarEstadoDebeDesactivarPresentacion() {

        Long id = 1L;

        Presentacion presentacion = new Presentacion();
        presentacion.setId(id);
        presentacion.setNombre("CAJA");
        presentacion.setActivo(true);

        PresentacionEstadoDTO dto = new PresentacionEstadoDTO(false);

        when(presentacionRepository.findById(id))
                .thenReturn(Optional.of(presentacion));

        when(presentacionRepository.save(any(Presentacion.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        PresentacionResponseDTO respuesta = presentacionService.cambiarEstado(id, dto);

        assertFalse(respuesta.activo());

        verify(presentacionRepository)
                .save(any(Presentacion.class));
    }
}