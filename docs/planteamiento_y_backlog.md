# Documento de Planteamiento del Proyecto y Backlog de Historias de Usuario
**Curso:** Desarrollo de Aplicaciones Web y Patrones  
**Profesor:** Andrés Aiello Rodríguez  
**Proyecto:** Plataforma Web de Reservas y Gestión Hotelera: Las Bromelias Lodge  

---

## 1. Planteamiento del Proyecto

### Nombre de la Solución
Plataforma Web de Reservas y Gestión Hotelera: Las Bromelias Lodge

### Tipo de Aplicación
Aplicación web transaccional de reservas en línea desarrollada en Java con el framework **Spring Boot**, interfaz en **Thymeleaf**, estilos en **Bootstrap** y base de datos relacional **MySQL**.

### Sector
Turismo ecológico y gestión hotelera (PYME).

### Descripción del Problema
Actualmente, Las Bromelias Lodge administra sus reservas mediante métodos totalmente manuales (atención telefónica, mensajes de texto y libretas/Excel). Esta falta de digitalización genera:
* **Riesgo de errores operativos:** Solapamiento de fechas y reservas erróneas.
* **Pérdida de oportunidades:** Retrasos al responder consultas sobre disponibilidad y precios fuera de horario.
* **Ausencia de registro centralizado:** Carencia de una base de datos estructurada para el control histórico de huéspedes y pagos.

### Cliente Real o Potencial
**Las Bromelias Lodge**, una PYME familiar ubicada en el Cerro de la Muerte, Costa Rica, compuesta por un complejo de dos cabañas equipadas en un entorno de montaña.

### Usuarios Meta
1. **Visitante / Cliente Potencial:** Turistas nacionales o extranjeros que buscan consultar la oferta, verificar disponibilidad y realizar/gestionar sus reservas de forma autónoma.
2. **Administrador y Recepción:** Personal interno encargado de gestionar el catálogo de cabañas, verificar pagos (SINPE/Efectivo), gestionar el check-in/check-out y auditar las operaciones.

### Justificación
Digitalizar y automatizar la gestión de reservas eliminará los errores manuales, brindará disponibilidad en tiempo real las 24 horas y otorgará al negocio una plataforma transaccional sólida basada en arquitectura MVC.

---

## 2. Matriz de Historias de Usuario y Backlog Priorizado (20 HU)

