package com.doras.web.stellight.api.config.auth;

import com.doras.web.stellight.api.config.auth.dto.OAuthAttributes;
import com.doras.web.stellight.api.config.auth.dto.SessionUser;
import com.doras.web.stellight.api.domain.user.Users;
import com.doras.web.stellight.api.domain.user.UsersRepository;
import com.doras.web.stellight.api.exception.BannedUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

/**
 * custom OAuth2 user service class
 */
@RequiredArgsConstructor
@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UsersRepository usersRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract necessary information from user attributes
        OAuthAttributes attributes =
                OAuthAttributes.of(oAuth2User.getAttributes());

        // Get(and save if needed) user and add to session attribute.
        Users user = save(attributes);

        // check ban
        if (user.getBan() != null) {
            throw new BannedUserException();
        }

        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    /**
     * Returns found user by email address and save user if it is new user.
     * @param attributes DTO with information about user
     * @return found user
     */
    private Users save(OAuthAttributes attributes) {
        Users user = usersRepository.findByEmail(attributes.getEmail())
                .orElse(attributes.toEntity());

        return usersRepository.save(user);
    }
}
