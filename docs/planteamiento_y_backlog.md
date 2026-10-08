# Plataforma Web de Reservas y Gestión Hotelera: Las Bromelias Lodge
**Documentación y Gestión Inicial del Proyecto**

* **Estudiantes:**
  * María José Segura Díaz
  * María Alejandra Granada Vega
  * Emmanuel Calderón Soto
  * Joseph Armas Díaz
* **Curso:** Desarrollo de Aplicaciones Web y Patrones
* **Profesor:** Andrés Aiello Rodríguez
* **Institución:** Universidad Fidélitas - Escuela de Ciencias de la Computación

---

## 1. Datos Generales del Proyecto

* **Nombre de la Solución:** Plataforma Web de Reservas y Gestión Hotelera: Las Bromelias Lodge
* **Tipo de Aplicación:** Aplicación web transaccional para la gestión integral de reservas en línea. Construida en Java con Spring Boot bajo la arquitectura Modelo-Vista-Controlador (MVC). Presentación con Thymeleaf y Bootstrap.
* **Sector:** Turismo ecológico y gestión hotelera (PYME).

---

## 2. Descripción del Problema

El proyecto consiste en el diseño y desarrollo de una aplicación web transaccional e interactiva para la PYME del sector de hotelería **Las Bromelias Lodge**. La plataforma busca centralizar y digitalizar la experiencia de reservas.

Actualmente el negocio se administra mediante métodos manuales (llamadas y mensajería), generando los siguientes problemas:
1. **Riesgo de errores operativos:** Registro erróneo de reservas en libretas o Excel, provocando solapamiento de fechas.
2. **Pérdida de oportunidades:** Espera prolongada de clientes para conocer disponibilidad y precios.
3. **Ausencia de un registro centralizado y transaccional:** Falta de una base de datos estructurada para llevar un control histórico de huéspedes, reservas y cabañas.

---

## 3. Cliente Real o Potencial y Usuarios Meta

### Cliente Real
**Las Bromelias Lodge**, emprendimiento familiar ubicado en la zona montañosa del Cerro de la Muerte, Costa Rica. Complejo de dos cabañas equipadas, chimenea y senderos.

### Usuarios Meta
* **Visitante / Cliente potencial:** Explora la oferta, consulta disponibilidad en tiempo real y realiza la reserva por cuenta propia.
* **Administrador / Recepción:** Gestiona el catálogo de cabañas, aprueba/rechaza comprobantes de pago, registra reservas manuales y consulta la ocupación.

---

## 4. Backlog de Historias de Usuario

