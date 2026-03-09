package org.example.repository;

import org.example.entity.Register;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegisterRepository extends JpaRepository<Register, Long> {

    List<Long> registrationDocument(List<Long> documentIds);
}
