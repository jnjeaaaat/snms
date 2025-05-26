package org.jnjeaaaat.snms.domain.member.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.NOT_FOUND_MEMBER;

public class NotFoundMember extends CustomException {

    public NotFoundMember() {
        super(NOT_FOUND_MEMBER);
    }
}
