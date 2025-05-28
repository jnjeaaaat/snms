package org.jnjeaaaat.snms.domain.auth.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.DUPLICATE_UID;

public class DuplicateUidException extends CustomException {

    public DuplicateUidException() {
        super(DUPLICATE_UID);
    }
}
