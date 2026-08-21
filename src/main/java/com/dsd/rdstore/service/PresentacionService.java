package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.presentacion.PresentacionRequestDTO;
import com.dsd.rdstore.dto.presentacion.PresentacionResponseDTO;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Presentacion;
import com.dsd.rdstore.repository.PresentacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresentacionService {

    private final PresentacionRepository presentacionRepository;


    public PresentacionResponseDTO crearPresentacion(
            PresentacionRequestDTO dto) {

        String nombre = dto.nombre().trim();

        if (presentacionRepository
                .existsByNombreIgnoreCase(nombre)) {

            throw new DuplicateResourceException(
                "Ya existe una presentación con el nombre: " + nombre
            );
        }

        Presentacion presentacion = new Presentacion();
        presentacion.setNombre(nombre);

        Presentacion presentacionGuardada =
            presentacionRepository.save(presentacion);

        return mapResponse(presentacionGuardada);
    }


    public List<PresentacionResponseDTO> listarPresentaciones() {

        return presentacionRepository
            .findAll()
            .stream()
            .map(this::mapResponse)
            .toList();
    }


    public PresentacionResponseDTO obtenerPresentacionPorId(Long id) {

        Presentacion presentacion =
            obtenerPresentacion(id);

        return mapResponse(presentacion);
    }


    public PresentacionResponseDTO actualizarPresentacion(
            Long id,
            PresentacionRequestDTO dto) {

        Presentacion presentacion =
            obtenerPresentacion(id);

        String nombre = dto.nombre().trim();

        if (presentacionRepository
                .existsByNombreIgnoreCaseAndIdNot(nombre, id)) {

            throw new DuplicateResourceException(
                "Ya existe una presentación con el nombre: " + nombre
            );
        }

        presentacion.setNombre(nombre);

        Presentacion presentacionActualizada =
            presentacionRepository.save(presentacion);

        return mapResponse(presentacionActualizada);
    }


    private Presentacion obtenerPresentacion(Long id) {

        return presentacionRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Presentación",
                    id
                )
            );
    }


    private PresentacionResponseDTO mapResponse(
            Presentacion presentacion) {

        return new PresentacionResponseDTO(
            presentacion.getId(),
            presentacion.getNombre()
        );
    }
}