| ID | Rol | Historia de Usuario | Criterios de Aceptación | Prioridad |
|---|---|---|---|---|
| **HU-01** | Visitante | Como posible huésped y usuario visitante, necesito una lista de las cabañas con fotos, información de la capacidad y precio para conocer la oferta. | • Muestra foto, descripción corta, capacidad y precio por noche.<br>• Solo muestra cabañas en estado "Activa". | Alta |
| **HU-02** | Visitante | Como usuario visitante, necesito poder ver la información del lodge, como el clima y un enlace a la ubicación. | • Incluir vista del lugar y recomendaciones de vestimenta.<br>• Incluir mapa o enlace guiado de Google Maps o Waze. | Baja |
| **HU-03** | Visitante | Como usuario visitante, necesito poder cambiar el idioma entre español e inglés. | • Selector ES/EN en el encabezado.<br>• Utilizar `messages.properties` y Thymeleaf. | Media |
| **HU-04** | Visitante | Como usuario visitante, si tengo una consulta general necesito un formulario de contacto. | • Campos: Nombre, Correo, Consulta y Mensaje.<br>• Confirmar y guardar la consulta. | Baja |
| **HU-05** | Visitante | Como usuario visitante, necesito la opción de crear una cuenta para gestionar mis reservas. | • Validación de correo y contraseña cifrada con BCrypt.<br>• Asignar rol `ROLE_CLIENT` automáticamente. | Alta |
| **HU-06** | Cliente | Como cliente, necesito ingresar fechas de llegada y salida para ver la disponibilidad. | • Validar que fecha de salida sea posterior a la de entrada.<br>• Filtrar cabañas ocupadas. | Alta |
| **HU-07** | Cliente | Como cliente, necesito asegurar mi hospedaje por medio de una reserva en las fechas seleccionadas. | • Cálculo automático del total (Noches x Precio).<br>• Crear reserva en estado "Pendiente de pago". | Alta |
| **HU-08** | Cliente | Como cliente, necesito seleccionar servicios adicionales (leña, desayunos, tours). | • Checkboxes con servicios activos y precios.<br>• Sumar monto al total de la reserva. | Media |
| **HU-09** | Cliente | Como cliente, necesito añadir el comprobante de transferencia o SINPE para reportar el pago. | • Espacio para ingresar el número de comprobante.<br>• Cambiar estado a "Pago por verificar". | Alta |
| **HU-10** | Cliente | Como cliente, necesito revisar mi historial de reservas. | • Tabla con código, cabaña, fechas, total y estado.<br>• Mostrar solo reservas del usuario en sesión. | Media |
| **HU-11** | Cliente | Como cliente, necesito cancelar una reserva en estado Pendiente. | • Botón "Cancelar Reserva" si el estado es "Pendiente".<br>• Cambiar estado a "Cancelada". | Media |
| **HU-12** | Admin | Como administrador, necesito iniciar sesión con mis credenciales. | • Formulario con Spring Security.<br>• Acceso a `/admin` o mostrar error si falla. | Alta |
| **HU-13** | Admin | Como administrador, necesito registrar, editar y activar/desactivar cabañas. | • Formulario CRUD (Nombre, Capacidad, Precio, Estado).<br>• Inactivar cabañas sin eliminarlas de la BD. | Alta |
| **HU-14** | Admin | Como administrador, necesito gestionar el catálogo de servicios extra. | • Registrar, modificar y eliminar servicios adicionales. | Media |
| **HU-15** | Admin | Como administrador, necesito verificar comprobantes para aprobar o rechazar reservas. | • Lista de "Pagos por verificar".<br>• Botones para cambiar a "Reserva Confirmada" o "Pago Rechazado". | Alta |
| **HU-16** | Recepción | Como recepcionista, necesito ver la lista general de reservas con filtros. | • Tabla general con buscador por cliente y filtro por estado. | Media |
| **HU-17** | Recepción | Como recepcionista, necesito registrar manualmente reservas presenciales o telefónicas. | • Formulario para elegir cliente, cabaña, fechas y marcar como "Confirmada". | Media |
| **HU-18** | Recepción | Como recepcionista, necesito controlar la entrada y salida física de los huéspedes. | • Botón Check-In cambia a "Cabaña Ocupada".<br>• Botón Check-Out cambia a "Cabaña disponible". | Alta |
| **HU-19** | Admin | Como administrador, necesito generar un reporte de ganancias. | • Cálculo de monto total recaudado por rango de fechas (separado por moneda). | Media |
| **HU-20** | Admin | Como administrador, necesito una bitácora con los cambios de estado en las reservas. | • Tabla de solo lectura: ID Reserva, Estado anterior, Estado actual, Usuario, Fecha y Hora. | Media |

---

## 5. Modelo Preliminar de Datos

### Entidades Principales
1. **Usuario:** `id`, `nombre`, `apellidos`, `correo` (único), `contrasena` (BCrypt), `telefono`, `rol` (`ROLE_CLIENT`, `ROLE_RECEPTION`, `ROLE_ADMIN`), `activo`, `fecha_registro`.
2. **Cabana:** `id`, `nombre`, `descripcion`, `num_habitaciones`, `descripcion_camas`, `capacidad`, `precio_crc`, `precio_usd`, `foto_url`, `estado` (`ACTIVA`/`INACTIVA`).
3. **Reserva:** `id`, `codigo` (único), `usuario_id`, `cabana_id`, `fecha_entrada`, `fecha_salida`, `cantidad_huespedes`, `lleva_mascotas`, `moneda`, `precio_noche_aplicado`, `total`, `estado`, `origen`, `fecha_creacion`, `fecha_limite_pago`.
4. **ServicioExtra:** `id`, `nombre`, `descripcion`, `precio_crc`, `precio_usd`, `activo`.
5. **ReservaServicio:** `id`, `reserva_id`, `servicio_id`, `cantidad`, `precio_unitario`.
6. **Pago:** `id`, `reserva_id`, `metodo`, `moneda`, `numero_comprobante`, `monto`, `estado` (`POR_VERIFICAR`, `APROBADO`, `RECHAZADO`), `fecha_reporte`, `fecha_verificacion`, `registrado_por`.
7. **Consulta:** `id`, `nombre`, `correo`, `mensaje`, `fecha`.
8. **BitacoraReserva:** `id`, `reserva_id`, `estado_anterior`, `estado_nuevo`, `usuario_id`, `fecha_hora`.
