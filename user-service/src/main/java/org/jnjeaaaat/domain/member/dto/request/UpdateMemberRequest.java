package org.jnjeaaaat.domain.member.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.jnjeaaaat.global.validator.annotation.Trim;

import static org.jnjeaaaat.global.constant.DtoValid.NICKNAME_REGEX;

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
