package org.jnjeaaaat.snms.domain.auth.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.UNMATCHED_PASSWORD;

public class UnmatchedPassword extends CustomException {
    public UnmatchedPassword() {
        super(UNMATCHED_PASSWORD);
    }
}