| ID | Rol | Historia de Usuario (`Como... necesito... para...`) | Criterios de Aceptación | Prioridad |
| :--- | :--- | :--- | :--- | :--- |
| **HU-01** | Visitante | Como posible huésped y usuario visitante, necesito una lista de las cabañas con fotos, información de la capacidad y precio para conocer la oferta de Las Bromelias Lodge. | • Se muestra foto al seleccionar la cabaña, descripción corta, capacidad máxima y precio por noche.<br>• Solo se muestran cabañas en estado "Activa". | **Alta** |
| **HU-02** | Visitante | Como usuario visitante, necesito poder ver la información del lodge, como el clima y un enlace a la ubicación para entender cómo llegar. | • Incluir vista del lugar y recomendaciones de vestimenta.<br>• Incluir mapa o enlace guiado de Google Maps / Waze. | **Baja** |
| **HU-03** | Visitante | Como usuario visitante, necesito poder cambiar el idioma entre español e inglés para poder visualizar el sitio en mi idioma nativo. | • Selector ES/EN en el encabezado.<br>• Uso de `messages.properties` y Thymeleaf. | **Media** |
| **HU-04** | Visitante | Como usuario visitante, si tengo una consulta general necesito un formulario de contacto. | • Campos: Nombre, Correo, Consulta y Mensaje.<br>• Confirmar y almacenar la consulta en la base de datos. | **Baja** |
| **HU-05** | Visitante | Como usuario visitante, necesito tener la opción de crear una cuenta para poder hacer y gestionar mis reservas. | • Validación de correo y contraseña cifrada con BCrypt.<br>• Asignación automática del rol `ROLE_CLIENT`. | **Alta** |
| **HU-06** | Cliente | Como cliente necesito poder ingresar fechas de llegada y salida, para ver la disponibilidad de cabañas durante ese lapso de días. | • Validar que la fecha de salida sea posterior a la de entrada.<br>• Filtrar cabañas ocupadas en esas fechas. | **Alta** |
| **HU-07** | Cliente | Como cliente necesito poder asegurar mi hospedaje por medio de una reserva en las fechas seleccionadas. | • Cálculo automático del total (Noches x Precio).<br>• Crear la reserva en estado "Pendiente de pago". | **Alta** |
| **HU-08** | Cliente | Como cliente, necesito poder seleccionar servicios adicionales, ya sea leña, desayunos o tours para personalizar mi reserva. | • Checkboxes con servicios activos y sus precios.<br>• Sumar el monto de servicios al total de la reserva. | **Media** |
| **HU-09** | Cliente | Como cliente, necesito añadir el comprobante de transferencia o SINPE, para reportar el pago. | • Espacio para ingresar texto/número de comprobante.<br>• Cambiar estado a "Pago por verificar". | **Alta** |
| **HU-10** | Cliente | Como cliente, necesito poder revisar mi historial de reservas, para ver el estado y los detalles de mis viajes. | • Tabla con código, cabaña, fechas, total y estado.<br>• Mostrar únicamente las reservas del usuario en sesión. | **Media** |
| **HU-11** | Cliente | Como cliente, necesito cancelar una reserva que se encuentre en estado de Pendiente para liberar la cabaña en caso de cambio de opinión o no poder viajar. | • Botón "Cancelar Reserva" accionable solo en estado "Pendiente".<br>• Cambiar estado de la reserva a "Cancelada". | **Media** |
| **HU-12** | Admin | Como administrador, necesito iniciar sesión con mis credenciales, para acceder a las funciones de gestión. | • Formulario con Spring Security.<br>• Acceso a `/admin` si es correcto, mensaje de error si falla. | **Alta** |
| **HU-13** | Admin | Como administrador, necesito registrar, editar y activar o desactivar cabañas para tener la oferta actualizada. | • Formulario CRUD (Nombre, Capacidad, Precio, Estado).<br>• Borrado lógico (desactivar, no eliminar de BD). | **Alta** |
| **HU-14** | Admin | Como administrador, necesito gestionar el catálogo de servicios extra con un CRUD, para así actualizar los precios y detalles. | • Registrar, modificar y eliminar servicios adicionales según disponibilidad. | **Media** |
| **HU-15** | Admin | Como administrador necesito poder verificar que el comprobante ingresado por el cliente sea válido para así aprobar o rechazar la reserva. | • Lista de reservas en estado "Pago por verificar".<br>• Botones para cambiar a "Reserva Confirmada" o "Pago Rechazado". | **Alta** |
| **HU-16** | Recepción | Como recepcionista necesito ver la lista general de reservas con filtros y así tener un control de las llegadas al Lodge. | • Tabla general con buscador por cliente o filtro por estado. | **Media** |
| **HU-17** | Recepción | Como recepcionista, necesito registrar de manera manual las reservas por medio de una llamada o de manera presencial para no perder el control. | • Formulario para elegir cliente, cabaña, fechas y marcar directamente como "Confirmada". | **Media** |
| **HU-18** | Recepción | Como recepcionista, necesito controlar la entrada y salida física de los huéspedes, para saber la disponibilidad real. | • Botón "Check-In" cambia a "Cabaña Ocupada".<br>• Botón "Check-Out" cambia a "Cabaña Disponible". | **Alta** |
| **HU-19** | Admin | Como administrador necesito generar un reporte de ganancias generadas para poder medir el rendimiento comercial. | • Cálculo del monto total recaudado por reservas dentro de un rango de fechas (trimestral/semestral). | **Media** |
| **HU-20** | Admin | Como administrador, necesito de una bitácora con los cambios de estado en las reservas para así poder auditar las operaciones. | • Tabla de solo lectura: ID Reserva, Estado anterior, Estado actual, Usuario, Fecha y Hora. | **Media** |
Markdown
## Mapa de Navegación y Flujo de Pantallas
![Mapa de Navegación](WhatsApp%20Image%202026-10-07%20at%2017.01.49.jpeg)
