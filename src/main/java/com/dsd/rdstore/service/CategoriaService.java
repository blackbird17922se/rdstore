package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.categoria.CategoriaEstadoDTO;
import com.dsd.rdstore.dto.categoria.CategoriaRequestDTO;
import com.dsd.rdstore.dto.categoria.CategoriaResponseDTO;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Categoria;
import com.dsd.rdstore.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private final String RECURSO = "Categoría";


    public CategoriaResponseDTO crearCategorias(CategoriaRequestDTO dto) {

        String nombre = dto.nombre().trim();

        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new DuplicateResourceException( RECURSO ,nombre);
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setActivo(true);

        Categoria categoriaCreada = categoriaRepository.save(categoria);

        return mapearResponse(categoriaCreada);
    }


    public List<CategoriaResponseDTO> listarCategorias() {

        return categoriaRepository.findAll()
                .stream()
                .map(categoria -> mapearResponse(categoria))
                .toList();

    }

    public CategoriaResponseDTO obtenerCategoriaPorId(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));

        return mapearResponse(categoria);

    }

    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO dto) {

        String nombre = dto.nombre().trim();

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));


        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new DuplicateResourceException(RECURSO, nombre);
        }

        categoria.setNombre(nombre);

        Categoria categoriaActualizada = categoriaRepository.save(categoria);

        return mapearResponse(categoriaActualizada);
    }


    public CategoriaResponseDTO cambiarEstado(
            Long id,
            CategoriaEstadoDTO dto) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(RECURSO,id)
                );

        categoria.setActivo(dto.activo());

        Categoria guardada =
                categoriaRepository.save(categoria);

        return mapearResponse(guardada);
    }


    // private void validarCategoriaSinProductos(Long id) {

    //     if (productoRepository.existsByCategoriaId(id)) {
    //         throw new BusinessRuleException(
    //                 "No se puede eliminar la categoría porque tiene productos asociados"
    //         );
    //     }
    // }

    public List<CategoriaResponseDTO> listarActivas() {

        return categoriaRepository
                .findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(this::mapearResponse)
                .toList();
    }

    private CategoriaResponseDTO mapearResponse(Categoria categoria) {

        return new CategoriaResponseDTO(
            categoria.getId(),
            categoria.getNombre(),
            categoria.getActivo());
    }

}
