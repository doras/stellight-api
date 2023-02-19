package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.service.stellar.StellarService;
import com.doras.web.stellight.api.web.dto.StellarsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stellars")
public class StellarsController {

    private final StellarService stellarService;

    @GetMapping("/{id}")
    public StellarsResponseDto findById(@PathVariable Long id) {
        return stellarService.findById(id);
    }
}
