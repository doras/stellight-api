package com.doras.web.stellight.api.domain.user;

import com.doras.web.stellight.api.domain.BaseDateEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * User entity.
 */
@Getter
@NoArgsConstructor
@Entity
public class Users extends BaseDateEntity {
    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * (Required) email address
     */
    @Column(nullable = false)
    private String email;

    /**
     * (Required) role of the user
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Constructor of Users
     * @param email values for {@link #email}
     * @param role values for {@link #role}
     */
    @Builder
    public Users(String email, Role role) {
        this.email = email;
        this.role = role;
    }

    /**
     * Get key of {@link #role}
     * @return key of role
     */
    public String getRoleKey() {
        return this.role.getKey();
    }
}
