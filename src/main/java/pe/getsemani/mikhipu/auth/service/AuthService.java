package pe.getsemani.mikhipu.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.getsemani.mikhipu.auth.dto.JwtAuthResponse;
import pe.getsemani.mikhipu.auth.dto.LoginRequest;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.user.repository.UserRepository;
import pe.getsemani.mikhipu.security.JwtTokenProvider;

@Service
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public JwtAuthResponse authenticate(LoginRequest request) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            );
            var authentication = authenticationManager.authenticate(authToken);
            String jwt = tokenProvider.generateToken(authentication);
            return new JwtAuthResponse(jwt, TOKEN_TYPE);

        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }

}
