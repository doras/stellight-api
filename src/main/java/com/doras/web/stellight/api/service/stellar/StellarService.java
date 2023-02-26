package com.doras.web.stellight.api.service.stellar;

import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import com.doras.web.stellight.api.exception.StellarNotFoundException;
import com.doras.web.stellight.api.web.dto.StellarResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Find all stellars.
     * @return List of found entities with {@link StellarResponseDto} classes.
     */
    @Transactional(readOnly = true)
    public List<StellarResponseDto> findAll() {
        return stellarRepository.findAllByOrderById().stream().map(StellarResponseDto::new).collect(Collectors.toList());
    }
}
