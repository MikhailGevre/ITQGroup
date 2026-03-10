package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.repository.DocumentRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DocumentTransactionalService {
    private final DocumentRepository documentRepository;
    private final RegisterService registerService;

    @Transactional
    public void registerAndApprove(Long documentId) {
        int checkRegister = registerService.approveDocument(documentId);
        if (checkRegister == 0) {
            throw new RuntimeException("Can't approve document in registers");
        }
        int updateRows = documentRepository.approveDocument(documentId);
        if (updateRows == 0) {
            throw new RuntimeException("Can't approve document in documents");
        }
    }
}
