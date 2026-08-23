package com.dsd.rdstore.controller;

import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.rol.RolResponseDTO;
import com.dsd.rdstore.service.RolService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/roles")
public class RolController {

    private final RolService rolService;

    @GetMapping
    public ResponseEntity<List<RolResponseDTO>> listarRoles() {
        return ResponseEntity.ok(rolService.listarRoles());
    }

}
