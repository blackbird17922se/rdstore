package com.dsd.rdstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dsd.rdstore.dto.rol.RolResponseDTO;
import com.dsd.rdstore.repository.RolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<RolResponseDTO> listarRoles(){

        return rolRepository.findAll()
            .stream()
            .map(rol -> new RolResponseDTO(
                rol.getId(),
                rol.getNombre()
            ))
            .toList();
    }

}
