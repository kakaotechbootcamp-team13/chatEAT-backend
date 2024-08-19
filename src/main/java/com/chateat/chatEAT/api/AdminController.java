package com.chateat.chatEAT.api;

import com.chateat.chatEAT.auth.principaldetails.PrincipalDetails;
import com.chateat.chatEAT.domain.member.request.AuthorizeRoleRequest;
import com.chateat.chatEAT.domain.member.response.AuthorizeRoleResponse;
import com.chateat.chatEAT.domain.member.response.MemberListPageResponse;
import com.chateat.chatEAT.domain.member.service.MemberService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    @PatchMapping("/change-role/{memberId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<AuthorizeRoleResponse> changeRole(@PathVariable("memberId") Long memberId,
                                                            @RequestBody AuthorizeRoleRequest authorizeRoleRequest) {
        AuthorizeRoleResponse response = memberService.changRole(authorizeRoleRequest, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/members")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getMemberListPage(
            @RequestParam(value = "p", defaultValue = "1") int p,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(p - 1, size, Sort.by("id").ascending());
        Page<MemberListPageResponse> responses = memberService.findAllMembers(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("members", responses.getContent());
        response.put("currentPage", responses.getNumber() + 1);
        response.put("totalItems", responses.getTotalElements());
        response.put("totalPages", responses.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/members/{memberId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal PrincipalDetails user,
                                             @PathVariable("memberId") Long memberId) {
        memberService.deleteMember(memberId, user.getUsername());
        return ResponseEntity.ok().build();
    }
}
