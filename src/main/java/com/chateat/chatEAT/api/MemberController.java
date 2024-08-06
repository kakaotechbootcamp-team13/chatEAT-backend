package com.chateat.chatEAT.api;

import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.chateat.chatEAT.domain.member.request.MemberUpdateRequest;
import com.chateat.chatEAT.domain.member.request.MemberWithdrawRequest;
import com.chateat.chatEAT.domain.member.request.UpdatePasswordRequest;
import com.chateat.chatEAT.domain.member.response.*;
import com.chateat.chatEAT.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<MemberJoinResponse> join(@RequestBody final MemberJoinRequest request) {
        MemberJoinResponse response = memberService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public ResponseEntity<MemberUpdateResponse> update(@RequestBody final MemberUpdateRequest request) {
        MemberUpdateResponse response = memberService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update-password")
    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public ResponseEntity<Void> updatePassword(@RequestBody final UpdatePasswordRequest request) {
        memberService.updatePassword(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/withdraw")
    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public ResponseEntity<MemberWithdrawResponse> withdraw(@RequestBody final MemberWithdrawRequest request) {
        MemberWithdrawResponse response = memberService.withdraw(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/myInfo")
    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public ResponseEntity<MyInfoResponse> myInfo() {
        MyInfoResponse response = memberService.myInfo();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/email-check/{email}") // true = 사용 중인 이메일이 존재
    public ResponseEntity<EmailCheckResponse> checkEmail(@PathVariable("email") String email) {
        EmailCheckResponse response = memberService.checkEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/nickname-check/{nickname}")
    public ResponseEntity<Boolean> checkNickname(@PathVariable("nickname") String nickname) {
        boolean result = memberService.checkNickname(nickname);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/logout")
    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    }
}
