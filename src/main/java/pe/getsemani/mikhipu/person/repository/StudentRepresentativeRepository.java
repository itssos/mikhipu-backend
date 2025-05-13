package pe.getsemani.mikhipu.person.repository;

import java.util.Set;

public interface StudentRepresentativeRepository {
    void removeRepresentativesFromStudent(Long studentId, Set<Long> representativeIds);
}
