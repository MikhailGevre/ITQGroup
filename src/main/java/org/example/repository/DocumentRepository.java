package org.example.repository;

import org.example.dto.DocumentUpdateRow;
import org.example.entity.Document;
import org.example.entity.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {


    @Query("""
            select d
            from Document d
            where d.id in :documentIds
            """)
    List<Document> findAllByIdsByPageable(List<Long> documentIds, Pageable pageable);


    @Query(value = """
            
              WITH input_ids AS (
                SELECT unnest(cast(:documentIds as bigint[])) AS id
            ),
            doc_state AS (
                SELECT d.id, d.status
                FROM documents d
                JOIN input_ids i ON i.id = d.id
            ),
            updated AS (
                UPDATE documents d
                SET status = 'SUBMITTED'
                FROM doc_state s
                WHERE d.id = s.id
                  AND s.status = 'DRAFT'
                RETURNING d.id
            )
            SELECT
                i.id,
                CASE
                    WHEN u.id IS NOT NULL THEN 'SUCCESS'
                    WHEN s.id IS NULL THEN 'NOT_FOUND'
                    ELSE 'CONFLICT'
                END AS result
            FROM input_ids i
            LEFT JOIN doc_state s ON s.id = i.id
            LEFT JOIN updated u ON u.id = i.id;
            """, nativeQuery = true)
    List<DocumentUpdateRow> sendToApprove(@Param("documentIds") Long[] documentIds);


    @Query(value = """
            WITH input_ids AS (
            SELECT unnest(cast(:documentIds as bigint[])) AS id
            ),
            doc_state AS (
            SELECT d.id, d.status
            FROM documents d
            JOIN input_ids i ON i.id = d.id
                        ),
            updated AS (
                UPDATE documents d
                SET status = 'CANDIDATE'
                    FROM doc_state AS s
                    WHERE d.id = s.id
                        AND d.status = 'SUBMITTED'
                RETURNING d.id
            )
            SELECT
            i.id,
            CASE
                WHEN u.id IS NOT NULL THEN 'CANDIDATE'
                WHEN s.id IS NULL THEN 'NOT_FOUND'
                ELSE 'CONFLICT'
            END AS result
            FROM input_ids i
            LEFT JOIN updated u ON u.id = i.id
            LEFT JOIN doc_state s ON s.id = i.id;
            """, nativeQuery = true)
    List<DocumentUpdateRow> checkCandidates(@Param("documentIds") Long[] documentIds);


    @Query(value = """
                WITH input_ids AS (
                    SELECT unnest(cast(:documentIds as bigint[])) AS id
                ),
                    updated AS (
                        UPDATE documents d
                               SET status = 'APPROVED'
                               FROM input_ids i
                               WHERE d.id = i.id
                               AND status = 'CANDIDATE'
                               RETURNING d.id
                    )
                SELECT i.id
                       FROM input_ids i
                LEFT JOIN documents d ON d.id = i.id;
            """, nativeQuery = true)
    Long[] approveCandidates(@Param("documentIds") Long[] documentIds);


    @Query("""
            select d from Document d
            where (:status is null or d.status =:status)
            and (:author is null or d.author =:author)
            and d.createdAt >= :createdFrom
            and d.createdAt < :createdTo
            """)
    List<Document> getDocuments(@Param("status") Status status,
                                @Param("author") String author,
                                @Param("createdFrom") LocalDateTime createdFrom,
                                @Param("createdTo") LocalDateTime createdTo);


    @Modifying
    @Query("""
            update Document d
            set d.status = 'APPROVED'
            where d.id = :documentId
                and d.status <> 'APPROVED'
            """
    )
    int approveDocument(@Param("documentId") Long documentId);

    @Query("""
        select d.id from Document d
        where d.status =:status
        """)
    List<Long> findAllByStatus(@Param("status") Status status, Pageable pageable);
}
