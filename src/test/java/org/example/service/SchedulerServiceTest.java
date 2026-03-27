package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.DocumentSubmitApproveDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.utils.client.DocumentClient;
import org.utils.exception.JsonParseException;

import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {
    @Mock
    private DocumentClient client;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private SchedulerService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "resourceDocuments", "/api/v1/documents");
        ReflectionTestUtils.setField(service, "workerSubmitName", "SUBMIT-worker");
        ReflectionTestUtils.setField(service, "workerApproveName", "APPROVE-worker");
    }

    @Test
    public void workerSubmit_returnTheMethod_shouldReturnMethod() {
        ReentrantLock lock = mock(ReentrantLock.class);
        ReflectionTestUtils.setField(service, "submitLock", lock);

        given(lock.tryLock()).willReturn(false);

        service.workerSubmit();

        then(lock).should().tryLock();
        then(lock).should(never()).unlock();
    }

    @Test
    public void workerSubmit_returnTheMethodStatusCode_shouldReturnToSubmitMethodWithStatusCode() {
        String resourceDocumentsGet = "/api/v1/documents/DRAFT/batch-status";
        ResponseEntity<String> raw = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

        given(client.exchange(HttpMethod.GET, resourceDocumentsGet, null)).willReturn(raw);

        service.workerSubmit();

        then(objectMapper).shouldHaveNoInteractions();
    }

    @Test
    public void workerSubmit_throwChainedExceptionHttpGET_shouldThrowJsonProcessingAndParseException() {
        setUp();
        ResponseEntity<String> raw = new ResponseEntity<>("invalid-body", HttpStatus.OK);

        given(client.exchange(
                eq(HttpMethod.GET),
                contains("batch-status"),
                isNull()
        )).willReturn(raw);
        try {
            given(objectMapper.readValue(any(String.class), eq(Long[].class)))
                    .willThrow(new JsonProcessingException("exception") {
                    });
        } catch (JsonProcessingException e) {
        }

        assertThatThrownBy(() -> service.workerSubmit())
                .isInstanceOf(JsonParseException.class)
                .hasMessageContaining("exception");
    }

    @Test
    public void workerSubmit_returnMethodHttpPUT_shouldReturnTheMethod() {
        setUp();
        Long[] ids = new Long[]{1L, 2L, 3L};
        ResponseEntity<String> raw = new ResponseEntity<>("invalid-body", HttpStatus.OK);
        ResponseEntity<String> request = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

        given(client.exchange(eq(HttpMethod.GET), contains("batch-status"), isNull())).willReturn(raw);
        try {
            given(objectMapper.readValue(any(String.class), eq(Long[].class))).willReturn(ids);
        } catch (JsonProcessingException e) {
        }

        given(client.exchange(eq(HttpMethod.PUT), contains("submit"), any(DocumentSubmitApproveDto.class)))
                .willReturn(request);

        service.workerSubmit();

        then(client).should()
                .exchange(eq(HttpMethod.GET), contains("batch-status"), isNull());

        then(client).should()
                .exchange(eq(HttpMethod.PUT), contains("submit"), any(DocumentSubmitApproveDto.class));
    }

    @Test
    public void workerSubmit_successfulCase_shouldSucceedMethod() {
        setUp();
        Long[] ids = new Long[]{1L, 2L, 3L};
        ResponseEntity<String> raw = new ResponseEntity<>("invalid-body", HttpStatus.OK);
        ResponseEntity<String> request = new ResponseEntity<>(HttpStatus.OK);

        given(client.exchange(eq(HttpMethod.GET), contains("batch-status"), isNull())).willReturn(raw);
        try {
            given(objectMapper.readValue(any(String.class), eq(Long[].class))).willReturn(ids);
        } catch (JsonProcessingException e) {
        }

        given(client.exchange(eq(HttpMethod.PUT), contains("submit"), any(DocumentSubmitApproveDto.class)))
                .willReturn(request);

        service.workerSubmit();

        then(client).should()
                .exchange(eq(HttpMethod.GET), contains("batch-status"), isNull());

        then(client).should()
                .exchange(eq(HttpMethod.PUT), contains("submit"), any(DocumentSubmitApproveDto.class));
    }

    @Test
    public void workerApprove_lock_shouldLockMethod() {
        ReentrantLock lock = mock(ReentrantLock.class);
        ReflectionTestUtils.setField(service, "approveLock", lock);

        given(lock.tryLock()).willReturn(false);

        service.workerApprove();

        then(lock).should().tryLock();
        then(lock).should(never()).unlock();
    }

    @Test
    public void workerApprove_returnTheMethodStatusCode_shouldReturnToSubmitMethodWithStatusCode() {
        String resourceDocumentsGet = "/api/v1/documents/SUBMITTED/batch-status";
        ResponseEntity<String> raw = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

        given(client.exchange(HttpMethod.GET, resourceDocumentsGet, null)).willReturn(raw);

        service.workerApprove();

        then(objectMapper).shouldHaveNoInteractions();
    }

    @Test
    public void workerApprove_throwChainedExceptionHttpGET_shouldThrowJsonProcessingAndParseException() {
        setUp();
        ResponseEntity<String> raw = new ResponseEntity<>("invalid-body", HttpStatus.OK);

        given(client.exchange(
                eq(HttpMethod.GET),
                contains("batch-status"),
                isNull()
        )).willReturn(raw);
        try {
            given(objectMapper.readValue(any(String.class), eq(Long[].class)))
                    .willThrow(new JsonProcessingException("exception") {
                    });
        } catch (JsonProcessingException e) {
        }

        assertThatThrownBy(() -> service.workerApprove())
                .isInstanceOf(JsonParseException.class)
                .hasMessageContaining("exception");
    }

    @Test
    public void workerApprove_returnMethodHttpPUT_shouldReturnTheMethod() {
        setUp();
        Long[] ids = new Long[]{1L, 2L, 3L};
        ResponseEntity<String> raw = new ResponseEntity<>("invalid-body", HttpStatus.OK);
        ResponseEntity<String> request = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

        given(client.exchange(eq(HttpMethod.GET), contains("batch-status"), isNull())).willReturn(raw);
        try {
            given(objectMapper.readValue(any(String.class), eq(Long[].class))).willReturn(ids);
        } catch (JsonProcessingException e) {
        }

        given(client.exchange(eq(HttpMethod.PUT), contains("approved"), any(DocumentSubmitApproveDto.class)))
                .willReturn(request);

        service.workerApprove();

        then(client).should()
                .exchange(eq(HttpMethod.GET), contains("batch-status"), isNull());

        then(client).should()
                .exchange(eq(HttpMethod.PUT), contains("approved"), any(DocumentSubmitApproveDto.class));
    }

    @Test
    public void workerApprove_successfulCase_shouldSucceedMethod() {
        setUp();
        Long[] ids = new Long[]{1L, 2L, 3L};
        ResponseEntity<String> raw = new ResponseEntity<>("invalid-body", HttpStatus.OK);
        ResponseEntity<String> request = new ResponseEntity<>(HttpStatus.OK);

        given(client.exchange(eq(HttpMethod.GET), contains("batch-status"), isNull())).willReturn(raw);
        try {
            given(objectMapper.readValue(any(String.class), eq(Long[].class))).willReturn(ids);
        } catch (JsonProcessingException e) {
        }

        given(client.exchange(eq(HttpMethod.PUT), contains("approved"), any(DocumentSubmitApproveDto.class)))
                .willReturn(request);

        service.workerApprove();

        then(client).should()
                .exchange(eq(HttpMethod.GET), contains("batch-status"), isNull());

        then(client).should()
                .exchange(eq(HttpMethod.PUT), contains("approved"), any(DocumentSubmitApproveDto.class));
    }


}


