package com.dsd.rdstore.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.tarifaiva.TarifaIvaEstadoDTO;
import com.dsd.rdstore.dto.tarifaiva.TarifaIvaRequestDTO;
import com.dsd.rdstore.dto.tarifaiva.TarifaIvaResponseDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.TarifaIva;
import com.dsd.rdstore.model.enums.TipoIva;
import com.dsd.rdstore.repository.TarifaIvaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaIvaService {

    private final TarifaIvaRepository tarifaIvaRepository;


    public TarifaIvaResponseDTO crearTarifa(
            TarifaIvaRequestDTO dto) {

        String nombre = dto.nombre().trim();

        validarConfiguracion(
            dto.tipo(),
            dto.porcentaje()
        );

        if (tarifaIvaRepository
                .existsByNombreIgnoreCase(nombre)) {

            throw new DuplicateResourceException(
                "Ya existe una tarifa IVA con el nombre: "
                    + nombre
            );
        }

        if (tarifaIvaRepository
                .existsByTipoAndPorcentaje(
                    dto.tipo(),
                    dto.porcentaje()
                )) {

            throw new DuplicateResourceException(
                "Ya existe una tarifa "
                + dto.tipo()
                + " con porcentaje "
                + dto.porcentaje()
                + "%"
            );
        }

        TarifaIva tarifa = new TarifaIva();

        tarifa.setNombre(nombre);
        tarifa.setTipo(dto.tipo());
        tarifa.setPorcentaje(dto.porcentaje());
        tarifa.setActivo(true);

        TarifaIva tarifaGuardada =
            tarifaIvaRepository.save(tarifa);

        return mapResponse(tarifaGuardada);
    }


    public List<TarifaIvaResponseDTO> listarTarifas() {

        return tarifaIvaRepository
            .findAll()
            .stream()
            .map(this::mapResponse)
            .toList();
    }


    public List<TarifaIvaResponseDTO> listarTarifasActivas() {

        return tarifaIvaRepository
            .findByActivoTrueOrderByPorcentajeAsc()
            .stream()
            .map(this::mapResponse)
            .toList();
    }


    public TarifaIvaResponseDTO obtenerTarifaPorId(Long id) {

        return mapResponse(
            obtenerTarifa(id)
        );
    }


    public TarifaIvaResponseDTO actualizarTarifa(
            Long id,
            TarifaIvaRequestDTO dto) {

        // Primero comprobamos que exista
        TarifaIva tarifa =
            obtenerTarifa(id);

        String nombre =
            dto.nombre().trim();

        validarConfiguracion(
            dto.tipo(),
            dto.porcentaje()
        );

        if (tarifaIvaRepository
                .existsByNombreIgnoreCaseAndIdNot(
                    nombre,
                    id
                )) {

            throw new DuplicateResourceException(
                "Ya existe una tarifa IVA con el nombre: "
                    + nombre
            );
        }

        if (tarifaIvaRepository
                .existsByTipoAndPorcentajeAndIdNot(
                    dto.tipo(),
                    dto.porcentaje(),
                    id
                )) {

            throw new DuplicateResourceException(
                "Ya existe una tarifa "
                + dto.tipo()
                + " con porcentaje "
                + dto.porcentaje()
                + "%"
            );
        }

        tarifa.setNombre(nombre);
        tarifa.setTipo(dto.tipo());
        tarifa.setPorcentaje(dto.porcentaje());

        TarifaIva tarifaActualizada =
            tarifaIvaRepository.save(tarifa);

        return mapResponse(tarifaActualizada);
    }


    public TarifaIvaResponseDTO cambiarEstado(
            Long id,
            TarifaIvaEstadoDTO dto) {

        TarifaIva tarifa =
            obtenerTarifa(id);

        tarifa.setActivo(dto.activo());

        TarifaIva tarifaActualizada =
            tarifaIvaRepository.save(tarifa);

        return mapResponse(tarifaActualizada);
    }


    private TarifaIva obtenerTarifa(Long id) {

        return tarifaIvaRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Tarifa IVA",
                    id
                )
            );
    }


    private void validarConfiguracion(
            TipoIva tipo,
            BigDecimal porcentaje) {

        if (tipo == TipoIva.GRAVADO
                && porcentaje.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BusinessRuleException(
                "Una tarifa GRAVADO debe tener "
                + "un porcentaje mayor a 0"
            );
        }

        if ((tipo == TipoIva.EXENTO
                || tipo == TipoIva.EXCLUIDO)
                && porcentaje.compareTo(BigDecimal.ZERO) != 0) {

            throw new BusinessRuleException(
                "Las tarifas EXENTO y EXCLUIDO "
                + "deben tener porcentaje 0"
            );
        }
    }


    private TarifaIvaResponseDTO mapResponse(
            TarifaIva tarifa) {

        return new TarifaIvaResponseDTO(
            tarifa.getId(),
            tarifa.getNombre(),
            tarifa.getTipo(),
            tarifa.getPorcentaje(),
            tarifa.getActivo()
        );
    }
}