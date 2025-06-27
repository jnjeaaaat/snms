package org.jnjeaaaat.dto.member;

import lombok.Builder;

@Builder
public record ExistsMemberResponse(
        boolean exists
) {

}
