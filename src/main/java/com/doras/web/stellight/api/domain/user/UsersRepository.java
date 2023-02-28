package com.doras.web.stellight.api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA repository for {@link Users}.
 */
public interface UsersRepository extends JpaRepository<Users, Long> {

    /**
     * Find user by given email
     * @param email email of user
     * @return optional object of found user object
     */
    Optional<Users> findByEmail(String email);
}
