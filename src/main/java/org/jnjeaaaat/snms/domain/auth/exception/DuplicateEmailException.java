package org.jnjeaaaat.snms.domain.auth.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.DUPLICATE_EMAIL;

public class DuplicateEmailException extends CustomException {

    public DuplicateEmailException() {
        super(DUPLICATE_EMAIL);
    }
}
