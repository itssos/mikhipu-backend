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
import pe.getsemani.mikhipu.person.dto.create.RepresentativeCreateDTO;
import pe.getsemani.mikhipu.person.dto.response.RepresentativeResponseDTO;
import pe.getsemani.mikhipu.person.service.RepresentativeService;

import java.util.List;

@RestController
@RequestMapping("/api/representatives")
@RequiredArgsConstructor
public class RepresentativeController {

    private final RepresentativeService representativeService;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_REPRESENTATIVES')")
    public ResponseEntity<List<RepresentativeResponseDTO>> getAllRepresentatives() {
        return ResponseEntity.ok(representativeService.getAllRepresentatives());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_REPRESENTATIVE')")
    public ResponseEntity<RepresentativeResponseDTO> getRepresentativeById(@PathVariable Long id) {
        return ResponseEntity.ok(representativeService.getRepresentativeById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_REPRESENTATIVE')")
    public ResponseEntity<RepresentativeResponseDTO> createRepresentative(@Valid @RequestBody RepresentativeCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(representativeService.createRepresentative(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_REPRESENTATIVE')")
    public ResponseEntity<Void> deleteRepresentative(@PathVariable Long id) {
        representativeService.deleteRepresentative(id);
        return ResponseEntity.noContent().build();
    }
}