package dev.pedro.quickchat.storage;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.pedro.quickchat.storage.dto.UploadImageResponse;
import dev.pedro.quickchat.storage.exception.InvalidFileTypeException;
import dev.pedro.quickchat.storage.exception.UploadFileException;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("storage")
public class StorageController {

    private final StorageService storageService;

    private static final List<String> ALLOWED_TYPES = List.of(
        "image/jpeg", 
        "image/png", 
        "image/webp"
    );

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadImageResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String contentType = file.getContentType();

            boolean isValid = contentType != null && ALLOWED_TYPES.stream().anyMatch(contentType::startsWith);

            if(!isValid) {
                throw new InvalidFileTypeException("image/*", file.getContentType());
            }

            String fileUrl = storageService.uploadFile("image-bucket", file);

            return ResponseEntity.ok(new UploadImageResponse(fileUrl));
        } catch (Exception e) {
           throw new UploadFileException(e.getMessage());
        }
    }
    
}
