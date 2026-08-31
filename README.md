# 🛒 DStore - Backend

Backend de **DStore**, una aplicación web orientada a la gestión de ventas e inventario para pequeños comercios como papelerías, cacharrerías, tiendas locales y negocios que requieren control de existencias y vencimientos.

La API está desarrollada con **Java 21 y Spring Boot** y forma parte de una solución Full Stack integrada con un frontend en **Angular 20**.

DStore busca representar un sistema pequeño pero realista, priorizando reglas de negocio, seguridad, trazabilidad, consistencia de inventario y una arquitectura comprensible.

---

## 🔗 Repositorios

### Backend

Este repositorio contiene la API REST de DStore.

### Frontend

[DStore Frontend - Angular](https://github.com/blackbird17922se/dstore-front)

---

## 🎯 Objetivo del proyecto

El sistema cubre un flujo comercial completo:

```text
Catálogos y productos
        ↓
Entradas de inventario
        ↓
Existencias
        ↓
Ventas
        ↓
Consumo FEFO / FIFO
        ↓
Movimientos de inventario
        ↓
Anulación y reversión
```

Además, incorpora autenticación JWT, roles, gestión de usuarios, clientes, tarifas de IVA y perfil del usuario autenticado.

---

## 🚀 Tecnologías utilizadas

### Backend

- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Bean Validation
- MySQL
- Maven
- JUnit 5
- Mockito

### Frontend relacionado

- Angular 20
- TypeScript
- RxJS
- Angular Router
- HttpClient
- Functional Interceptors
- Route Guards

---

## 🧱 Arquitectura

El backend utiliza una arquitectura tradicional en capas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Complementada con:

```text
DTOs
Security
Exceptions
Validation
Enums
```

### Responsabilidades

**Controller**  
Expone los endpoints REST y gestiona las peticiones y respuestas HTTP.

**Service**  
Contiene reglas de negocio, validaciones y operaciones transaccionales.

**Repository**  
Gestiona el acceso a datos mediante Spring Data JPA.

**DTO**  
Define contratos específicos para entrada y salida de información.

**Security**  
Gestiona autenticación, autorización y validación del JWT.

**Exceptions**  
Centraliza errores técnicos y reglas de negocio.

---

## 🔐 Seguridad y autenticación

DStore implementa autenticación y autorización mediante **Spring Security + JWT**.

Actualmente incluye:

- Login.
- Contraseñas protegidas con BCrypt.
- Generación y validación de JWT.
- Filtro JWT.
- Sesiones stateless.
- Autorización por roles.
- Manejo personalizado de `401 Unauthorized`.
- Manejo personalizado de `403 Forbidden`.
- Protección de endpoints.
- Perfil del usuario autenticado.
- Cambio seguro de contraseña.
- Activación y desactivación de usuarios.

### Flujo de autenticación

```text
Usuario
   ↓
POST /api/v2/auth/login
   ↓
Spring Security
   ↓
Validación de credenciales
   ↓
JWT
   ↓
Cliente
   ↓
Authorization: Bearer TOKEN
   ↓
Filtro JWT
   ↓
Endpoint protegido
```

El usuario responsable de operaciones como ventas o ajustes se obtiene desde el contexto autenticado y no desde datos manipulables enviados por el cliente.

---

## 👤 Usuarios, roles y perfil

El módulo de usuarios permite:

- Crear usuarios.
- Listar usuarios.
- Editar información general.
- Asignar roles.
- Activar y desactivar usuarios.
- Evitar eliminación física como flujo principal.
- Consultar el perfil del usuario autenticado.
- Actualizar información personal.
- Cambiar contraseña validando previamente la contraseña actual.

La edición administrativa del usuario no utiliza la contraseña como parte del flujo normal de actualización.

---

## 🗂️ Catálogos

El backend administra catálogos utilizados por los productos:

- Categorías.
- Marcas.
- Presentaciones.
- Tarifas de IVA.

Incluye reglas como:

- Validación de duplicados.
- Activación y desactivación.
- Validación de relaciones activas antes de asociarlas a un producto.

---

## 📦 Productos

El módulo de productos permite:

- Crear productos.
- Actualizar productos.
- Consultar productos.
- Activar y desactivar productos.
- Código de barras opcional y único.
- Marca.
- Categoría.
- Presentación.
- Tarifa de IVA.
- Precio mediante `BigDecimal`.
- Configuración de control de vencimiento.

### Stock

El stock **no se almacena directamente en `Producto`**.

La fuente de verdad es `ExistenciaProducto`.

```text
Stock producto
    =
SUM(existencias.cantidad)
```

Esto permite soportar múltiples lotes, distintas fechas de ingreso y diferentes fechas de vencimiento sin duplicar el stock en varias entidades.

---

## 👥 Clientes

El módulo de clientes permite:

- Crear clientes.
- Editar clientes.
- Consultar clientes.
- Activar y desactivar clientes.
- Manejar diferentes tipos de documento.
- Registrar teléfono, correo, dirección y observaciones.

El cliente es opcional en una venta normal.

---

## 📥 Entradas de inventario

Una entrada de inventario representa mercancía recibida por el negocio.

Una misma entrada puede contener varios productos:

```text
Entrada #25
│
├── Leche x12
│   ├── Lote: L001
│   └── Vencimiento: 10/09/2026
│
├── Leche x24
│   ├── Lote: L002
│   └── Vencimiento: 25/09/2026
│
└── Lápices x50
    ├── Lote: -
    └── Vencimiento: -
```

Al registrar una entrada se generan:

```text
EntradaInventario
      ↓
DetalleEntradaInventario
      ↓
ExistenciaProducto
      ↓
MovimientoInventario
```

Las entradas se consideran históricas y no se editan ni eliminan como una operación CRUD convencional.

---

## 📦 Existencias

Cada `ExistenciaProducto` representa una cantidad específica disponible de un producto.

Puede contener:

- Producto.
- Cantidad actual.
- Número de lote opcional.
- Fecha de vencimiento opcional.
- Fecha de ingreso.

Una existencia nunca debe manejar cantidad negativa.

El backend permite consultar:

- Existencias generales.
- Existencias por producto.
- Existencias disponibles.
- Productos próximos a vencer.

---

## ⏳ Control de vencimientos

Los productos pueden configurarse con control de vencimiento.

Cuando un producto lo requiere:

- La fecha de vencimiento forma parte del control de inventario.
- Diferentes vencimientos pueden generar existencias independientes.
- Es posible consultar productos próximos a vencer.
- Durante una venta se utiliza estrategia **FEFO**.

```text
FEFO
First Expired, First Out

Vence primero
      ↓
Sale primero
```

Para productos que no manejan vencimiento se utiliza **FIFO**:

```text
FIFO
First In, First Out

Entra primero
      ↓
Sale primero
```

La selección de la existencia es responsabilidad del backend, no del frontend.

---

## 📜 Movimientos de inventario

Cada cambio significativo sobre una existencia genera un movimiento histórico.

Entre los tipos utilizados se encuentran:

- `ENTRADA`
- `VENTA`
- `AJUSTE_ENTRADA`
- `AJUSTE_SALIDA`
- `ANULACION_VENTA`

Cada movimiento conserva:

- Existencia afectada.
- Producto.
- Tipo de movimiento.
- Cantidad.
- Fecha.
- Tipo de origen.
- ID de la operación de origen.
- Observación.

Ejemplo:

```text
ENTRADA            +10
VENTA                -3
ANULACION_VENTA      +3
------------------------
Existencia actual    10
```

Los movimientos sirven como trazabilidad y no se eliminan para “deshacer” operaciones.

---

## 🔧 Ajustes de inventario

Los ajustes permiten corregir diferencias entre inventario físico y sistema.

Un ajuste puede:

- Incrementar una existencia.
- Disminuir una existencia.
- Registrar motivo y observación.
- Identificar al usuario responsable.
- Generar automáticamente un movimiento.
- Evitar cantidades negativas.

Ejemplo:

```text
Existencia actual: 24

Producto dañado:
AJUSTE_SALIDA: 3

Nueva existencia: 21

Movimiento:
AJUSTE_SALIDA -3
```

Las operaciones se ejecutan de forma transaccional para conservar la consistencia entre ajuste, existencia y movimiento.

---

## 🧾 IVA

DStore utiliza un catálogo de tarifas de IVA.

Los productos se relacionan con una tarifa que puede representar conceptos como:

- Gravado.
- Exento.
- Excluido.

Cada tarifa conserva:

- Tipo.
- Porcentaje.
- Estado.

### Precio final al público

En esta versión:

```text
Producto.precio
=
precio final al público con IVA incluido
```

Por ejemplo:

```text
Precio final: 11.900
IVA: 19%

Base: 10.000
IVA:   1.900
Total: 11.900
```

Durante una venta, el backend obtiene el precio y la tarifa directamente desde el producto y calcula la base y el IVA incluido.

---

## 🛒 Ventas

El módulo de ventas se encuentra integrado con inventario, usuarios, clientes e IVA.

### Registro de venta

El cliente envía únicamente la información necesaria:

```json
{
  "idCliente": null,
  "observacion": null,
  "detalles": [
    {
      "idProducto": 11,
      "cantidad": 2
    },
    {
      "idProducto": 6,
      "cantidad": 1
    }
  ]
}
```

El frontend **no decide**:

- Precio.
- IVA.
- Vendedor.
- Existencia o lote que debe consumirse.

El backend obtiene esos valores desde sus fuentes confiables.

### Flujo

```text
VentaRequest
    ↓
Usuario autenticado
    ↓
Cliente opcional
    ↓
Producto
    ↓
Precio + Tarifa IVA
    ↓
Venta
    ↓
DetalleVenta
    ↓
SalidaInventarioService
    ↓
FEFO / FIFO
    ↓
ExistenciaProducto
    ↓
MovimientoInventario
```

Las operaciones principales se ejecutan con `@Transactional`.

---

## 🧾 Detalle de venta

Cada detalle conserva una fotografía histórica de la operación.

Incluye información como:

- Producto.
- Cantidad.
- Precio unitario.
- Tipo de IVA.
- Porcentaje de IVA.
- Subtotal/base.
- Valor de IVA.
- Total.

Esto evita que una venta histórica cambie si posteriormente se modifica:

```text
Producto.precio
TarifaIva
```

El detalle representa las condiciones reales aplicadas al momento de vender.

---

## ❌ Anulación de ventas

Una venta confirmada puede ser anulada indicando un motivo.

La anulación:

- Cambia el estado de la venta.
- Registra fecha de anulación.
- Registra motivo.
- Busca los movimientos originales generados por la venta.
- Restaura exactamente las existencias afectadas.
- Conserva los movimientos originales.
- Genera movimientos inversos `ANULACION_VENTA`.
- Evita anular dos veces la misma venta mediante una regla de negocio.

Ejemplo:

```text
VENTA            -3   Existencia #11
ANULACION_VENTA  +3   Existencia #11
```

Esto mantiene la trazabilidad completa de la operación.

---

## ⚠️ Manejo de reglas de negocio

DStore utiliza una excepción específica para reglas de negocio:

```java
throw new NegocioExcepcion(
    "La venta ya se encuentra anulada"
);
```

Este tipo de excepción permite diferenciar errores funcionales de errores técnicos inesperados y devolver respuestas más claras al cliente.

---

## ✅ Validación

Los DTO utilizan Bean Validation para proteger los contratos de entrada.

Ejemplos de reglas utilizadas:

- `@NotNull`
- `@NotBlank`
- `@NotEmpty`
- `@Positive`
- `@Size`
- `@Valid`
- `@PastOrPresent`
- `@FutureOrPresent`

Las reglas críticas se validan además en la capa de servicio.

---

## 🧪 Pruebas

El proyecto utiliza:

- JUnit 5
- Mockito

Se realizan pruebas unitarias representativas principalmente sobre servicios y reglas de negocio.

Entre los escenarios trabajados se encuentran:

- Creación de productos.
- Código de barras duplicado.
- Validación de catálogos relacionados.
- Cambio de estado.
- Entradas de inventario.
- Ajustes de inventario.
- Stock insuficiente.
- Generación de movimientos.
- Validaciones de negocio.
- Seguridad y autenticación.

También se realizan pruebas funcionales de endpoints mediante Postman.

---

## 🌐 Principales endpoints

La API utiliza como base:

```text
/api/v2
```

Algunos recursos disponibles:

```text
/auth
/usuarios
/roles
/categorias
/marcas
/presentaciones
/tarifas-iva
/productos
/clientes
/entradas-inventario
/existencias
/movimientos-inventario
/ajustes-inventario
/ventas
```

Ejemplos del módulo de ventas:

```text
POST   /api/v2/ventas
GET    /api/v2/ventas
GET    /api/v2/ventas/{id}
PATCH  /api/v2/ventas/{id}/anular
```

Perfil del usuario:

```text
GET    /api/v2/usuarios/perfil
PUT    /api/v2/usuarios/perfil
PATCH  /api/v2/usuarios/perfil/contrasena
```

---

## ⚙️ Configuración

La aplicación utiliza variables de entorno para evitar almacenar credenciales y secretos dentro del repositorio.

Variables principales:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

Ejemplo:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

Zona horaria:

```text
America/Bogota
```

---

## ▶️ Ejecución

### Requisitos

- Java 21
- Maven
- MySQL

Clonar:

```bash
git clone https://github.com/blackbird17922se/rdstore.git
cd rdstore
```

Configurar:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

Compilar:

```bash
mvn clean install
```

Ejecutar:

```bash
mvn spring-boot:run
```

La API estará disponible normalmente en:

```text
http://localhost:8080
```

---

## ✅ Estado actual

### Seguridad

- ✅ Login.
- ✅ JWT.
- ✅ BCrypt.
- ✅ Autorización por roles.
- ✅ Manejo 401 / 403.
- ✅ Perfil del usuario.
- ✅ Cambio de contraseña.
- ✅ Activación y desactivación de usuarios.

### Catálogos y maestros

- ✅ Usuarios.
- ✅ Roles.
- ✅ Categorías.
- ✅ Marcas.
- ✅ Presentaciones.
- ✅ Tarifas de IVA.
- ✅ Productos.
- ✅ Clientes.

### Inventario

- ✅ Entradas de inventario.
- ✅ Detalle de entradas.
- ✅ Existencias.
- ✅ Stock calculado.
- ✅ Lotes.
- ✅ Control de vencimientos.
- ✅ Próximos a vencer.
- ✅ Movimientos.
- ✅ Ajustes.
- ✅ FEFO.
- ✅ FIFO.

### Ventas

- ✅ Registro de venta.
- ✅ Cliente opcional.
- ✅ Vendedor desde JWT.
- ✅ Precio final con IVA incluido.
- ✅ Snapshot histórico de IVA y precio.
- ✅ Consumo automático de inventario.
- ✅ Historial de ventas.
- ✅ Detalle de venta.
- ✅ Anulación.
- ✅ Reversión de inventario.
- ✅ Movimiento de anulación.

### Calidad

- ✅ DTO Request / Response.
- ✅ Bean Validation.
- ✅ Manejo de reglas de negocio.
- ✅ Operaciones transaccionales.
- ✅ JUnit 5.
- ✅ Mockito.
- ✅ Integración funcional con Angular.

---

## 🗺️ Alcance de esta versión

Esta versión se concentra en completar el flujo principal de:

```text
Seguridad
Productos
Inventario
Clientes
Ventas
```

No forman parte del alcance actual:

- Apertura y cierre de caja.
- Balance de caja.
- Medios de pago configurables.
- Proveedores.
- Compras a proveedores.
- Crédito / ventas fiadas.
- Abonos.
- Cuentas por cobrar.
- Reportería avanzada.

Estas funcionalidades quedan como posibles evoluciones posteriores y no son necesarias para completar el flujo actual de ventas e inventario.

---

## 🚀 Posibles mejoras futuras

- Dashboard con indicadores.
- Reportes de ventas e inventario.
- Paginación y filtros avanzados.
- Notificaciones de vencimiento.
- Refresh Token.
- Auditoría adicional.
- Caja y medios de pago.
- Proveedores y compras.
- Ventas a crédito.
- Cuentas por cobrar.
- Registro formal de descuadres de inventario.
- Mayor cobertura de pruebas automatizadas.
- Docker.
- CI/CD.

---

## 🧠 Decisiones de diseño destacadas

### ¿Por qué `Producto` no almacena stock?

Porque un producto puede tener múltiples existencias, lotes y vencimientos. El stock se deriva de `ExistenciaProducto`.

### ¿Por qué FEFO y FIFO?

Los productos perecederos deben consumir primero lo que vence antes. Los productos sin vencimiento consumen primero lo que ingresó antes.

### ¿Por qué `DetalleVenta` guarda precio e IVA?

Porque una venta debe conservar las condiciones históricas aplicadas aunque después cambie el producto o su tarifa.

### ¿Por qué la anulación genera otro movimiento?

Porque los movimientos son trazabilidad histórica. Anular no significa borrar lo sucedido, sino registrar la operación inversa.

### ¿Por qué el vendedor no viene en `VentaRequest`?

Porque el backend lo obtiene del usuario autenticado mediante JWT, evitando que el cliente pueda atribuir una venta a otro usuario.

---

## 🎯 Objetivo de aprendizaje

DStore también forma parte de un proyecto personal orientado al fortalecimiento de habilidades Full Stack.

El backend permite aplicar y demostrar conocimientos de:

- Java 21.
- Spring Boot.
- Spring Security.
- JWT.
- JPA / Hibernate.
- SQL y modelado relacional.
- APIs REST.
- DTOs.
- Bean Validation.
- Manejo de excepciones.
- Transacciones.
- Reglas de negocio.
- Inventario.
- FEFO / FIFO.
- Trazabilidad.
- JUnit 5.
- Mockito.
- Integración con Angular.

---

## 👨‍💻 Autor

**Mauricio Alarcón**

Proyecto personal orientado al fortalecimiento de habilidades en desarrollo Full Stack con **Java, Spring Boot y Angular**.
