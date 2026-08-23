package com.dsd.rdstore.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dsd.rdstore.dto.rol.RolResponseDTO;
import com.dsd.rdstore.model.Rol;
import com.dsd.rdstore.repository.RolRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    @Test
    void listarRoles_deberiaRetornarListaDeRolesDTO() {

        Rol admin = new Rol();
        admin.setId(2L);
        admin.setNombre("ADMIN");

        Rol vendedor = new Rol();
        vendedor.setId(3L);
        vendedor.setNombre("VENDEDOR");

        when(rolRepository.findAll())
                .thenReturn(List.of(admin, vendedor));

        // Act
        List<RolResponseDTO> resultado = rolService.listarRoles();

        // Assert
        assertThat(resultado).hasSize(2);

        assertThat(resultado.get(0).id()).isEqualTo(2L);
        assertThat(resultado.get(0).nombre()).isEqualTo("ADMIN");

        assertThat(resultado.get(1).id()).isEqualTo(3L);
        assertThat(resultado.get(1).nombre()).isEqualTo("VENDEDOR");

        verify(rolRepository).findAll();
    }

}
