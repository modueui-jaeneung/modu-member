package com.modu.MemberServer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {

    String email;
    String password;
    String repeatPassword;
    String nickname;
    String address;
    String introduceMyself;
}
