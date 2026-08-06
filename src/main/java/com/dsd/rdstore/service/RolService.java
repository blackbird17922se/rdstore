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

        // el map convierte cada Rol en un RolDTO.
        return rolRepository.findAll()
            .stream()
            .map(rol -> new RolResponseDTO(
                rol.getId(),
                rol.getNombre()
            ))
            .toList();
    }

    /* Los roles del sistema son predefinidos y se utilizan para controlar 
    permisos básicos de acceso. En esta versión, los usuarios administradores 
    pueden asignar roles existentes, pero no crear nuevos roles personalizados. */

}
