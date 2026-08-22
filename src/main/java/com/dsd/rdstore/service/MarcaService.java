package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.marca.MarcaEstadoDTO;
import com.dsd.rdstore.dto.marca.MarcaRequestDTO;
import com.dsd.rdstore.dto.marca.MarcaResponseDTO;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Marca;
import com.dsd.rdstore.repository.MarcaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;

    private final String RECURSO = "Marca";


    public MarcaResponseDTO crearMarca(MarcaRequestDTO dto){

        String nombre = dto.nombre().trim();

        if (marcaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new DuplicateResourceException( RECURSO ,nombre);
        }

        Marca marca = new Marca();
        marca.setNombre(nombre);
        marca.setActivo(true);

        Marca guardado = marcaRepository.save(marca);

        return mapResponse(guardado);
    }


    public List<MarcaResponseDTO> listarMarcas(){

        return marcaRepository
            .findAll()
            .stream()
            .map(this::mapResponse)
            .toList();
    }


    public List<MarcaResponseDTO> listarMarcasActivas(){

        return marcaRepository
            .findByActivoTrueOrderByNombreAsc()
            .stream()
            .map(this::mapResponse)
            .toList();

    }


    public MarcaResponseDTO obtenerMarcaPorId(Long id){

        return mapResponse(
            obtenerMarcaRepository(id)
        );
    }


    public MarcaResponseDTO actualizarMarca(Long id, MarcaRequestDTO dto){

        Marca marca = obtenerMarcaRepository(id);

        String nombre = dto.nombre().trim();

        if (marcaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new DuplicateResourceException(RECURSO, id);
        }

        marca.setNombre(nombre);

        Marca actualizada = marcaRepository.save(marca);

        return mapResponse(actualizada);
    }


    public MarcaResponseDTO cambiarEstado(Long id, MarcaEstadoDTO dto){

        Marca marca = obtenerMarcaRepository(id);

        marca.setActivo(dto.activo());

        Marca actualizada = marcaRepository.save(marca);

        return mapResponse(actualizada);
    }


    private Marca obtenerMarcaRepository(Long id) {

        return marcaRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(RECURSO,id)
            );
    }


    private MarcaResponseDTO mapResponse(
            Marca marca) {

        return new MarcaResponseDTO(
                marca.getId(),
                marca.getNombre(),
                marca.getActivo());
    }

}
