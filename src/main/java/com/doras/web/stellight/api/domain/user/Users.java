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
     * (Required) OAuth user identifier
     */
    @Column(nullable = false)
    private String snsId;

    /**
     * (Required) role of the user
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Ban info, if null the user is not banned.
     */
    @OneToOne(mappedBy = "users")
    private Ban ban;

    /**
     * Constructor of Users
     * @param snsId values for {@link #snsId}
     * @param role values for {@link #role}
     */
    @Builder
    public Users(String snsId, Role role) {
        this.snsId = snsId;
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
