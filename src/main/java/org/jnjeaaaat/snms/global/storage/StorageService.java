package org.jnjeaaaat.snms.global.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.EMPTY_FILE;
import static org.jnjeaaaat.snms.global.exception.ErrorCode.INTERNAL_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 단일 이미지 업로드
     * @param filePathType 저장할 폴더 위치 MEMBER("member"), POST("post")
     * @param id 저장할 객체 id (memberId, postId, ...)
     * @param file 업로드할 파일
     * @return S3에 저장된 파일의 URL
     */
    public String uploadImage(FilePathType filePathType, Long id, MultipartFile file) {
        String folderPath = filePathType.getPath(id);
        return uploadFile(folderPath, file);
    }

    public List<String> uploadImageList(FilePathType filePathType, Long postId, List<MultipartFile> files) {
        String folderPath = filePathType.getPath(postId);

        // 기존 포스트 이미지 삭제
        deleteAllFilesInFolder(folderPath);

        return uploadMultipleFiles(folderPath, files);
    }

    /**
     * 파일 업로드
     * @param folderPath 저장할 폴더 경로 (예: "member/123", "post/456")
     * @param file 업로드할 파일
     * @return S3에 저장된 파일의 URL
     */
    private String uploadFile(String folderPath, MultipartFile file) {

        String fileName = generateFileName(file.getOriginalFilename());
        String key = folderPath + "/" + fileName;

        try {
            ObjectMetadata metadata = createObjectMetadata(file);

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    file.getInputStream(),
                    metadata
            );

            amazonS3.putObject(putObjectRequest);

            String fileUrl = amazonS3.getUrl(bucketName, key).toString();
            log.info("파일 업로드 성공: {}", fileUrl);

            return fileUrl;

        } catch (IOException e) {
            throw new StorageException(INTERNAL_ERROR, e.getMessage());
        }
    }

    public List<String> uploadMultipleFiles(String folderPath, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new StorageException(EMPTY_FILE);
        }

        if (files.size() > 8) {
            throw new IllegalArgumentException("한 번에 업로드할 수 있는 파일은 최대 10개입니다.");
        }

        List<String> uploadedUrls = new ArrayList<>();
        List<String> uploadedKeys = new ArrayList<>();

        try {
            for (MultipartFile file : files) {

                String fileName = generateFileName(file.getOriginalFilename());
                String key = folderPath + "/" + fileName;

                ObjectMetadata metadata = createObjectMetadata(file);

                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        bucketName,
                        key,
                        file.getInputStream(),
                        metadata
                );

                amazonS3.putObject(putObjectRequest);

                String fileUrl = amazonS3.getUrl(bucketName, key).toString();
                uploadedUrls.add(fileUrl);
                uploadedKeys.add(key);

                log.info("파일 업로드 성공: {}", fileUrl);
            }

            return uploadedUrls;

        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생. 롤백 진행: {}", e.getMessage());
            rollbackUploadedFiles(uploadedKeys);
            throw new StorageException(INTERNAL_ERROR, e.getMessage());
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            amazonS3.deleteObject(bucketName, key);

            log.info("파일 삭제 성공: {}", fileUrl);
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            throw new StorageException(INTERNAL_ERROR, e.getMessage());
        }
    }

    public void deleteAllFilesInFolder(String folderPath) {
        log.info("폴더 내 파일 모두 삭제");
        try {
            ListObjectsV2Request listRequest = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(folderPath + "/");

            ListObjectsV2Result result = amazonS3.listObjectsV2(listRequest);

            if (!result.getObjectSummaries().isEmpty()) {
                List<DeleteObjectsRequest.KeyVersion> keys = result.getObjectSummaries()
                        .stream()
                        .map(s3Object -> new DeleteObjectsRequest.KeyVersion(s3Object.getKey()))
                        .collect(Collectors.toList());

                DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName)
                        .withKeys(keys);

                amazonS3.deleteObjects(deleteRequest);
                log.info("폴더 내 파일 삭제 성공: {}", folderPath);
            }
        } catch (Exception e) {
            log.error("폴더 내 파일 삭제 실패: {}", e.getMessage());
            throw new StorageException(INTERNAL_ERROR, e.getMessage());
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }

    private String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            return url.getPath().substring(1); // "/" 제거
        } catch (MalformedURLException e) {
            throw new StorageException(INTERNAL_ERROR, e.getMessage());
        }
    }

    private void rollbackUploadedFiles(List<String> uploadedKeys) {
        for (String key : uploadedKeys) {
            try {
                amazonS3.deleteObject(bucketName, key);
                log.info("롤백 완료: {}", key);
            } catch (Exception e) {
                log.error("롤백 실패: {}", key, e);
            }
        }
    }

    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        return metadata;
    }

}
