package com.chateat.chatEAT.api;

import com.chateat.chatEAT.domain.member.request.AuthorizeRoleRequest;
import com.chateat.chatEAT.domain.member.response.AuthorizeRoleResponse;
import com.chateat.chatEAT.domain.member.response.MemberListResponse;
import com.chateat.chatEAT.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    @PatchMapping("/change-role/{memberId}")
    public ResponseEntity<AuthorizeRoleResponse> changeRole(@PathVariable("memberId") Long memberId, @RequestBody AuthorizeRoleRequest authorizeRoleRequest) {
        AuthorizeRoleResponse response = memberService.changRole(authorizeRoleRequest, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/members")
    public ResponseEntity<Page<MemberListResponse>> getMemberListPage(
            @RequestParam(value = "p", defaultValue = "1") int p,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(p - 1, size, Sort.by("nickname").descending());
        Page<MemberListResponse> responses = memberService.findAllMembers(pageable);
        return ResponseEntity.ok(responses);
    }
}
