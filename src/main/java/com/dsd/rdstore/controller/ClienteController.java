package com.dsd.rdstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsd.rdstore.dto.cliente.ClienteResponseDTO;
import com.dsd.rdstore.dto.cliente.ClienteEstadoDTO;
import com.dsd.rdstore.dto.cliente.ClienteRequestDTO;
import com.dsd.rdstore.service.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes(){
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<ClienteResponseDTO>> listarClientesActivos() {
        return ResponseEntity.ok(clienteService.listarClientesActivos());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerClientePorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerClientePorId(id));
    }


    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(
        @Valid @RequestBody ClienteRequestDTO dto) {

            ClienteResponseDTO response = clienteService.crearCliente(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
        @PathVariable Long id, @Valid @RequestBody ClienteRequestDTO dto) {

        return ResponseEntity.ok(clienteService.actualizarCliente(id, dto));
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<ClienteResponseDTO> cambiarEstado(
        @PathVariable Long id, @Valid @RequestBody ClienteEstadoDTO dto){

            return ResponseEntity.ok(clienteService.cambiarEstado(id, dto));
    }

}
