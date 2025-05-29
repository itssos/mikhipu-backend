package pe.getsemani.mikhipu.persons.student.repository;

import java.util.Set;

public interface StudentRepresentativeRepository {
    void removeRepresentativesFromStudent(Long studentId, Set<Long> representativeIds);
}
