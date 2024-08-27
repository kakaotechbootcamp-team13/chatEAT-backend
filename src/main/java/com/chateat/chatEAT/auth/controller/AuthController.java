package com.chateat.chatEAT.auth.controller;

import com.chateat.chatEAT.auth.jwt.JwtTokenProvider;
import com.chateat.chatEAT.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PatchMapping("/reissue")
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        String encryptedRefreshToken = jwtTokenProvider.resolveRefreshTokenFromCookie(request);
        String newAccessToken = authService.reissueAccessToken(encryptedRefreshToken);
        jwtTokenProvider.accessTokenSetHeader(newAccessToken, response);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/logout")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String encryptedRefreshToken = jwtTokenProvider.resolveRefreshTokenFromCookie(request);
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        authService.logout(encryptedRefreshToken, accessToken);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
