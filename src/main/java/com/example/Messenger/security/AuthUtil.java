package com.example.Messenger.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public String getJWTToken(String email){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, "");
        try {
            authenticationManager.authenticate(authToken);
        }catch (Exception ignore){}

        return jwtUtil.generateToken(email);
    }
}