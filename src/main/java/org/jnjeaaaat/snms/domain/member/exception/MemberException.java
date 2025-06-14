package org.jnjeaaaat.snms.domain.member.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;
import org.jnjeaaaat.snms.global.exception.ErrorCode;

public class MemberException extends CustomException {

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
