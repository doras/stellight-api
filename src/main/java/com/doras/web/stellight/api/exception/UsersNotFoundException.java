package com.doras.web.stellight.api.exception;

/**
 * Unchecked exception thrown when the {@link com.doras.web.stellight.api.domain.user.Users} data is not found.
 */
public class UsersNotFoundException extends DataNotFoundException {

    /**
     * Constructor with id that user is not found by.
     * Using default detailed message of {@link UsersNotFoundException} with given {@code id}.
     * @param id the id that the data was attempted to be found but failed.
     */
    public UsersNotFoundException(Long id) {
        super("해당 사용자가 없습니다. id = " + id);
    }

    /**
     * Constructor with snsId that data is not found by.
     * Using default detailed message of {@link UsersNotFoundException} with given {@code snsId}.
     * @param snsId the snsId that the data was attempted to be found but failed.
     */
    public UsersNotFoundException(String snsId) {
        super("해당 사용자가 없습니다. snsId = " + snsId);
    }
}
