package com.doras.web.stellight.api.domain.user;

import com.doras.web.stellight.api.domain.CreatedDateEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Banned User entity.
 */
@Getter
@NoArgsConstructor
@Entity
public class Ban extends CreatedDateEntity {
    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Banned user
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    /**
     * Reason why the user is banned
     */
    @Column
    private String reason;

    /**
     * Constructor of Ban
     * @param users values for {@link #users}
     * @param reason values for {@link #reason}
     */
    @Builder
    public Ban(Users users, String reason) {
        this.users = users;
        this.reason = reason;
    }
}
