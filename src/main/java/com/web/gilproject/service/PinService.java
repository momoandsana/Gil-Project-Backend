package com.web.gilproject.service;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.Pin;
import com.web.gilproject.dto.PinResDTO;
import com.web.gilproject.repository.PinRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PinService {
    private final PinRepository pinRepository;

    public Pin insert(Pin pin) {
        Pin savedPin= pinRepository.save(pin);
        return savedPin;
    }

    public void update(Long pinId, PinResDTO pinResDTO) {
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
    }

    public void delete(Long pinId) {
        Optional<Pin> optionalPin = pinRepository.findById(pinId);
        if (optionalPin.isPresent()) {
            Pin pin = optionalPin.get();
            pinRepository.delete(pin);
        }else {
            // Pin ID에 해당하는 엔티티가 없을 경우 예외 처리
            throw new EntityNotFoundException("Pin with ID " + pinId + " not found");
        }
    }
}
