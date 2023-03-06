package com.doras.web.stellight.api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for {@link Ban}.
 */
public interface BanRepository extends JpaRepository<Ban, Long> {
}
