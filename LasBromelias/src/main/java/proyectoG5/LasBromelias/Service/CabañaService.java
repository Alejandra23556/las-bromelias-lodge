package proyectoG5.LasBromelias.Service;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import proyectoG5.LasBromelias.Domain.Cabaña;
import proyectoG5.LasBromelias.Repository.CabanaRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CabañaService {

    private final CabanaRepository cabanaRepository;
    private final StorageService storageService;


    //Obtener Cabañas Disponibles
    @Transactional(readOnly = true)
    public List<Cabaña> getCabanasDisponibles() {
        return cabanaRepository.findByEstadoIgnoreCase("ACTIVA");
    }

    //Guardar una nueva cabaña con imagenes .jpg
    @Transactional
    public Cabaña save(Cabaña cabana, MultipartFile imagenFile) {
        if (cabana == null) {
            throw new IllegalArgumentException("Debe indicar los datos de la cabaña.");
        }

        cabana = cabanaRepository.save(cabana);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String rutaImagen = storageService.uploadImage(
                        imagenFile,
                        "cabana",
                        cabana.getId()
                );

                cabana.setFotoUrl(rutaImagen);
                cabana = cabanaRepository.save(cabana);
            } catch (IOException e) {
                throw new IllegalStateException("No fue posible guardar la imagen.", e);
            }
        }
        return cabana;
    }

    //Eliminar cabaña
    @Transactional
    public void delete(Long idCabana) {
        if (!cabanaRepository.existsById(idCabana)) {
            throw new IllegalArgumentException(
                    "La cabaña con ID " + idCabana + " no existe."
            );
        }

        try {
            cabanaRepository.deleteById(idCabana);
            cabanaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar la cabaña porque tiene datos asociados.",
                    e
            );
        }
    }
}
