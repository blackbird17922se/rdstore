package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.categoria.CategoriaRequestDTO;
import com.dsd.rdstore.dto.categoria.CategoriaResponseDTO;
import com.dsd.rdstore.model.Categoria;
import com.dsd.rdstore.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;


    public List<CategoriaResponseDTO> listarcategoria(){

        return categoriaRepository.findAll()
        .stream()
        .map(categoria -> new CategoriaResponseDTO(
            categoria.getId(),
            categoria.getNombre()
        )).toList();
        
    }

    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto){

        Categoria categoria = new Categoria();
        categoria.setNombre(dto.nombre());

        Categoria categoriaCreada = categoriaRepository.save(categoria);

        return new CategoriaResponseDTO(
            categoriaCreada.getId(),
            categoriaCreada.getNombre()
        );
    }

}
