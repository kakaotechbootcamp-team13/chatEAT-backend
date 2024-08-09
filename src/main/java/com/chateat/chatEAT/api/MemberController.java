package com.chateat.chatEAT.api;

import com.chateat.chatEAT.auth.userdetails.CustomUserDetails;
import com.chateat.chatEAT.domain.member.request.MemberJoinRequest;
import com.chateat.chatEAT.domain.member.request.MemberUpdateRequest;
import com.chateat.chatEAT.domain.member.request.MemberWithdrawRequest;
import com.chateat.chatEAT.domain.member.request.UpdatePasswordRequest;
import com.chateat.chatEAT.domain.member.response.EmailCheckResponse;
import com.chateat.chatEAT.domain.member.response.MemberJoinResponse;
import com.chateat.chatEAT.domain.member.response.MemberUpdateResponse;
import com.chateat.chatEAT.domain.member.response.MemberWithdrawResponse;
import com.chateat.chatEAT.domain.member.response.MyInfoResponse;
import com.chateat.chatEAT.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<MemberJoinResponse> join(@RequestBody final MemberJoinRequest request) {
        MemberJoinResponse response = memberService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public ResponseEntity<MemberUpdateResponse> update(@AuthenticationPrincipal CustomUserDetails user,
                                                       @RequestBody final MemberUpdateRequest request) {
        MemberUpdateResponse response = memberService.update(request, user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/update-password")
    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal CustomUserDetails user,
                                               @RequestBody final UpdatePasswordRequest request) {
        memberService.updatePassword(request, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/withdraw")
    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public ResponseEntity<MemberWithdrawResponse> withdraw(@AuthenticationPrincipal CustomUserDetails user,
                                                           @RequestBody final MemberWithdrawRequest request) {
        MemberWithdrawResponse response = memberService.withdraw(request, user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/myInfo")
    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public ResponseEntity<MyInfoResponse> myInfo(@AuthenticationPrincipal CustomUserDetails user) {
        MyInfoResponse response = memberService.myInfo(user.getUsername());
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
}
