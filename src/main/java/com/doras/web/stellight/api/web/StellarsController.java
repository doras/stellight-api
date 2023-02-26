package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.service.stellar.StellarService;
import com.doras.web.stellight.api.web.dto.StellarResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller about stellars.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stellars")
public class StellarsController {

    private final StellarService stellarService;

    /**
     * Find Stellar by id.
     * @param id ID for schedule to be found.
     * @return Information of found entity in {@link StellarResponseDto}.
     */
    @GetMapping("/{id}")
    public StellarResponseDto findById(@PathVariable Long id) {
        return stellarService.findById(id);
    }

    /**
     * Find all stellars.
     * @return List of found entities with {@link StellarResponseDto} classes.
     */
    @GetMapping
    public List<StellarResponseDto> findAll() {
        return stellarService.findAll();
    }
}
