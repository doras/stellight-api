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
     * SNS ID of OAuth user
     */
    private final String snsId;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String snsId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.snsId = snsId;
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
                .snsId((String) response.get("id"))
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
                .snsId(snsId)
                .role(Role.USER)
                .build();
    }
}
