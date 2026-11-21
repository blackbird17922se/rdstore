package com.dsd.rdstore.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsd.rdstore.common.util.SpecificationPageUtils;
import com.dsd.rdstore.dto.common.PageResponseDTO;
import com.dsd.rdstore.dto.cliente.ClienteEstadoDTO;
import com.dsd.rdstore.dto.cliente.ClienteRequestDTO;
import com.dsd.rdstore.dto.cliente.ClienteResponseDTO;
import com.dsd.rdstore.exception.DuplicateResourceException;
import com.dsd.rdstore.exception.ResourceNotFoundException;
import com.dsd.rdstore.model.Cliente;
import com.dsd.rdstore.repository.ClienteRepository;
import com.dsd.rdstore.specification.ClienteSpecification;

import lombok.RequiredArgsConstructor;

/** @author Mauricio Alarcon */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final String RECURSO = "Cliente";

    // Solo estas propiedades de Cliente se pueden usar para ordenar desde la API.
    private static final Set<String> CAMPOS_ORDEN = Set.of(
        "id", "nombresApellidos", "numeroDocumento", "fechaRegistro"
    );


    public ClienteResponseDTO crearCliente(ClienteRequestDTO dto) {

        String nombre = dto.nombresApellidos().trim();

        if (clienteRepository.existsByNombresApellidosIgnoreCase(nombre)) {
            throw new DuplicateResourceException(RECURSO, nombre);
        }

        Cliente cliente = new Cliente();

        mapearRequest(dto, cliente);

        cliente.setNombresApellidos(nombre);
        cliente.setFechaRegistro(LocalDate.now());
        cliente.setActivo(true);

        Cliente guardado = clienteRepository.save(cliente);

        return mapearResponse(guardado);
    }


    public List<ClienteResponseDTO> listarClientes(){

        return clienteRepository
            .findAll()
            .stream()
            .map(this::mapearResponse)
            .toList();
    }


    /**
     * Consulta paginada para el mantenimiento de clientes.
     * Se conservan los listados anteriores para no afectar otros procesos.
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<ClienteResponseDTO> listarClientesPaginados(
            String nombre,
            String documento,
            Boolean activo,
            int page,
            int size,
            String ordenarPor,
            String direccion) {

        return SpecificationPageUtils.buscar(
            clienteRepository,
            ClienteSpecification.filtrar(nombre, documento, activo),
            this::mapearResponse,
            page, size, ordenarPor, direccion, CAMPOS_ORDEN
        );
    }


    public List<ClienteResponseDTO> listarClientesActivos(){

        return clienteRepository
            .findByActivoTrueOrderByNombresApellidosAsc()
            .stream()
            .map(this::mapearResponse)
            .toList();
    }


    public ClienteResponseDTO actualizarCliente(Long id ,ClienteRequestDTO dto) {

        Cliente cliente = obtenerClienteRepository(id);

        String nombre = dto.nombresApellidos().trim();

        if (clienteRepository
                .existsByNombresApellidosIgnoreCaseAndIdNot(nombre, id)) {

            throw new DuplicateResourceException(RECURSO, nombre);
        }

        mapearRequest(dto, cliente);

        cliente.setNombresApellidos(nombre);

        Cliente guardado = clienteRepository.save(cliente);

        return mapearResponse(guardado);
    }


    public ClienteResponseDTO cambiarEstado(Long id ,ClienteEstadoDTO dto){

        Cliente cliente = obtenerClienteRepository(id);

        cliente.setActivo(dto.activo());

        Cliente actualizado = clienteRepository.save(cliente);

        return mapearResponse(actualizado);
    }


    public ClienteResponseDTO obtenerClientePorId(Long id){

        return mapearResponse(
            obtenerClienteRepository(id)
        );
    }


    private Cliente obtenerClienteRepository(Long id){

        return clienteRepository
            .findById(id)
            .orElseThrow(()->
                new ResourceNotFoundException(RECURSO, id)
        );
    }

    private ClienteResponseDTO mapearResponse(Cliente cliente) {

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getNombresApellidos(),
                cliente.getTelefono(),
                cliente.getCorreo(),
                cliente.getDireccion(),
                cliente.getObservacion(),
                cliente.getFechaRegistro(),
                cliente.getActivo());
    }


    private void mapearRequest(
            ClienteRequestDTO dto,
            Cliente cliente) {

        cliente.setTipoDocumento(dto.tipoDocumento());
        cliente.setNumeroDocumento(dto.numeroDocumento());
        cliente.setTelefono(dto.telefono());
        cliente.setCorreo(dto.correo());
        cliente.setDireccion(dto.direccion());
        cliente.setObservacion(dto.observacion());
    }

}
