package org.jnjeaaaat.domain.member.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CountType {

    FOLLOW("follow", ErrorCode.FOLLOW_LIMIT_EXCEEDED),
    BLOCK("block", ErrorCode.BLOCK_LIMIT_EXCEEDED);

    private final String type;
    private final ErrorCode limitExceededError;
}
