package cl.duoc.ms_auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    ProblemDetail handleUserBadRequest(BadRequestException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("User bad request");
        pd.setType(URI.create("https://api.localhost:8080/errors/student-not-found"));
        return pd;
    }

    @ExceptionHandler(ConflictException.class)
    ProblemDetail handleUserConflict(ConflictException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Conflict users");
        pd.setType(URI.create("https://api.localhost:8080errors/business-rule"));
        return pd;
    }

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail handleUserNotFound(NotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("User not found");
        pd.setType(URI.create("https://api.localhost:8080/errors/student-not-found"));
        return pd;
    }

    @ExceptionHandler(ForbiddenException.class)
    ProblemDetail handleUserForbidden(ForbiddenException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        pd.setTitle("Forbidden credentials");
        pd.setType(URI.create("https://api.localhost:8080errors/business-rule"));
        return pd;
    }

    @ExceptionHandler(UnauthorizedException.class)
    ProblemDetail handleUserUnauthorized(UnauthorizedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        pd.setTitle("Unauthorized credentials");
        pd.setType(URI.create("https://api.localhost:8080errors/business-rule"));
        return pd;
    }


    // Validación @Valid / @Validated: devuelve los errores de campos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        fe -> fe.getField(),
                        java.util.stream.Collectors.mapping(
                                fe -> fe.getDefaultMessage(), java.util.stream.Collectors.toList()
                        )
                ));

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setTitle("Validation failed");
        pd.setDetail("One or more fields are invalid.");
        pd.setType(URI.create("https://api.localhost:8080/errors/validation"));
        // datos extra (extensiones RFC-7807)
        pd.setProperty("errors", errors);
        return pd;
    }

    // Seguridad
    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Forbidden");
        pd.setDetail("You don't have permission to perform this action.");
        return pd;
    }

    // Fallback genérico
    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnhandled(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Internal Server Error");
        pd.setDetail("Unexpected error. Please contact support.");
        pd.setProperty("reason", ex.getClass().getSimpleName());
        return pd;
    }
}
