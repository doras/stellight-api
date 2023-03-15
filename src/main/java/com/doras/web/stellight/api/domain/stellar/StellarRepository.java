package com.doras.web.stellight.api.domain.stellar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA repository for {@link Stellar}.
 */
public interface StellarRepository extends JpaRepository<Stellar, Long> {
    List<Stellar> findAllByOrderById();
}
