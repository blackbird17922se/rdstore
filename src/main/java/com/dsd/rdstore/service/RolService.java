package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.RolDTO;
import com.dsd.rdstore.repository.RolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;


    public List<RolDTO> listarRoles(){

        // el map convierte cada Rol en un RolDTO.
        return rolRepository.findAll()
            .stream()
            .map(rol -> new RolDTO(
                rol.getId(),
                rol.getNombre()
            ))
            .toList();
    }

}
