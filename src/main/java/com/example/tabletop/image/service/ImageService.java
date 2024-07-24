package com.example.tabletop.image.service;

<<<<<<< Updated upstream
public class ImageService {

}
=======
import com.example.tabletop.image.entity.Image;
import com.example.tabletop.image.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final String uploadDir;

    @Autowired
    public ImageService(ImageRepository imageRepository, @Value("${app.upload.dir:uploads}") String uploadDir) {
        this.imageRepository = imageRepository;
        this.uploadDir = uploadDir;
    }

    @Transactional
    public Image saveImage(MultipartFile file, Long parentId, Image.ParentType parentType) throws IOException {
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(uploadDir, filename);

        Files.createDirectories(filepath.getParent());
        Files.write(filepath, file.getBytes());

        Image image = new Image(
            parentId,
            parentType,
            filename,
            file.getOriginalFilename(),
            filepath.toString()
        );

        return imageRepository.save(image);
    }

    @Transactional(readOnly = true)
    public Image getImage(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
    }

    @Transactional(readOnly = true)
    public List<Image> getImagesByParent(Long parentId, Image.ParentType parentType) {
        return imageRepository.findByParentIdAndParentType(parentId, parentType);
    }

    @Transactional
    public void deleteImage(Long imageId) throws IOException {
        Image image = getImage(imageId);
        Path filepath = Paths.get(image.getFilepath());
        Files.deleteIfExists(filepath);
        imageRepository.delete(image);
    }

    @Transactional(readOnly = true)
    public byte[] getImageBytes(Long imageId) throws IOException {
        Image image = getImage(imageId);
        Path filepath = Paths.get(image.getFilepath());
        return Files.readAllBytes(filepath);
    }
}
>>>>>>> Stashed changes
