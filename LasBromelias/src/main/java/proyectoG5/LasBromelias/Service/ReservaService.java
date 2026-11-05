package proyectoG5.LasBromelias.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyectoG5.LasBromelias.Domain.Cabaña;
import proyectoG5.LasBromelias.Domain.Reserva;
import proyectoG5.LasBromelias.Domain.ReservaServicio;
import proyectoG5.LasBromelias.Domain.ServicioExtra;
import proyectoG5.LasBromelias.Domain.Usuario;
import proyectoG5.LasBromelias.Repository.CabanaRepository;
import proyectoG5.LasBromelias.Repository.ReservaRepository;
import proyectoG5.LasBromelias.Repository.ReservaServicioRepository;
import proyectoG5.LasBromelias.Repository.ServicioExtraRepository;
import proyectoG5.LasBromelias.Repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaServicioRepository reservaServicioRepository;
    private final ServicioExtraRepository servicioExtraRepository;
    private final CabanaRepository cabanaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BitacoraReservaService bitacoraReservaService;

    //Obtener Reservas
    @Transactional(readOnly = true)
    public List<Reserva> getReservas() {
        return reservaRepository.findAll();
    }

    //Obtener las reservas del usuario que inició sesión
    @Transactional(readOnly = true)
    public List<Reserva> getReservasPorUsuario(String correo) {
        return reservaRepository.findByUsuario_CorreoOrderByFechaCreacionDesc(correo);
    }

    //Obtener solo una reserva
    @Transactional(readOnly = true)
    public Optional<Reserva> getReserva(Long idReserva) {
        return reservaRepository.findById(idReserva);
    }

    //Obtener reservas segun su estado usando parametros
    @Transactional(readOnly = true)
    public List<Reserva> getReservasPorEstado(String estado) {
        return reservaRepository.findByEstadoOrderByFechaEntradaAsc(estado);
    }

    //Validar si la cabaña esta disponible en la fecha solicitada
    @Transactional(readOnly = true)
    public boolean estaDisponible(Long idCabana, java.time.LocalDate entrada, java.time.LocalDate salida) {

        if (entrada == null || salida == null || !salida.isAfter(entrada)) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada.");
        }

        //Asignamos una lista que contenga los estados que quiten una cabaña de estar disponible
        List<String> bloqueoEstados = Arrays.asList("PENDIENTE_PAGO", "PAGO_POR_VERIFICAR", "CONFIRMADA", "EN_CURSO");

        boolean noExisteSuperposicion = reservaRepository.existsByCabana_IdAndEstadoInAndFechaEntradaLessThanAndFechaSalidaGreaterThan(idCabana, bloqueoEstados, salida, entrada);

        //Realizamos una consulta para saber si la cabaña esta disponible en el rango de fechas solicitado,
        return !noExisteSuperposicion;
    }

    //Metodo de crear Reserva
    @Transactional
    public Reserva crearReserva(Reserva reserva) {
        //Validaciones de datos
        if (reserva == null || reserva.getUsuario() == null || reserva.getCabana() == null) {
            throw new IllegalArgumentException("Debe indicar la reserva, el cliente y la cabaña.");
        }
        if (reserva.getUsuario().getId() == null || reserva.getCabana().getId() == null) {
            throw new IllegalArgumentException("El cliente y la cabaña deben existir previamente.");
        }
        if (reserva.getFechaEntrada() == null || reserva.getFechaSalida() == null
                || !reserva.getFechaSalida().isAfter(reserva.getFechaEntrada())) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada.");
        }
        if (reserva.getCantidadHuespedes() == null || reserva.getCantidadHuespedes() <= 0) {
            throw new IllegalArgumentException("La cantidad de huéspedes debe ser mayor a cero.");
        }

        //Validar si el cliente existe o si esta activo
        //Se usa isPresent() al trabajar con este tipo de consultas ya que no lanza null
        Optional<Usuario> resultadoUsuario = usuarioRepository.findById(reserva.getUsuario().getId());
        if (!resultadoUsuario.isPresent()) {
            throw new IllegalArgumentException("El cliente no existe.");
        }
        Usuario usuario = resultadoUsuario.get();
        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("El cliente está inactivo.");
        }

        //Validar que la cabaña exista y que cumpla validaciones(Utilizando el mismo tipo de estructura anterior)
        Optional<Cabaña> resultadoCabana = cabanaRepository.findById(reserva.getCabana().getId());
        if (!resultadoCabana.isPresent()) {
            throw new IllegalArgumentException("La cabaña no existe.");
        }

        Cabaña cabana = resultadoCabana.get(); //Usamos el get para obtener el objeto del optional

        if (!"ACTIVA".equalsIgnoreCase(cabana.getEstado())) {
            throw new IllegalArgumentException("La cabaña está inactiva.");
        }
        if (reserva.getCantidadHuespedes() > cabana.getCapacidad()) {
            throw new IllegalArgumentException("La cantidad de huéspedes supera la capacidad de la cabaña.");
        }

        //Usamos el metodo de esta disponible para validad disponibilidad
        if (!estaDisponible(cabana.getId(), reserva.getFechaEntrada(), reserva.getFechaSalida())) {
            throw new IllegalStateException("La cabaña ya está reservada para esas fechas.");
        }

        //Validar la moneda, el sistema admite tanto USD como CRC
        String moneda = reserva.getMoneda();

        BigDecimal precioNoche;
        if ("CRC".equals(moneda)) {
            precioNoche = cabana.getPrecioCrc();
        } else if ("USD".equals(moneda)) {
            precioNoche = cabana.getPrecioUsd();
        } else {
            throw new IllegalArgumentException("La moneda debe ser CRC o USD.");
        }

        //usamos ChronoUnit.DAYS.between para contar la cantidad de noches entre las fechas elegidas y asi
        //Hacer un calculo del precio
        long noches = ChronoUnit.DAYS.between(reserva.getFechaEntrada(), reserva.getFechaSalida());
        BigDecimal total = precioNoche.multiply(BigDecimal.valueOf(noches));

        //Ahora si, luego de las validaciones, establecemos los atributos de la reserva que se creara
        reserva.setUsuario(usuario);
        reserva.setCabana(cabana);
        reserva.setCodigo("RES-#" + System.currentTimeMillis()); //Generacion de un codigo unico
        reserva.setPrecioNocheAplicado(precioNoche);
        reserva.setTotal(total);
        reserva.setEstado("PENDIENTE_PAGO");
        reserva.setOrigen("WEB");
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setFechaLimitePago(LocalDateTime.now().plusHours(48)); //Establecimos 48 horas como fecha de limite de pago

        Reserva reservaGuardada = reservaRepository.save(reserva);
        bitacoraReservaService.registrarCambioEstado(reservaGuardada, null, "PENDIENTE_PAGO", null);
        return reservaGuardada;
    }

    //Metodo para modificar estado de la cabaña durante Check-in/Check-out
    @Transactional
    public Reserva cambiarEstado(Long idReserva, String estadoNuevo, Usuario usuario) {
        Optional<Reserva> resultado = reservaRepository.findById(idReserva);
        if (!resultado.isPresent()) {
            throw new IllegalArgumentException("La reserva no existe.");
        }
        if (estadoNuevo == null || estadoNuevo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el estado nuevo.");
        }

        Reserva reserva = resultado.get();
        String estadoAnterior = reserva.getEstado();
        String nuevo = estadoNuevo.trim().toUpperCase();

        //Validamos no hacer cambios de estado que no tengan sentido con el metodo transicionPermitida()
        if (!transicionPermitida(estadoAnterior, nuevo)) {
            throw new IllegalStateException("No se permite cambiar de " + estadoAnterior + " a " + nuevo + ".");
        }

        reserva.setEstado(nuevo);
        Reserva reservaGuardada = reservaRepository.save(reserva);
        bitacoraReservaService.registrarCambioEstado(reservaGuardada, estadoAnterior, nuevo, usuario);
        return reservaGuardada;
    }

    //Metodo para agregar servicios extra y actualizar el total de la reserva
    @Transactional
    public Reserva agregarServiciosExtra(Long idReserva, List<ReservaServicio> serviciosSeleccionados) {
        if (idReserva == null) {
            throw new IllegalArgumentException("Debe indicar el ID de la reserva.");
        }
        Optional<Reserva> resultadoReserva = reservaRepository.findById(idReserva);
        if (!resultadoReserva.isPresent()) {
            throw new IllegalArgumentException("La reserva no existe.");
        }
        Reserva reserva = resultadoReserva.get();

        if (serviciosSeleccionados == null || serviciosSeleccionados.isEmpty()) {
            return reserva;
        }

        if (!"PENDIENTE_PAGO".equals(reserva.getEstado())) {
            throw new IllegalStateException("Solo se pueden agregar servicios a una reserva pendiente de pago.");
        }
        if (reserva.getTotal() == null) {
            throw new IllegalStateException("La reserva no tiene un total calculado.");
        }

        //Calculamos el nuevo total con los servicios extra
        BigDecimal nuevoTotal = reserva.getTotal();
        List<ReservaServicio> detalles = new ArrayList<>();
        List<Long> idsServicios = new ArrayList<>();

        for (ReservaServicio seleccionado : serviciosSeleccionados) {
            if (seleccionado == null || seleccionado.getServicio() == null
                    || seleccionado.getServicio().getId() == null
                    || seleccionado.getCantidad() == null || seleccionado.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cada servicio debe tener un ID y una cantidad mayor a cero.");
            }

            //Verificar si ya existe
            Long idServicio = seleccionado.getServicio().getId();
            if (idsServicios.contains(idServicio) || reservaServicioRepository.existsByReserva_IdAndServicio_Id(idReserva, idServicio)) {
                throw new IllegalArgumentException("El servicio ya fue agregado a esta reserva.");
            }
            idsServicios.add(idServicio);

            //Verificar que los servicos extra existan y esten activos
            Optional<ServicioExtra> resultadoServicio = servicioExtraRepository.findById(idServicio);
            if (!resultadoServicio.isPresent()) {
                throw new IllegalArgumentException("El servicio seleccionado no existe.");
            }
            ServicioExtra servicio = resultadoServicio.get();
            if (!servicio.getActivo()) {
                throw new IllegalArgumentException("El servicio " + servicio.getNombre() + " está inactivo.");
            }

            //Validar monedas
            BigDecimal precioUnitario;
            if ("CRC".equals(reserva.getMoneda())) {
                precioUnitario = servicio.getPrecioCrc();
            } else if ("USD".equals(reserva.getMoneda())) {
                precioUnitario = servicio.getPrecioUsd();
            } else {
                throw new IllegalArgumentException("La moneda de la reserva debe ser CRC o USD.");
            }

            //Establecemos el detalle (la tabla ReservaServicio funciona como el detalle, relaciona servicioExtra con reserva)
            ReservaServicio detalle = new ReservaServicio();
            detalle.setReserva(reserva);
            detalle.setServicio(servicio);
            detalle.setCantidad(seleccionado.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalles.add(detalle);

            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(seleccionado.getCantidad()));
            nuevoTotal = nuevoTotal.add(subtotal);
        }

        reservaServicioRepository.saveAll(detalles);
        reserva.setTotal(nuevoTotal);
        return reservaRepository.save(reserva);
    }

    //Metodo para validar cambios de estado
    private boolean transicionPermitida(String anterior, String nuevo) {
        if (anterior == null || anterior.equals(nuevo)) {
            return false;
        }

        if (anterior.equals("PENDIENTE_PAGO")) {
            return nuevo.equals("PAGO_POR_VERIFICAR") || nuevo.equals("CANCELADA");
        }
        if (anterior.equals("PAGO_POR_VERIFICAR")) {
            return nuevo.equals("CONFIRMADA") || nuevo.equals("PENDIENTE_PAGO")
                    || nuevo.equals("CANCELADA");
        }
        if (anterior.equals("CONFIRMADA")) {
            return nuevo.equals("EN_CURSO") || nuevo.equals("CANCELADA");
        }
        if (anterior.equals("EN_CURSO")) {
            return nuevo.equals("FINALIZADA");
        }
        return false;
    }
}


