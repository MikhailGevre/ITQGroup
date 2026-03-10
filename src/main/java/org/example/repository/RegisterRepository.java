package org.example.repository;

import org.example.entity.Register;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RegisterRepository extends JpaRepository<Register, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO registers (document_id, approved_at)
        SELECT document_id, NOW()
        FROM unnest(cast(:documentIds as bigint[])) AS document_id
        RETURNING id
        """, nativeQuery = true)
    List<Long> batchInsert(@Param("documentIds") Long[] documentIds);

    @Modifying
    @Query(value = """
       INSERT INTO registers (document_id, approved_at)
       VALUES (:documentId, NOW())
       """, nativeQuery = true)
    int approveDocument(@Param("documentId") Long documentId);
}
