package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "histories")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author", nullable = false, length = 64)
    private String author;

    @Column(name = "comment", length = 256)
    private String comment;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, updatable = false)
    private Document document;

    @Column(name = "created_at")
    LocalDateTime createdAt;

}
