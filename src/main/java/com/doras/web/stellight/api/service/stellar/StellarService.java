package com.doras.web.stellight.api.service.stellar;

import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import com.doras.web.stellight.api.exception.StellarNotFoundException;
import com.doras.web.stellight.api.web.dto.StellarsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StellarService {
    private final StellarRepository stellarRepository;

    @Transactional(readOnly = true)
    public StellarsResponseDto findById(Long id) {
        Stellar entity = stellarRepository.findById(id)
                .orElseThrow(() -> new StellarNotFoundException(id));

        return new StellarsResponseDto(entity);
    }
}
