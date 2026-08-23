package com.dsd.rdstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dsd.rdstore.dto.tarifaiva.TarifaIvaRequestDTO;
import com.dsd.rdstore.dto.tarifaiva.TarifaIvaResponseDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.model.TarifaIva;
import com.dsd.rdstore.model.enums.TipoIva;
import com.dsd.rdstore.repository.TarifaIvaRepository;

@ExtendWith(MockitoExtension.class)
public class TarifaIvaServiceTest {

    @Mock
    private TarifaIvaRepository tarifaIvaRepository;

    @InjectMocks
    private TarifaIvaService tarifaIvaService;

    @Test
    void crearTarifaDebeCrearActiva() {

        TarifaIvaRequestDTO dto = new TarifaIvaRequestDTO(
                "IVA GENERAL 19%",
                TipoIva.GRAVADO,
                new BigDecimal("19.00"));

        when(tarifaIvaRepository
                .existsByNombreIgnoreCase(dto.nombre()))
                .thenReturn(false);

        when(tarifaIvaRepository
                .existsByTipoAndPorcentaje(
                        dto.tipo(),
                        dto.porcentaje()))
                .thenReturn(false);

        when(tarifaIvaRepository.save(any(TarifaIva.class)))
                .thenAnswer(invocacion -> {

                    TarifaIva tarifa = invocacion.getArgument(0);

                    tarifa.setId(1L);

                    return tarifa;
                });

        TarifaIvaResponseDTO respuesta = tarifaIvaService.crearTarifa(dto);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.id());

        assertEquals(
                new BigDecimal("19.00"),
                respuesta.porcentaje());

        assertEquals(
                TipoIva.GRAVADO,
                respuesta.tipo());

        assertTrue(respuesta.activo());
    }

    @Test
    void crearTarifaNoDebePermitirExentoConPorcentajeMayorACero() {

        TarifaIvaRequestDTO dto = new TarifaIvaRequestDTO(
                "EXENTO INCORRECTO",
                TipoIva.EXENTO,
                new BigDecimal("19.00"));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> tarifaIvaService.crearTarifa(dto));

        assertEquals(
                "Las tarifas EXENTO y EXCLUIDO deben tener porcentaje 0",
                exception.getMessage());

        verify(tarifaIvaRepository, never())
                .save(any(TarifaIva.class));
    }
}
