package com.doras.web.stellight.api.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Role Enum.
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    /**
     * User role which is default role.
     */
    USER("ROLE_USER", "일반 사용자"),

    /**
     * Admin role for only administrator.
     */
    ADMIN("ROLE_ADMIN", "관리자");

    /**
     * key of role
     */
    private final String key;

    /**
     * title of role
     */
    private final String title;
}
