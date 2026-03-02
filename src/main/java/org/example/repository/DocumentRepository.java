package org.example.repository;

import org.example.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

}
