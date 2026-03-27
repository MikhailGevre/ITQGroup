package org.example.service;

import org.example.repository.RegisterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceTest {
    @Mock
    private RegisterRepository registerRepository;
    @InjectMocks
    private RegisterService registerService;

    @Test
    public void registerDocument_registerDocument_shouldReturnListLong() {
        Long[] documentIds = new Long[]{1L, 2L, 3L, 4L, 5L};
        List<Long> listIds = Arrays.asList(documentIds);
        given(registerRepository.batchInsert(documentIds)).willReturn(listIds);

        registerService.registerDocumentBatch(documentIds);

        then(registerRepository).should().batchInsert(documentIds);
    }

    @Test
    public void approveDocument_approveDocument_shouldApproveAndReturnInt() {
        long documentId = 1L;

        given(registerRepository.approveDocument(documentId)).willReturn(1);

        Integer result = registerService.approveDocument(documentId);

        then(registerRepository).should().approveDocument(documentId);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1);
    }
}