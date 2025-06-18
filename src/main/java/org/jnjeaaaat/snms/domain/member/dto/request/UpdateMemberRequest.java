package org.jnjeaaaat.snms.domain.member.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.jnjeaaaat.snms.global.validator.annotation.Trim;

import static org.jnjeaaaat.snms.global.constant.DtoValid.NICKNAME_REGEX;

public record UpdateMemberRequest(

        @Trim
        @Nullable
        @Pattern(regexp = NICKNAME_REGEX, message = "{validation.constraints.ValidNickname.message}")
        String nickname,

        @Nullable
        @Size(max = 500, message = "{validation.constraints.MaxLength500.message}")
        String introduce
) {

}
