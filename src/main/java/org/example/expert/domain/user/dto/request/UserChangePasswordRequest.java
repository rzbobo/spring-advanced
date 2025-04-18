package org.example.expert.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordRequest {

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String oldPassword;


    // Level 1-3 Validation
    // 비밀번호 유효성 검사를 DTO파트에서 진행하도록 변경  <- validation 라이브러리 활용하는거라고 언급
    // 조건 : "새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다."
    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.")
    // 정규식은 숫자 + 소문자 + 대문자로 이루어지게 구성
    // 정규식으로 표현한다고 치면 예외처리는 어떻게 하나 <- 그냥 어노테이션별로 구분해서 메세지 던지기
    // 정규식은 ^로 시작해서  $로 종료
    // 필수로 포함해야할 요소들을 () 내부에 작성, 이후 마지막에 []에서 포함했던 요소들 점검, {}를 통해 Size 설정
    private String newPassword;
}
