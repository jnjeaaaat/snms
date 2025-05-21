package org.jnjeaaaat.snms.domain.auth.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.UNMATCHED_DEFAULT_FILE;

public class UnmatchedDefaultFile extends CustomException {
    public UnmatchedDefaultFile() {
        super(UNMATCHED_DEFAULT_FILE);
    }
}
