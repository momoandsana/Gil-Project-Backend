package com.web.gilproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.S3ObjectSummary;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmazonService {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public AmazonService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();


        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // S3 파일 업로드
        amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);

        // url 반환
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public String uploadFile(MultipartFile file, String uniqueFileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());


        amazonS3.putObject(bucketName, uniqueFileName, file.getInputStream(), metadata);


        return amazonS3.getUrl(bucketName, uniqueFileName).toString();
    }


    public List<String> listFiles() {
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        return objectListing.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .map(key -> amazonS3.getUrl(bucketName, key).toString())
                .collect(Collectors.toList());
    }

    public List<String> listFilesByPrefix(String prefix) {
        ListObjectsRequest request = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix);
        ObjectListing objectListing = amazonS3.listObjects(request);

        return objectListing.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .map(key -> amazonS3.getUrl(bucketName, key).toString())
                .collect(Collectors.toList());
    }

    public String uploadFileToFolder(MultipartFile file, String folder) throws IOException {
        String fileName = file.getOriginalFilename();

        String key = StringUtils.hasText(folder) ? folder + "/" + fileName : fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);

        return amazonS3.getUrl(bucketName, key).toString();
    }
}
