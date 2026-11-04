package proyectoG5.LasBromelias.Domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cabana")
public class Cabaña implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "num_habitaciones")
    private Integer numHabitaciones;

    @Column(name = "descripcion_camas", length = 255)
    private String descripcionCamas;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "precio_crc", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCrc;

    @Column(name = "precio_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUsd;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}

