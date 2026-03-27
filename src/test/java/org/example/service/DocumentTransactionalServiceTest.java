package org.example.service;

import org.example.exception.RegisterDocumentException;
import org.example.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DocumentTransactionalServiceTest {
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private RegisterService registerService;
    @InjectMocks
    private DocumentTransactionalService service;

    @Test
    public void registerAndApprove_throwException_shouldThrowExceptionAboutRegister() {
        long documentId = 1;
        given(registerService.approveDocument(documentId)).willReturn(0);

        assertThatThrownBy(() -> service.registerAndApprove(documentId))
                .isInstanceOf(RegisterDocumentException.class)
                .hasMessageContaining(String.valueOf(documentId));
    }

    @Test
    public void registerAndApprove_throwException_shouldThrowExceptionAboutUpdateRows() {
        long documentId = 1;
        given(registerService.approveDocument(documentId)).willReturn(1);
        given(documentRepository.approveDocument(documentId)).willReturn(0);

        assertThatThrownBy(() -> service.registerAndApprove(documentId))
                .isInstanceOf(RegisterDocumentException.class)
                .hasMessageContaining(String.valueOf(documentId));
    }

    @Test
    public void registerAndApprove_positiveRegister_shouldRegisterAndApprove() {
        long documentId = 1;
        given(registerService.approveDocument(documentId)).willReturn(1);
        given(documentRepository.approveDocument(documentId)).willReturn(1);

        service.registerAndApprove(documentId);

        then(registerService).should().approveDocument(documentId);
        then(documentRepository).should().approveDocument(documentId);
    }
}