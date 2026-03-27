package org.example.service;

import org.example.dto.*;
import org.example.entity.Document;
import org.example.entity.Result;
import org.example.entity.Status;
import org.example.exception.EntityNotFoundException;
import org.example.exception.RegisterDocumentException;
import org.example.mapper.DocumentMapper;
import org.example.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
    @Mock
    private DocumentRepository repository;
    @Mock
    private DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);
    @Mock
    private RegisterService registerService;
    @Mock
    private DocumentTransactionalService transactionalService;
    @Captor
    private ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
    @Captor
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    @InjectMocks
    private DocumentService service;


    @Test
    public void create_createDocument_shouldCreateDocumentAndStatusDraft() {
        DocumentDto expectedDto = new DocumentDto("Author", "Title");
        DocumentRequestDto resultDto = DocumentRequestDto.builder()
                .status(Status.DRAFT)
                .build();
        Document saved = new Document();
        Document entity = new Document();
        given(mapper.toEntity(expectedDto)).willReturn(entity);
        given(repository.save(any(Document.class))).willReturn(saved);
        given(mapper.toDto(saved)).willReturn(resultDto);

        DocumentRequestDto requestServiceDto = service.create(expectedDto);

        assertThat(resultDto).isEqualTo(requestServiceDto);
        then(repository).should().save(documentCaptor.capture());
        assertThat(Status.DRAFT).isEqualTo(documentCaptor.getValue().getStatus());
        assertThat(Status.DRAFT).isEqualTo(resultDto.status());
    }

    @Test
    public void getDocument_throwExceptionEntity_shouldThrowExceptionEntityNotFoundException() {
        long id = 1L;
        given(repository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.getDocument(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(id));

        then(repository).should().findById(id);
    }

    @Test
    public void getDocument_getDocument_shouldReturnDocument() {
        long id = 1L;
        DocumentRequestDto expectedDto = DocumentRequestDto.builder()
                .id(id)
                .build();
        Document entity = new Document();
        entity.setId(id);

        given(repository.findById(id)).willReturn(Optional.of(entity));
        given(mapper.toDto(entity)).willReturn(expectedDto);

        DocumentRequestDto requestDto = service.getDocument(id);

        assertThat(requestDto).isEqualTo(expectedDto);
        then(repository).should().findById(id);
        then(mapper).should().toDto(entity);
    }

    @Test
    public void getBatch_getDocumentBatch_shouldReturnDocumentBatch() {

        List<Long> ids = List.of(1L, 2L, 3L);
        Document documentOne = new Document();
        Document documentTwo = new Document();
        Document documentThree = new Document();
        DocumentRequestDto resultDtoOne = DocumentRequestDto.builder()
                .id(1L)
                .build();
        DocumentRequestDto resultDtoTwo = DocumentRequestDto.builder()
                .id(2L)
                .build();
        DocumentRequestDto resultDtoThree = DocumentRequestDto.builder()
                .id(3L)
                .build();

        documentOne.setId(1L);
        documentTwo.setId(2L);
        documentThree.setId(3L);

        List<Document> documents = List.of(documentOne, documentTwo, documentThree);
        DocumentBatchDto expectedDto = DocumentBatchDto.builder()
                .ids(ids)
                .page(1)
                .size(10)
                .direction("DESC")
                .build();

        given(mapper.toDto(documentOne)).willReturn(resultDtoOne);
        given(mapper.toDto(documentTwo)).willReturn(resultDtoTwo);
        given(mapper.toDto(documentThree)).willReturn(resultDtoThree);
        given(repository.findAllByIdsByPageable(eq(ids), pageableCaptor.capture())).willReturn(documents);

        List<DocumentRequestDto> responseDto = service.getBatch(expectedDto);

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(10);

        Sort.Order order = pageable.getSort().getOrderFor("createdAt");
        assertThat(order).isNotNull();
        assertThat(order.getProperty()).isEqualTo("createdAt");
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);

        assertThat(responseDto).containsExactly(resultDtoOne, resultDtoTwo, resultDtoThree);
    }

    @Test
    public void getBatchByStatus_getBatchByStatus_shouldReturnBathByStatus() {
        int batchSize = 500;
        int defaultPage = 0;
        ReflectionTestUtils.setField(service, "batchSize", batchSize);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);

        given(repository.findAllByStatus(eq(Status.DRAFT), any(Pageable.class))).willReturn(List.of());


        service.getBatchByStatus("DRAFT");

        then(repository).should().findAllByStatus(statusCaptor.capture(), pageableCaptor.capture());
        Status status = statusCaptor.getValue();
        assertThat(status).isNotNull();
        assertThat(status).isEqualTo(Status.DRAFT);

        Pageable pageable = pageableCaptor.getValue();
        Sort.Order order = pageable.getSort().getOrderFor("id");
        assertThat(order).isNotNull();
        assertThat(order.getProperty()).isEqualTo("id");
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(pageable.getPageNumber()).isEqualTo(defaultPage);
        assertThat(pageable.getPageSize()).isEqualTo(batchSize);
    }

    @Test
    public void submit_submitDocument_shouldSubmitDocumentReturnEnumMap() {
        DocumentUpdateRow updateRowOne = prepareInterfaceProjection(1L, "SUCCESS");
        DocumentUpdateRow updateRowTwo = prepareInterfaceProjection(2L, "NOT_FOUND");
        List<DocumentUpdateRow> updateRows = List.of(updateRowOne, updateRowTwo);

        Long[] documentsIds = new Long[]{1L, 2L, 3L};
        given(repository.sendToApprove(documentsIds)).willReturn(updateRows);

        EnumMap<Result, List<Long>> result = service.submit(documentsIds);

        assertThat(result).isNotNull();
        assertThat(result)
                .containsEntry(Result.SUCCESS, List.of(1L))
                .containsEntry(Result.NOT_FOUND, List.of(2L));
    }

    @Test
    public void approve_candidateIsNull_shouldResponseEnumMapWithoutCandidate() {
        DocumentUpdateRow updateRowOne = prepareInterfaceProjection(1L, "CONFLICT");
        DocumentUpdateRow updateRowTwo = prepareInterfaceProjection(2L, "NOT_FOUND");
        List<DocumentUpdateRow> updateRows = List.of(updateRowOne, updateRowTwo);
        Long[] documentsIds = new Long[]{1L, 2L, 3L};

        given(repository.checkCandidates(documentsIds)).willReturn(updateRows);
        EnumMap<Result, List<Long>> result = service.approve(documentsIds);

        assertThat(result).isNotNull();
        assertThat(result)
                .containsEntry(Result.CONFLICT, List.of(1L))
                .containsEntry(Result.NOT_FOUND, List.of(2L));
        assertThat(result).size().isEqualTo(2);
    }

    @Test
    public void approve_approveCandidate_shouldResponseEnumMapWithApproved() {
        DocumentUpdateRow updateRowOne = prepareInterfaceProjection(1L, "CANDIDATE");
        DocumentUpdateRow updateRowTwo = prepareInterfaceProjection(2L, "CANDIDATE");
        List<DocumentUpdateRow> updateRows = List.of(updateRowOne, updateRowTwo);
        Long[] documentsIds = new Long[]{1L, 2L};
        Long[] approveIds = new Long[]{1L, 2L};
        List<Long> registerDocumentIds = Arrays.asList(approveIds);

        given(repository.checkCandidates(documentsIds)).willReturn(updateRows);
        given(repository.approveCandidates(documentsIds)).willReturn(approveIds);
        given(registerService.registerDocumentBatch(approveIds)).willReturn(registerDocumentIds);

        EnumMap<Result, List<Long>> result = service.approve(documentsIds);
        assertThat(result).isNotNull();
        assertThat(result)
                .containsEntry(Result.APPROVED, List.of(1L, 2L));
        assertThat(result).size().isEqualTo(1);
    }

    @Test
    public void findDocuments_findDocumentsByFilter_shouldResponseListDto() {
        DocumentSearchDto dto =
                new DocumentSearchDto(Status.DRAFT, "Author",
                        LocalDate.now().minusDays(2), LocalDate.now().minusDays(1));
        Document documentOne = new Document();
        Document documentTwo = new Document();
        documentOne.setId(1L);
        documentTwo.setId(2L);
        List<Document> documents = List.of(documentOne, documentTwo);
        DocumentRequestDto requestDtoOne = DocumentRequestDto.builder()
                .id(documentOne.getId())
                .author(documentOne.getAuthor())
                .status(documentOne.getStatus())
                .build();
        DocumentRequestDto requestDtoTwo = DocumentRequestDto.builder()
                .id(documentTwo.getId())
                .author(documentTwo.getAuthor())
                .status(documentTwo.getStatus())
                .build();

        given(mapper.toDto(any()))
                .willAnswer(invocation -> {
                    Document doc = invocation.getArgument(0);
                    return DocumentRequestDto.builder()
                            .id(doc.getId())
                            .author(doc.getAuthor())
                            .status(doc.getStatus())
                            .build();
                });

        given(repository.getDocuments(eq(Status.DRAFT), eq("Author"),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(documents);


        List<DocumentRequestDto> result = service.findDocuments(dto);

        assertThat(result).isNotNull();
        assertThat(result).containsExactly(requestDtoOne, requestDtoTwo);
        assertThat(result).size().isEqualTo(2);
    }

    @Test
    public void concurrencyTest_throwException_shouldThrowEntityNotFoundException() {
        int threads = 10;
        int attempts = 20;
        Long documentId = 1L;

        given(repository.findById(documentId)).willReturn(Optional.empty());
        assertThatThrownBy(
                () -> service.concurrencyTest(documentId, threads, attempts))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(documentId));
    }

    @Test
    public void concurrencyTest_setStatus_shouldReturnDtoSuccessOneThread() {
        int threads = 5;
        int attempts = 10;
        Long documentId = 1L;
        Document document = new Document();
        document.setId(documentId);
        document.setStatus(Status.APPROVED);


        AtomicBoolean first = new AtomicBoolean(false);

        doAnswer(invocation -> {
            if (first.compareAndSet(false, true)) {
                return null;
            }
            throw new RegisterDocumentException("already approved");
        }).when(transactionalService).registerAndApprove(documentId);

        given(repository.findById(documentId))
                .willReturn(Optional.of(document));

        DocumentConcurrencyResultDto result =
                service.concurrencyTest(documentId, threads, attempts);

        assertThat(result.successful()).isEqualTo(1);
        assertThat(result.failed()).isEqualTo(attempts - 1);
        assertThat(result.status()).isEqualTo(Status.APPROVED);
    }


    private DocumentUpdateRow prepareInterfaceProjection(long id, String result) {
        return new DocumentUpdateRow() {
            @Override
            public Long getId() {
                return id;
            }

            @Override
            public String getResult() {
                return result;
            }
        };
    }
}