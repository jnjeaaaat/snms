package org.jnjeaaaat.global.event.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    FOLLOW("팔로우 알림"),
    POST_CREATED("게시물 생성 알림"),

    ;

    private final String title;
}
