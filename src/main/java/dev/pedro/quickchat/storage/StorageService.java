package dev.pedro.quickchat.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;

@Service
public class StorageService {

    private MinioClient minioClient;

    private String minioEndpoint;

    private String publicImageBucket = "image-bucket";

    public StorageService(MinioClient minioClient, @Value("${MINIO_URL:http://localhost:9000}") String minioEndpoint) {
        this.minioClient = minioClient;
        this.minioEndpoint = minioEndpoint;
    }

    @PostConstruct
    public void initBucker() {
        try {
            boolean result = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(publicImageBucket).build()
            );

            if(!result) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(publicImageBucket).build()
                );
                makeBucketPublic(publicImageBucket);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void makeBucketPublic(String bucketName) {
        try {
            String policyJson = """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": "*",
                            "Action": ["s3:GetObject"],
                            "Resource": ["arn:aws:s3:::%s/*"]
                        }
                    ]
                }
                """.formatted(bucketName);

            minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policyJson)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error configuring MinIO public policy", e);
        }
    }

    public String uploadFile(String bucketName, MultipartFile file) {
        try {
            boolean result = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if(!result) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
                );
            }

            String filename = file.getOriginalFilename();

            String extension = "";
            if(filename != null && filename.contains(".")) {
                extension = filename.substring(filename.lastIndexOf("."));
            }
            
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String objectName = UUID.randomUUID() + "_" + date.format(formatter) + extension;

            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1L)
                    .contentType(file.getContentType())
                    .build()
            );

            return String.format("%s/%s/%s", minioEndpoint, bucketName, objectName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
