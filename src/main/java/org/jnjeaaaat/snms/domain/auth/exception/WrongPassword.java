package org.jnjeaaaat.snms.domain.auth.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.WRONG_PASSWORD;

public class WrongPassword extends CustomException {

    public WrongPassword() {
        super(WRONG_PASSWORD);
    }
}
