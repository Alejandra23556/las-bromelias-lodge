package proyectoG5.LasBromelias.Service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadImage(MultipartFile image, String folder, Long id) throws IOException;
}
