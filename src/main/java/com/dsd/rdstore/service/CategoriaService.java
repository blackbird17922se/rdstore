package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.categoria.CategoriaRequestDTO;
import com.dsd.rdstore.dto.categoria.CategoriaResponseDTO;
import com.dsd.rdstore.exception.BusinessRuleException;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Categoria;
import com.dsd.rdstore.repository.CategoriaRepository;
import com.dsd.rdstore.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaResponseDTO crearCategorias(CategoriaRequestDTO dto) {

        String nombre = dto.nombre().trim();

        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new DuplicateResourceException(
                    "Ya existe una categoría con el nombre: " + nombre);
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);

        Categoria categoriaCreada = categoriaRepository.save(categoria);

        return new CategoriaResponseDTO(
                categoriaCreada.getId(),
                categoriaCreada.getNombre());
    }

    public List<CategoriaResponseDTO> listarCategorias() {

        return categoriaRepository.findAll()
                .stream()
                .map(categoria -> mapearResponse(categoria))
                .toList();

    }

    public CategoriaResponseDTO obtenerCategoriaPorId(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));

        return mapearResponse(categoria);

    }

    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO dto) {

        String nombre = dto.nombre().trim();

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));


        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new DuplicateResourceException(
                    "Ya existe una categoría con el nombre: " + nombre);
        }

        categoria.setNombre(nombre);

        Categoria categoriaActualizada = categoriaRepository.save(categoria);

        return mapearResponse(categoriaActualizada);
    }


    public void eliminarCategoria(Long id) {

        Categoria categoria = obtenerCategoriaPorIdF(id);

        validarCategoriaSinProductos(id);

        categoriaRepository.delete(categoria);
    }

    private Categoria obtenerCategoriaPorIdF(Long id) {

        return categoriaRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría",
                        id));
    }

    private CategoriaResponseDTO mapearResponse(Categoria categoria) {

        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre());
    }

    private void validarCategoriaSinProductos(Long id) {

        if (productoRepository.existsByCategoriaId(id)) {
            throw new BusinessRuleException(
                    "No se puede eliminar la categoría porque tiene productos asociados"
            );
        }
    }

}
