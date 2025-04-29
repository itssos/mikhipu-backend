package pe.getsemani.mikhipu.person.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.getsemani.mikhipu.person.dto.create.PersonCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.PersonResponseDTO;
import pe.getsemani.mikhipu.person.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_PERSONS')")
    public ResponseEntity<List<PersonResponseDTO>> getAllPersons() {
        return ResponseEntity.ok(personService.getAllPersons());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_PERSON')")
    public ResponseEntity<PersonResponseDTO> getPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.getPersonById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_PERSON')")
    public ResponseEntity<PersonResponseDTO> createPerson(@Valid @RequestBody PersonCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.createPerson(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_PERSON')")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
}
