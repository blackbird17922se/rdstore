package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

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


    public CategoriaResponseDTO crearCategorias(CategoriaRequestDTO dto){

        String nombre = dto.nombre().trim();

        if(categoriaRepository.existsByNombreIgnoreCase(nombre)){
            throw new DuplicateResourceException(
                "Ya existe una categoría con el nombre: " + nombre
            );
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);

        Categoria categoriaCreada = categoriaRepository.save(categoria);

        return new CategoriaResponseDTO(
            categoriaCreada.getId(),
            categoriaCreada.getNombre()
        );
    }


    public List<CategoriaResponseDTO> listarCategorias(){

        return categoriaRepository.findAll()
        .stream()
        .map(categoria -> mapearResponse(categoria))
        .toList();

    }


    public CategoriaResponseDTO obtenerCategoriaPorId(Long id){

        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Categoría", id));

        return mapearResponse(categoria);

    }


    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO dto){

        String nombre = dto.nombre().trim();

        // si uso existsByNombreIgnoreCase, se encontrara a si mismo y no funcionara
        if(categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)){
            throw new DuplicateResourceException(
                "Ya existe una categoría con el nombre: " + nombre
            );
        }

        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Categoria", id));

        categoria.setNombre(nombre);

        Categoria categoriaActualizada =
            categoriaRepository.save(categoria);

        return mapearResponse(categoriaActualizada);
    }


    public void eliminarCategoria(Long id){

        if(!categoriaRepository.existsById(id)){
            throw new ResourceNotFoundException("Categoria", id);
        }

        categoriaRepository.deleteById(id);

    }

    private CategoriaResponseDTO mapearResponse(Categoria categoria) {

        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre());
    }

}
