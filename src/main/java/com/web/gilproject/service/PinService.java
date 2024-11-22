package com.web.gilproject.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.Pin;
import com.web.gilproject.dto.PinResDTO;
import com.web.gilproject.exception.PathErrorCode;
import com.web.gilproject.exception.PathPinException;
import com.web.gilproject.repository.PinRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PinService {
    private final PinRepository pinRepository;
    private final AmazonS3 amazonS3;
    private final AmazonService amazonService;



    public Pin insert(Pin pin) {
        Pin savedPin= pinRepository.save(pin);
        return savedPin;
    }

    /*public void update(Long pinId, PinResDTO pinResDTO) {
        // Pin 엔티티를 조회
        Optional<Pin> optionalPin = pinRepository.findById(pinId);

        if (optionalPin.isPresent()) {
            Pin pin = optionalPin.get();

            // Pin 엔티티의 필드 업데이트
            pin.setContent(pinResDTO.getContent());
            pin.setLatitude(pinResDTO.getLatitude());
            pin.setLongitude(pinResDTO.getLongitude());

            if (pinResDTO.getImageUrl() != null && !pinResDTO.getImageUrl().isEmpty()) {
                pin.setImageUrl(pinResDTO.getImageUrl());
            }

            // 변경된 Pin 엔티티를 저장
            pinRepository.save(pin);
        } else {
            // Pin ID에 해당하는 엔티티가 없을 경우 예외 처리
            throw new EntityNotFoundException("Pin with ID " + pinId + " not found");
        }
    }*/
    //pin삭제하기
    public void delete(Long pinId) {
        Optional<Pin> optionalPin = pinRepository.findById(pinId);
        if (optionalPin.isPresent()) {
            Pin pin = optionalPin.get();

            String url = pin.getImageUrl();

            deleteFileByUrl("aws-gil-project",url);

            pinRepository.delete(pin);
        }else {
            // Pin ID에 해당하는 엔티티가 없을 경우 예외 처리
            throw new PathPinException(PathErrorCode.DELETE_FAILED);
        }
    }

    //
    public void deleteFileByUrl(String bucketName, String s3Url) {
        // Key 추출
        String key = extractKeyFromUrl(s3Url);

        System.out.println("Extracted Key: " + key + "/" + bucketName);

        // S3에서 파일 삭제
        deleteFile(bucketName, key);
    }

    public void deleteFile(String bucketName,String fileKey) {
        try {
            // 삭제 요청 생성
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, fileKey);

            // 파일 삭제
            amazonS3.deleteObject(deleteObjectRequest);

            System.out.println("File deleted successfully: " + fileKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PathPinException(PathErrorCode.DELETE_FAILED);
        }
    }

    //s3에서 이미지 삭제하기위해 key구하기
    public String extractKeyFromUrl(String s3Url) {
        try {
            URL url = new URL(s3Url);

            String path = url.getPath();

            return path.startsWith("/") ? path.substring(1) : path;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PathPinException(PathErrorCode.DELETE_FAILED);
        }
    }
}
