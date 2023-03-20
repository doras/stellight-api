package com.doras.web.stellight.api.domain.user;

import com.doras.web.stellight.api.domain.BaseDateEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Stream;

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
     * (Required) roles of the user
     */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles;

    /**
     * Ban info, if null the user is not banned.
     */
    @OneToOne(mappedBy = "users")
    private Ban ban;

    /**
     * Constructor of Users
     * @param snsId values for {@link #snsId}
     * @param roles values for {@link #roles}
     */
    @Builder
    public Users(String snsId, Set<Role> roles) {
        this.snsId = snsId;
        this.roles = roles;
    }

    /**
     * Get keys of {@link #roles}
     * @return keys of roles
     */
    public Stream<String> getRoleKeys() {
        return this.roles.stream().map(Role::getKey);
    }
}
