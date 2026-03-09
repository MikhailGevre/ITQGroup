package org.example.repository;

import org.example.dto.DocumentUpdateRow;
import org.example.entity.Document;
import org.example.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
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
                SELECT unnest(:documentIds) AS id
            ),
            updated AS (
                UPDATE documents d
                SET status = 'SUBMITTED'
                    FROM input_ids i
                    WHERE d.id = i.id
                        AND d.status = 'DRAFT'
                RETURNING d.id
            )
            SELECT
                i.id,
                CASE
                    WHEN u.id IS NOT NULL THEN 'SUCCESS'
                    WHEN d.id IS NULL THEN 'NOT_FOUND'
                    ELSE 'CONFLICT'
                END AS result
            FROM input_ids i
            LEFT JOIN updated u ON u.id = i.id
            LEFT JOIN documents d ON d.id = i.id;
            """, nativeQuery = true)
    List<DocumentUpdateRow> sendToApprove(@Param("documentIds") List<Long> documentIds);


    @Query(value = """
            WITH input_ids AS (
            SELECT unnest(:documentIds) AS id
            ),
            updated AS (
                UPDATE documents d
                SET status = 'CANDIDATE'
                    FROM input_ids i
                    WHERE d.id = i.id
                        AND d.status = 'SUBMITTED'
                RETURNING d.id
            )
            SELECT
            i.id,
            CASE
                WHEN u.id IS NOT NULL THEN 'CANDIDATE'
                WHEN d.id IS NULL THEN 'NOT_FOUND'
                ELSE 'CONFLICT'
            END AS result
            FROM input_ids i
            LEFT JOIN updated u ON u.id = i.id
            LEFT JOIN documents d ON d.id = i.id;
            """, nativeQuery = true)
    List<DocumentUpdateRow> checkCandidates(@Param("documentIds") List<Long> documentIds);


    @Query(value = """
                WITH input_ids AS (
                    SELECT unnest(:documentIds) AS id
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
    List<Long> approveCandidates(@Param("documentIds") List<Long> documentIds);


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

    @Query("""
            select d from Document d
            where d in :document
            and d.status = 'SUBMITTED'
            """
    )
    Document approveDocument(@Param("document") Document document);
}
