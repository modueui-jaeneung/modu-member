package com.modu.MemberServer.controller;


import com.modu.MemberServer.dto.LoginRequestDto;
import com.modu.MemberServer.dto.SignUpDto;
import com.modu.MemberServer.dto.SignUpSocialDto;
import com.modu.MemberServer.dto.UpdateMemberDto;
import com.modu.MemberServer.entity.Member;
import com.modu.MemberServer.entity.enums.SocialType;
import com.modu.MemberServer.service.MemberService;
import com.modu.MemberServer.utils.EncryptHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    // 회원가입 -> 성공 시 201
    // zz
    @PostMapping("/members")
    public ResponseEntity<Message> signup(@RequestBody SignUpDto signUpDto) {
        log.info("POST /members signUpDto={}", signUpDto);
        Long memberId = memberService.signupLocal(signUpDto);
        return new ResponseEntity<>(new Message(memberId.toString()), HttpStatus.CREATED);
    }

    // 소셜 회원가입 -> 성공 시 201
    @PostMapping("/members/social")
    public ResponseEntity<Message> signupSocial(@ModelAttribute SignUpSocialDto signUpSocialDto) {
        Long memberId = memberService.signupSocial(signUpSocialDto);
        return new ResponseEntity<>(new Message(memberId.toString()), HttpStatus.CREATED);
    }

    // 로그인에서 회원을 인증하기 위하여 이메일과 소셜타입으로 회원조회 (회원ID, 비밀번호 값 얻기)
    // memberService에서 회원을 조회했는데 0건이 나온다면 NoSuchElementException 예외 던짐.
    // 컨트롤러가 받아서 400 으로 응답함
    @GetMapping("/members/id")
    public ResponseEntity<LoginResponseDto> findMemberIdForLogin(@ModelAttribute LoginRequestDto loginDto) {
        SocialType socialType = SocialType.valueOf(loginDto.getSocialType());
        Member member = memberService.findByEmailAndSocialType(loginDto.getEmail(), socialType);

        return new ResponseEntity<>(new LoginResponseDto(member.getId(), member.getPassword()), HttpStatus.OK);
    }


    // 프로필 보여주기
    @GetMapping("/members/{memberId}")
    public ResponseEntity<ProfileResponseDto> getMemberProfile(@PathVariable Long memberId) {
        Member findMember = memberService.findById(memberId);
        ProfileResponseDto profileResponseDto = new ProfileResponseDto(
                findMember.getNickname(),
                findMember.getAddress(),
                findMember.getIntroduceMyself());
        return new ResponseEntity(profileResponseDto, HttpStatus.OK);
    }

    // 회원정보 수정 페이지 띄울 때 보여줄 회원정보
    @GetMapping("/members/info/{memberId}")
    public ResponseEntity<MemberInfoDto> getMemberInfo(@PathVariable Long memberId) {
        Member findMember = memberService.findById(memberId);

        MemberInfoDto memberInfoDto = new MemberInfoDto(
                findMember.getEmail(),
                findMember.getNickname(),
                findMember.getSocialType(),
                findMember.getAddress(),
                findMember.getIntroduceMyself()
        );
        return new ResponseEntity<>(memberInfoDto, HttpStatus.OK);
    }

    // 회원정보 수정 (닉네임, 주소, 비밀번호, 자기소개 수정 가능. 이외의 것들은 수정 불가)
    @PutMapping("/members/info/{memberId}")
    public ResponseEntity<Message> updateMember(@PathVariable Long memberId,
                                                HttpServletRequest request,
                                                @RequestParam String email,
                                                @RequestParam String nickname,
                                                @RequestParam String address,
                                                @RequestParam String introduceMyself,
                                                @RequestParam String socialType) {
        String password;
        String repeatPassword;
        if (request.getParameterMap().containsKey("password") && request.getParameterMap().containsKey("repeatPassword")) {
            password = request.getParameter("password");
            repeatPassword = request.getParameter("repeatPassword");
        } else {
            password = UUID.randomUUID().toString();
            repeatPassword = password;
        }
        UpdateMemberDto updateMemberDto = new UpdateMemberDto(email, socialType, password, repeatPassword, nickname, address, introduceMyself);
        memberService.updateMember(memberId, updateMemberDto);
        return new ResponseEntity<>(new Message("회원수정 성공!"), HttpStatus.OK);
    }

//     회원탈퇴. soft delete 로서 Member의 isDeleted 필드를 1로 바꿀 것.
//     동시에 회원과 관련된 팔로우 데이터는 hard delete로서 모두 delete 쿼리 보내어 데이터 자체를 지울 것.
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Message> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return new ResponseEntity<>(new Message("회원탈퇴 성공"), HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    public class ProfileResponseDto {
        String nickname;
        String address;
        String introduceMyself;
    }

    @Data
    @AllArgsConstructor
    public class Message {
        String message;
    }

    @Data
    @AllArgsConstructor
    public class LoginResponseDto {
        Long id;
        String encryptedPassword;
    }

    @Data
    @AllArgsConstructor
    public class MemberInfoDto {
        String email;
        String nickname;
        SocialType socialType;
        String address;
        String introduceMyself;
    }
}
