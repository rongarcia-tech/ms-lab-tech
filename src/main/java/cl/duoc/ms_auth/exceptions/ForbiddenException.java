package cl.duoc.ms_auth.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String msg) { super(msg); }
}