package proyectoG5.LasBromelias.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalStorageService implements StorageService {

    private final Path uploadDirectory;

    public LocalStorageService(@Value("${image.storage.local-path:uploads}") String uploadPath) {
        this.uploadDirectory = Path.of(uploadPath).toAbsolutePath().normalize();
    }

    @Override
    public String uploadImage(MultipartFile image, String folder, Long id) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IOException("No se recibió una imagen para guardar.");
        }
        if (id == null) {
            throw new IOException("No se puede guardar la imagen sin el ID de la cabaña.");
        }

        String extension = "";
        String originalName = image.getOriginalFilename();
        if (originalName != null && originalName.lastIndexOf('.') >= 0) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        Path folderPath = uploadDirectory.resolve(folder).normalize();
        if (!folderPath.startsWith(uploadDirectory)) {
            throw new IOException("La carpeta de destino no es válida.");
        }
        Files.createDirectories(folderPath);

        String fileName = "img" + String.format("%014d", id) + extension;
        Path destination = folderPath.resolve(fileName);
        try (InputStream input = image.getInputStream()) {
            Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/uploads/" + folder + "/" + fileName;
    }

}



