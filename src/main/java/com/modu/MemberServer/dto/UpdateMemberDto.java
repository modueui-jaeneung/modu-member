package com.modu.MemberServer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateMemberDto {
    String email;
    String socialType;
    String password;
    String repeatPassword;
    String nickname;
    String address;
    String introduceMyself;
}
