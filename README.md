# Plataforma Web de Reservas y Gestión Hotelera: Las Bromelias Lodge

Aplicación web transaccional para la gestión de reservas en línea desarrollada con Java (Spring Boot), Thymeleaf, Bootstrap y MySQL.

---

## Integrantes del Equipo (Grupo 5)
* María José Segura Díaz
* María Alejandra Granada Vega


**Profesor:** Andrés Aiello Rodríguez  
**Curso:** Desarrollo de Aplicaciones Web y Patrones | Universidad Fidélitas

---

## Prototipo y Documentación
* **Prototipo en Figma:** [Ver Prototipo Navegable](https://www.figma.com/design/H83VJLCOkHO54u5D0KVTvD/Prototipo-Las-Bromelias?node-id=0-1)
* **Documentación del Avance 1:** Disponible en la carpeta `/docs`.

---

## Acuerdo de Trabajo por Ramas (Git Workflow)

Para asegurar un desarrollo ordenado y colaborativo, el equipo aplicará la siguiente estrategia de trabajo:

1. **Estructura de Ramas Principales:**
   * `main`: Código estable y probado para entregas oficiales.
   * `develop`: Rama de integración donde se unen las funcionalidades en desarrollo.

2. **Ramas de Trabajo (Feature Branches):**
   * Cada estudiante creará una rama partiendo de `develop` para trabajar en sus historias de usuario, usando la nomenclatura: `feature/HUXX-descripcion-corta` (Ejemplo: `feature/HU05-registro-usuario`).

3. **Reglas para Integrar Código (Pull Requests):**
   * Queda prohibido hacer `push` directo a `main` o `develop`.
   * Para integrar código a `develop`, se debe abrir un Pull Request (PR).
   * Cada PR debe ser revisado y aprobado por al menos un compañero de equipo antes de fusionarlo (merge).

## Ejecución del proyecto

### Requisitos

- Java 21
- MySQL Server en ejecución en el puerto `3306`
- IntelliJ IDEA u otro IDE compatible con proyectos Maven

### Preparar la base de datos

1. Abre MySQL Workbench e inicia sesión con un usuario administrador.
2. Abre el archivo `src/main/resources/las_bromelias_lodge.sql`.
3. Ejecuta el script completo. Este crea la base de datos, las tablas y los datos de prueba, además del usuario MySQL que utiliza la aplicación.
4. Verifica que las credenciales de conexión en `src/main/resources/application.properties` coincidan con las definidas en el script.

### Iniciar la aplicación

1. Abre el proyecto en IntelliJ IDEA y espera a que Maven descargue las dependencias.
2. Desde la carpeta principal del proyecto, ejecuta la clase principal de Spring Boot, la que tiene la anotación `@SpringBootApplication`.
3. Cuando la aplicación haya iniciado, abre en el navegador:

   http://localhost:8081

### Usuario de prueba

- Correo: `cliente@example.com`
- Contraseña: `ClienteG5!2026`

La aplicación guarda las imágenes cargadas en la carpeta `uploads` dentro de la carpeta principal del proyecto.

