package com.doras.web.stellight.api.config.auth.dto;

import com.doras.web.stellight.api.domain.user.Role;
import com.doras.web.stellight.api.domain.user.Users;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * DTO for OAuth attribute
 */
@Getter
public class OAuthAttributes {
    /**
     * Attributes of OAuth
     */
    private final Map<String, Object> attributes;

    /**
     * Key for name attribute
     */
    private final String nameAttributeKey;

    /**
     * Email address of OAuth user
     */
    private final String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.email = email;
    }

    /**
     * Make instance of OAuthAttributes with given attributes
     * @param attributes OAuth attributes
     * @return created instance
     */
    public static OAuthAttributes of(Map<String, Object> attributes) {
        return ofNaver(attributes);
    }

    /**
     * Make Naver OAuth instance with given attributes
     * @param attributes OAuth attributes
     * @return created instance
     */
    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .email((String) response.get("email"))
                .attributes(response)
                .nameAttributeKey("id")
                .build();
    }

    /**
     * Make Users entity from this object
     * @return created Users entity
     */
    public Users toEntity() {
        return Users.builder()
                .email(email)
                .role(Role.USER)
                .build();
    }
}
