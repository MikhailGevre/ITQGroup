package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.RegisterDocumentException;
import org.example.repository.DocumentRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentTransactionalService {
    private final DocumentRepository documentRepository;
    private final RegisterService registerService;

    @Transactional
    public void registerAndApprove(Long documentId) {
        int checkRegister = registerService.approveDocument(documentId);
        if (checkRegister == 0) {
            log.error("Регистрации документа не прошла успешно {}", documentId);
            throw new RegisterDocumentException("Ошибка при регистрации документа в Реестр id документа " + documentId);
        }
        int updateRows = documentRepository.approveDocument(documentId);
        if (updateRows == 0) {
            log.error("Регистрации документа не прошла успешно {}", updateRows);
            throw new RegisterDocumentException("Ошибка при обновлении статуса документа в репозитории id документа "
                    + documentId);
        }
    }
}
