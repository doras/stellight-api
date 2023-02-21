package com.doras.web.stellight.api.exception;

/**
 * Unchecked exception thrown when the {@link com.doras.web.stellight.api.domain.stellar.Stellar} data is not found.
 */
public class StellarNotFoundException extends DataNotFoundException {

    /**
     * Constructor with ID that data is not found by.
     * Using default detailed message of {@link StellarNotFoundException} with given {@code id}.
     * @param id the ID that the data was attempted to be found but failed.
     */
    public StellarNotFoundException(Long id) {
        super("해당 스텔라가 없습니다. id = " + id);
    }
}
