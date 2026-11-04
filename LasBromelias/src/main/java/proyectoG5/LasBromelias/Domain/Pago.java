package proyectoG5.LasBromelias.Domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pago")
public class Pago implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(name = "metodo", nullable = false, length = 20)
    private String metodo;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;

    @Column(name = "numero_comprobante", length = 50)
    private String numeroComprobante;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;

    @ManyToOne
    @JoinColumn(name = "registrado_por")
    private Usuario registradoPor;
}

