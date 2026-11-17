package com.dsd.rdstore.service;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import com.dsd.rdstore.common.pagination.PaginationUtils;
import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.specification.PresentacionSpecification;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.presentacion.PresentacionEstadoDTO;
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

    private static final Set<String> CAMPOS_ORDEN = Set.of("id", "nombre");

    @Transactional(readOnly = true)
    public PageResponseDTO<PresentacionResponseDTO> buscarPaginadas(
            String nombre, Boolean activo, int page, int size, String ordenarPor, String direccion) {

        Pageable pageable = PaginationUtils.crear(
                page, size, ordenarPor, direccion, CAMPOS_ORDEN);
        Page<PresentacionResponseDTO> resultados = presentacionRepository
                .findAll(PresentacionSpecification.filtrar(nombre, activo), pageable)
                .map(this::mapResponse);
        return PageResponseDTO.from(resultados);
    }



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
        presentacion.setActivo(true);

        Presentacion guardada =
            presentacionRepository.save(presentacion);

        return mapResponse(guardada);
    }


    public List<PresentacionResponseDTO> listarPresentaciones() {

        return presentacionRepository
            .findAll()
            .stream()
            .map(this::mapResponse)
            .toList();
    }


    public List<PresentacionResponseDTO> listarPresentacionesActivas() {

        return presentacionRepository
            .findByActivoTrueOrderByNombreAsc()
            .stream()
            .map(this::mapResponse)
            .toList();
    }


    public PresentacionResponseDTO obtenerPresentacionPorId(Long id) {

        return mapResponse(
            obtenerPresentacion(id)
        );
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

        Presentacion actualizada =
            presentacionRepository.save(presentacion);

        return mapResponse(actualizada);
    }


    public PresentacionResponseDTO cambiarEstado(
            Long id,
            PresentacionEstadoDTO dto) {

        Presentacion presentacion =
            obtenerPresentacion(id);

        presentacion.setActivo(dto.activo());

        Presentacion actualizada =
            presentacionRepository.save(presentacion);

        return mapResponse(actualizada);
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
            presentacion.getNombre(),
            presentacion.getActivo()
        );
    }
}