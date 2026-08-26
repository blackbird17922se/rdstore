# 🛒 DStore - Sistema de Gestión de Ventas e Inventario

DStore es una aplicación web orientada a la gestión de ventas e inventario para pequeños negocios como papelerías, cacharrerías, tiendas locales y comercios con productos que requieren control de vencimiento.

El proyecto se desarrolla como una solución Full Stack utilizando **Java, Spring Boot y Angular**, con énfasis en buenas prácticas, seguridad, trazabilidad de inventario y reglas de negocio reales.

Además de su propósito funcional, DStore forma parte de mi portafolio profesional y de mi proceso de fortalecimiento en desarrollo Full Stack.

---

## 🚀 Tecnologías utilizadas

### Backend

- Java 21
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- JPA / Hibernate
- Bean Validation
- MySQL
- JUnit 5
- Mockito
- Maven

### Frontend

- Angular
- TypeScript
- RxJS
- Angular Router
- Interceptors
- Guards

> El frontend se encuentra en un repositorio independiente y actualmente está siendo adaptado a la versión actual del backend.

### Base de datos

- MySQL

---

## 📂 Repositorios

### Backend

[DStore Backend](https://github.com/blackbird17922se/rdstore)

### Frontend

[DStore Frontend](https://github.com/blackbird17922se/dstore-front)

---

## 🔐 Seguridad

El backend implementa autenticación y autorización mediante Spring Security y JWT.

Actualmente incluye:

- Login de usuarios.
- Contraseñas protegidas con BCrypt.
- Generación y validación de JWT.
- Filtro JWT para autenticación de peticiones.
- Control de acceso por roles.
- Manejo personalizado de errores `401 Unauthorized`.
- Manejo personalizado de errores `403 Forbidden`.
- Gestión del perfil del usuario autenticado.
- Cambio seguro de contraseña.
- Sesiones stateless.

### Roles actuales

| Rol | Responsabilidad |
| --- | --- |
| `ADMIN` | Administración del sistema, productos e inventario |
| `VENDEDOR` | Operaciones permitidas según las reglas de seguridad |

---

## 🧠 Flujo de autenticación

1. El usuario envía sus credenciales al backend.
2. Spring Security valida el usuario y la contraseña.
3. El backend genera un JWT firmado.
4. El cliente almacena el token.
5. Cada petición protegida envía:

```text
Authorization: Bearer TOKEN
```

6. El filtro JWT valida el token.
7. Spring Security identifica al usuario y sus roles.
8. El acceso al endpoint se permite o rechaza según sus permisos.

---

## 📦 Módulos implementados

### 👥 Usuarios y seguridad

- Gestión de usuarios.
- Gestión de roles.
- Activación y desactivación de usuarios.
- Cambio de contraseña.
- Perfil propio del usuario.
- Autenticación JWT.
- Autorización por roles.

### 🗂️ Catálogos

- Categorías.
- Marcas.
- Presentaciones.
- Tarifas de IVA.
- Activación y desactivación de catálogos cuando aplica.
- Validación de registros duplicados.

### 📦 Productos

- Registro de productos.
- Código de barras opcional y único.
- Marca.
- Categoría.
- Presentación.
- Tarifa de IVA.
- Precio mediante `BigDecimal`.
- Activación y desactivación.
- Configuración de control de vencimiento.
- Validación de relaciones activas.

El stock no se almacena directamente en el producto.

El stock disponible se obtiene a partir de las existencias registradas en inventario.

---

## 🏬 Gestión de inventario

El inventario fue diseñado para mantener trazabilidad sobre el origen y estado actual de las existencias.

### Entradas de inventario

Una entrada representa mercancía recibida por el negocio.

Ejemplo:

```text
Entrada de inventario
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

Una misma entrada puede contener múltiples productos e incluso varias existencias del mismo producto con diferentes fechas de vencimiento.

### Existencias

Cada existencia representa una cantidad específica de un producto.

Puede contener:

- Producto.
- Cantidad disponible.
- Número de lote opcional.
- Fecha de vencimiento opcional.
- Fecha de ingreso.

Esto permite controlar productos perecederos sin obligar a todos los productos a utilizar lotes o vencimientos.

### Stock

El stock actual se calcula mediante las existencias:

```text
Stock producto = suma de sus existencias disponibles
```

Esto evita mantener el mismo dato duplicado entre `Producto` y `ExistenciaProducto`.

---

## 📜 Movimientos de inventario

Cada cambio realizado sobre una existencia genera un movimiento histórico.

Tipos actualmente contemplados:

- `ENTRADA`
- `VENTA`
- `AJUSTE_ENTRADA`
- `AJUSTE_SALIDA`

Ejemplo:

```text
ENTRADA         +24
AJUSTE_SALIDA    -3
-------------------
Existencia actual 21
```

Cada movimiento conserva:

- Existencia afectada.
- Producto.
- Tipo de movimiento.
- Cantidad.
- Fecha.
- Tipo de operación que lo originó.
- ID de la operación de origen.
- Observación.

Los movimientos son registros históricos y no se modifican ni eliminan directamente.

---

## 🔧 Ajustes de inventario

Los ajustes permiten corregir diferencias detectadas entre el inventario físico y el registrado en el sistema.

Ejemplo:

```text
Existencia actual: 24

Producto dañado:
Ajuste SALIDA: 3

Nueva existencia: 21

Movimiento generado:
AJUSTE_SALIDA -3
```

Cada ajuste:

- Se realiza sobre una existencia específica.
- Registra el usuario responsable.
- Registra fecha y motivo.
- Puede incrementar o disminuir una existencia.
- Nunca permite dejar una existencia negativa.
- Genera automáticamente un movimiento de inventario.

Las operaciones se ejecutan mediante `@Transactional` para mantener la consistencia de los datos.

---

## ⏳ Control de vencimientos

Los productos pueden configurarse para requerir control de vencimiento.

Cuando un producto tiene esta configuración activa:

- La fecha de vencimiento es obligatoria durante una entrada.
- Diferentes fechas generan existencias independientes.
- Es posible consultar productos próximos a vencer.
- La estructura está preparada para utilizar estrategias como **FEFO (First Expired, First Out)** durante futuras ventas.

Los productos que no requieren vencimiento continúan funcionando mediante existencias normales.

---

## 🧾 IVA

DStore utiliza un catálogo configurable de tarifas de IVA.

Actualmente se pueden manejar conceptos como:

- Gravado.
- Exento.
- Excluido.

Las tarifas tienen:

- Nombre.
- Tipo.
- Porcentaje.
- Estado activo/inactivo.

Cada producto se relaciona con una tarifa de IVA.

Esto evita almacenar manualmente el porcentaje en cada producto y permite administrar las tarifas centralizadamente.

---

## 🧩 Arquitectura

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

Gestiona las peticiones y respuestas HTTP.

**Service**

Contiene reglas de negocio y operaciones transaccionales.

**Repository**

Gestiona el acceso a datos mediante Spring Data JPA.

**DTO**

Controla la información que entra y sale de la API.

---

## ✅ Pruebas

El proyecto utiliza:

- JUnit 5
- Mockito

Se realizan pruebas unitarias principalmente sobre servicios y reglas de negocio.

Entre los escenarios cubiertos se encuentran:

- Creación de productos.
- Validación de código de barras.
- Relaciones activas/inactivas.
- Entradas de inventario.
- Productos inactivos.
- Ajustes de inventario.
- Stock insuficiente.
- Generación de movimientos.
- Validaciones de reglas de negocio.
- Autenticación y JWT.

También se realizan pruebas funcionales de los endpoints mediante Postman.

---

## ⚙️ Configuración

La aplicación utiliza variables de entorno para evitar almacenar credenciales dentro del repositorio.

Variables requeridas:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

Ejemplo de configuración:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

La zona horaria utilizada por la aplicación es:

```text
America/Bogota
```

---

## ▶️ Ejecución del Backend

Clonar el repositorio:

```bash
git clone https://github.com/blackbird17922se/rdstore.git
cd rdstore
```

Configurar las variables de entorno:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

Luego ejecutar:

```bash
mvn clean install
mvn spring-boot:run
```

---

## ▶️ Ejecución del Frontend

Clonar el repositorio:

```bash
git clone https://github.com/blackbird17922se/dstore-front.git
```

Instalar dependencias:

```bash
npm install
```

Ejecutar:

```bash
ng serve
```

> El frontend se encuentra en proceso de actualización para consumir la versión actual de la API.

---

## 🚧 Estado actual

### Implementado

- ✅ Seguridad con JWT.
- ✅ Usuarios y roles.
- ✅ Perfil de usuario.
- ✅ Categorías.
- ✅ Marcas.
- ✅ Presentaciones.
- ✅ Tarifas de IVA.
- ✅ Productos.
- ✅ Existencias.
- ✅ Entradas de inventario.
- ✅ Control de vencimientos.
- ✅ Movimientos de inventario.
- ✅ Ajustes de inventario.
- ✅ Pruebas unitarias con JUnit y Mockito.

### En desarrollo

- 🚧 Gestión de clientes.
- 🚧 Integración del frontend Angular con la versión actual del backend.

### Próximos módulos

- 🔜 Ventas.
- 🔜 Detalle de ventas.
- 🔜 Consumo automático de existencias.
- 🔜 Estrategias FEFO / FIFO.
- 🔜 Pagos.
- 🔜 Ventas a crédito / fiado.
- 🔜 Abonos y cuentas por cobrar.
- 🔜 Descuadres de inventario.
- 🔜 Anulación de ventas.
- 🔜 Dashboard y alertas de vencimiento.

---

## 💡 Evoluciones contempladas

DStore está siendo diseñado para permitir futuras funcionalidades como:

### Venta sin stock

El sistema podrá configurarse para permitir una venta aunque la existencia disponible sea insuficiente.

En ese caso:

```text
Venta solicitada: 5
Stock disponible: 2

Se consumen: 2
Faltante: 3

→ Se genera un descuadre de inventario pendiente
```

Las existencias nunca serán negativas.

### Ventas fiadas

Las ventas normales podrán realizarse sin identificar cliente.

Para ventas a crédito:

```text
Cliente → obligatorio
```

El sistema podrá administrar:

- Deudas por cliente.
- Saldo pendiente.
- Abonos.
- Historial de pagos.
- Cuentas por cobrar.

---

## 🎯 Objetivo del proyecto

DStore busca representar una aplicación empresarial pequeña pero realista, aplicando conceptos como:

- Diseño de APIs REST.
- Seguridad con JWT.
- Arquitectura en capas.
- Validaciones.
- Manejo de excepciones.
- Transacciones.
- Modelado relacional.
- Gestión de inventario.
- Trazabilidad.
- Pruebas unitarias.
- Integración Angular + Spring Boot.

El proyecto evoluciona incrementalmente buscando mantener código comprensible, buenas prácticas y reglas de negocio cercanas a escenarios reales.

---

## 👨‍💻 Autor

**Mauricio Alarcón**

Proyecto personal orientado al fortalecimiento de habilidades en desarrollo Full Stack con Java, Spring Boot y Angular.
