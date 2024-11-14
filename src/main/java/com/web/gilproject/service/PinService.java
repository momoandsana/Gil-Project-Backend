package com.web.gilproject.service;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.Pin;
import com.web.gilproject.repository.PinRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PinService {
    private final PinRepository pinRepository;

    public Pin insert(Pin pin) {
        Pin savedPin= pinRepository.save(pin);
        return savedPin;
    }
}
