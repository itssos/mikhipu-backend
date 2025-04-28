package pe.getsemani.mikhipu.auth.service;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.getsemani.mikhipu.auth.dto.JwtAuthResponse;
import pe.getsemani.mikhipu.auth.dto.LoginRequest;
import pe.getsemani.mikhipu.person.dto.PersonDTO;
import pe.getsemani.mikhipu.person.entity.Person;
import pe.getsemani.mikhipu.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.person.repository.PersonRepository;
import pe.getsemani.mikhipu.role.repository.RoleRepository;
import pe.getsemani.mikhipu.security.JwtTokenProvider;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;

@Service
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       UserRepository userRepository,
                       PersonRepository personRepository,
                       RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
    }

    public JwtAuthResponse authenticate(LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(authToken);
        String jwt = tokenProvider.generateToken(authToken);

        // Obtener la entidad User por username
        User userEntity = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Optional<Person> personOptional = personRepository.findByUserUsername(request.getUsername());
        PersonDTO personDTO = personOptional.map(PersonMapper::mapPersonToDTO).orElse(null);

        return new JwtAuthResponse(jwt, TOKEN_TYPE, personDTO);
    }
}
