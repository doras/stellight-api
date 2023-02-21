package com.doras.web.stellight.api.service.stellar;

import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import com.doras.web.stellight.api.exception.StellarNotFoundException;
import com.doras.web.stellight.api.web.dto.StellarResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service about {@link Stellar}.
 */
@RequiredArgsConstructor
@Service
public class StellarService {
    private final StellarRepository stellarRepository;

    /**
     * Find Stellar by id.
     * @param id ID for schedule to be found.
     * @return Information of found entity in {@link StellarResponseDto}.
     */
    @Transactional(readOnly = true)
    public StellarResponseDto findById(Long id) {
        Stellar entity = stellarRepository.findById(id)
                .orElseThrow(() -> new StellarNotFoundException(id));

        return new StellarResponseDto(entity);
    }
}
