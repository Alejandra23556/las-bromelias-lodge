package proyectoG5.LasBromelias.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import proyectoG5.LasBromelias.Domain.Cabaña;
import proyectoG5.LasBromelias.Repository.CabanaRepository;

@Service
@RequiredArgsConstructor
public class CabañaService {

    private final CabanaRepository cabanaRepository;

    @Transactional(readOnly = true)
    public List<Cabaña> getCabanasDisponibles() {
        return cabanaRepository.findByEstadoIgnoreCase("ACTIVA");
    }

    @Transactional
    public Cabaña save(Cabaña cabana, MultipartFile imagenFile) {
        if (cabana == null) {
            throw new IllegalArgumentException("Debe indicar los datos de la cabaña.");
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                cabana.setFotoUrl(guardarImagen(imagenFile));
            } catch (IOException e) {
                throw new IllegalStateException("No fue posible guardar la imagen de la cabaña.", e);
            }
        }

        return cabanaRepository.save(cabana);
    }

    private String guardarImagen(MultipartFile imagenFile) throws IOException {
        String nombreOriginal = imagenFile.getOriginalFilename();
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            throw new IllegalArgumentException("La imagen debe tener una extensión válida.");
        }

        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.') + 1).toLowerCase();
        List<String> extensionesPermitidas = Arrays.asList("jpg", "jpeg", "png", "webp");
        if (!extensionesPermitidas.contains(extension)) {
            throw new IllegalArgumentException("La imagen debe ser JPG, JPEG, PNG o WEBP.");
        }

        Path carpetaImagenes = Path.of("uploads", "cabanas").toAbsolutePath().normalize();
        Files.createDirectories(carpetaImagenes);

        String nombreArchivo = UUID.randomUUID() + "." + extension;
        Path rutaImagen = carpetaImagenes.resolve(nombreArchivo);
        imagenFile.transferTo(rutaImagen);

        return "/img/cabanas/" + nombreArchivo;
    }
}
