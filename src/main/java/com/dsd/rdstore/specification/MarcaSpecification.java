package com.dsd.rdstore.specification;
import org.springframework.data.jpa.domain.Specification;
import com.dsd.rdstore.model.Marca;
import com.dsd.rdstore.specification.common.CommonSpecifications;

public class MarcaSpecification {
        private MarcaSpecification() {}

    public static Specification<Marca> filtrar(String nombre, Boolean activo) {

        return CommonSpecifications.<Marca>nombreContiene(nombre)
            .and(CommonSpecifications.<Marca>tieneEstado(activo));
    }
}
