package com.sossbar.global.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 크기 제한 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // 파일 업로드
    public String uploadFile(MultipartFile file, String dirName) {
        validateFile(file);

        String fileName = dirName + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            // url로 보여주는 방식, url 클릭시 다운로드 x
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(
                    bucket,
                    fileName,
                    file.getInputStream(),
                    metadata
            );
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAIL_EXCEPTION
                    , ErrorCode.FILE_UPLOAD_FAIL_EXCEPTION.getMessage());
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    // 파일 삭제
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        String key = extractFileName(fileUrl);
        amazonS3.deleteObject(bucket, key);
    }

    // 파일 유효성 검증
    private void validateFile(MultipartFile file) {
        // 빈 파일 검증
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND_EXCEPTION
                    , ErrorCode.FILE_NOT_FOUND_EXCEPTION.getMessage());
        }

        // 사이즈 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(
                    ErrorCode.FILE_SIZE_EXCEEDED_EXCEPTION
                    , ErrorCode.FILE_SIZE_EXCEEDED_EXCEPTION.getMessage()
            );
        }

        String originalFilename = file.getOriginalFilename();

        // 파일 형식 검증
        if (originalFilename == null ||
                !originalFilename.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
            throw new BusinessException(
                    ErrorCode.INVALID_FILE_TYPE_EXCEPTION
                    , ErrorCode.INVALID_FILE_TYPE_EXCEPTION.getMessage()
            );
        }
    }

    // 파일 이름 추출
    public String extractFileName(String fileUrl) {
        String key = fileUrl.replace(
                "https://sossbar-bucket.s3.ap-northeast-2.amazonaws.com/",
                ""
        );
        return URLDecoder.decode(key, StandardCharsets.UTF_8);
    }
}
