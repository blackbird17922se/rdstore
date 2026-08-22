package com.dsd.rdstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.dsd.rdstore.dto.marca.MarcaEstadoDTO;
import com.dsd.rdstore.dto.marca.MarcaRequestDTO;
import com.dsd.rdstore.dto.marca.MarcaResponseDTO;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.model.Marca;
import com.dsd.rdstore.repository.MarcaRepository;

@ExtendWith(MockitoExtension.class)
public class MarcaServiceTest {

    @Mock
    private MarcaRepository marcaRepository;

    @InjectMocks
    private MarcaService marcaService;

    private final String MARCA = "Bic";

    @Test
    void crearMarcaDebeCrearCorrectamente(){

        // arrange
        MarcaRequestDTO dto = new MarcaRequestDTO(MARCA);

        when(marcaRepository.existsByNombreIgnoreCase(MARCA))
            .thenReturn(false);

        Marca guardada = new Marca();
        guardada.setId(1L);
        guardada.setNombre(MARCA);
        //guardada.setActivo(true);

        when(marcaRepository.save(any(Marca.class)))
            .thenReturn(guardada);

        // act
        MarcaResponseDTO respuesta = marcaService.crearMarca(dto);

        // assert
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.id());
        assertEquals(MARCA, respuesta.nombre());

        verify(marcaRepository)
            .save(any(Marca.class));

    }

    @Test
    void crearMarcaNoDebePermitirDuplicados(){

        MarcaRequestDTO dto = new MarcaRequestDTO(MARCA);

        when(marcaRepository.existsByNombreIgnoreCase(MARCA))
            .thenReturn(true);

        assertThrows(DuplicateResourceException.class, 
            () -> marcaService.crearMarca(dto));
        
        verify(marcaRepository, never()).save(any(Marca.class));

    }


    @Test
    void actualizarMarcaActualizarCorrectamente(){

        Long id =1L;

        Marca existente =new Marca();
        existente.setId(id);
        existente.setNombre("Colgate");
        existente.setActivo(true);

        MarcaRequestDTO dto = new MarcaRequestDTO(MARCA);

        when(marcaRepository.findById(id))
            .thenReturn(Optional.of(existente));

        when(marcaRepository.existsByNombreIgnoreCaseAndIdNot(
            dto.nombre(), id))
            .thenReturn(false);
        when(marcaRepository.save(any(Marca.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

        MarcaResponseDTO respuesta = marcaService.actualizarMarca(id, dto);

        assertEquals(id, respuesta.id());
        assertEquals(MARCA, respuesta.nombre());

        verify(marcaRepository).save(any(Marca.class));

    }


    @Test
    void cambiarEstadoDebeDesactivarMarca(){

        Long id = 1L;

        Marca existente = new Marca();
        existente.setId(id);
        existente.setNombre(MARCA);
        existente.setActivo(true);

        MarcaEstadoDTO dto = new MarcaEstadoDTO(false);

        when(marcaRepository.findById(id))
            .thenReturn(Optional.of(existente));

        when(marcaRepository.save(any(Marca.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

        MarcaResponseDTO respuesta = marcaService.cambiarEstado(id, dto);

        assertNotNull(respuesta);
        assertFalse(respuesta.activo());

        verify(marcaRepository).save(any(Marca.class));

    }

    @Test
    void listarMarcasRetornarLista(){

        Marca marca = new Marca();
        marca.setId(1L);
        marca.setNombre(MARCA);
        marca.setActivo(true);

        Marca marca2 = new Marca();
        marca2.setId(2L);
        marca2.setNombre("Axion");
        marca2.setActivo(false);

        when(marcaRepository.findAll())
            .thenReturn(List.of(marca, marca2));

        List<MarcaResponseDTO> lista = marcaService.listarMarcas();

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).id()).isEqualTo(1L);
        assertThat(lista.get(0).nombre()).isEqualTo(MARCA);

        assertThat(lista.get(1).id()).isEqualTo(2L);
        assertThat(lista.get(1).nombre()).isEqualTo("Axion");
    }



}
