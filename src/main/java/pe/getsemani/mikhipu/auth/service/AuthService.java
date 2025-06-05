package pe.getsemani.mikhipu.auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.getsemani.mikhipu.auth.dto.JwtAuthResponse;
import pe.getsemani.mikhipu.auth.dto.LoginRequest;
import pe.getsemani.mikhipu.persons.person.dto.PersonResponseDTO;
import pe.getsemani.mikhipu.persons.person.entity.Person;
import pe.getsemani.mikhipu.persons.person.mapper.PersonMapper;
import pe.getsemani.mikhipu.persons.person.repository.PersonRepository;
import pe.getsemani.mikhipu.security.JwtTokenProvider;
import pe.getsemani.mikhipu.user.entity.User;
import pe.getsemani.mikhipu.user.repository.UserRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public JwtAuthResponse authenticate(LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(authToken);

        String jwt = tokenProvider.generateToken(authToken);

        User userEntity = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        Optional<Person> personOptional = personRepository.findByUserUsername(request.getUsername());
        PersonResponseDTO personResponseDTO = personOptional
                .map(personMapper::toDto)
                .orElse(null);

        return new JwtAuthResponse(jwt, TOKEN_TYPE, personResponseDTO);
    }
}
