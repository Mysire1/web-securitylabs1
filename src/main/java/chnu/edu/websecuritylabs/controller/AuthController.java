package chnu.edu.websecuritylabs.controller;

import chnu.edu.websecuritylabs.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Map<String, String> FULL_NAMES = Map.of(
            "john@mail.com", "John Lennon",
            "admin", "Admin Adminovych",
            "manager", "Manager Managerovych",
            "user", "User Userovych"
    );

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/authenticate")
    public String authenticate(@RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        return jwtService.generateToken(auth, FULL_NAMES.getOrDefault(auth.getName(), auth.getName()));
    }

    public record AuthRequest(String email, String password) {
    }
}
