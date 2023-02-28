package com.doras.web.stellight.api.exception;

/**
 * Unchecked exception thrown when the {@link com.doras.web.stellight.api.domain.user.Users} data is not found.
 */
public class UsersNotFoundException extends DataNotFoundException {

    /**
     * Constructor with ID that data is not found by.
     * Using default detailed message of {@link UsersNotFoundException} with given {@code email}.
     * @param email the email that the data was attempted to be found but failed.
     */
    public UsersNotFoundException(String email) {
        super("해당 사용자가 없습니다. email = " + email);
    }
}
