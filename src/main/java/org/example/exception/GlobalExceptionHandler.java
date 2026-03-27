package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.utils.exception.JsonParseException;
import org.utils.exception.ServiceRequestError;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId={} Entity not found: {}", errorId, ex.getMessage(), ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Ресурс не найден");
        problem.setDetail(ex.getMessage());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorId", errorId);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId={} Validation failed", errorId, ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Ошибка валидации");
        problem.setDetail("Некорректные данные запроса");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        problem.setProperty("errors", errors);
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorId", errorId);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId={} Constraint violation", errorId, ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Ошибка валидации");
        problem.setDetail("Некорректные параметры запроса");

        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(v -> {
            String field = v.getPropertyPath().toString();
            errors.put(field, v.getMessage());
        });

        problem.setProperty("errors", errors);
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorId", errorId);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId={} Type mismatch", errorId, ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Ошибка параметра запроса");
        problem.setDetail("Некорректный формат параметра");

        problem.setProperty("parameter", ex.getName());
        problem.setProperty("receivedValue",
                ex.getValue() != null ? ex.getValue().toString() : "null");

        if (ex.getRequiredType() != null) {
            problem.setProperty("requiredType", ex.getRequiredType().getSimpleName());
        }

        if (ex.getRequiredType() == LocalDate.class) {
            problem.setProperty("expectedFormat", "yyyy/MM/dd");
            problem.setProperty("example", "2024/03/15");
        }

        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorId", errorId);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ProblemDetail> handleSql(
            SQLException ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId={} SQL error [{}]: {}", errorId, ex.getSQLState(), ex.getMessage(), ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Ошибка сервера");
        problem.setDetail("Произошла ошибка при работе с базой данных");

        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorId", errorId);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<ProblemDetail> handleBadSql(
            BadSqlGrammarException ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId={} SQL syntax error", errorId, ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Ошибка сервера");
        problem.setDetail("Произошла ошибка при работе с базой данных");

        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorId", errorId);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId={} Unexpected error", errorId, ex);

        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Ошибка внутреннего сервера");
        problem.setDetail("Произошла неожиданная ошибка");

        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorId", errorId);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    @ExceptionHandler(RegisterDocumentException.class)
    public ResponseEntity<ProblemDetail> handleRegisterDocument(
            RegisterDocumentException ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId={} Register document error: {}", errorId, ex.getMessage(), ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Ошибка регистрации документа");
        problem.setDetail(ex.getMessage());

        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorId", errorId);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ProblemDetail> handleJsonParse(
            JsonParseException ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId={} JSON parse error: {}", errorId, ex.getMessage(), ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Ошибка обработки JSON");
        problem.setDetail("Некорректный формат данных");

        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorId", errorId);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(ServiceRequestError.class)
    public ResponseEntity<ProblemDetail> handleServiceRequestError(
            ServiceRequestError ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId={} External service error: {}", errorId, ex.getMessage(), ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problem.setTitle("Ошибка внешнего сервиса");
        problem.setDetail(ex.getMessage());

        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errorId", errorId);
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problem);
    }
}

