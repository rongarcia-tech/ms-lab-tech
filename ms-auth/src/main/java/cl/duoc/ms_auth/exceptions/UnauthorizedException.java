package cl.duoc.ms_auth.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String msg){ super(msg); }
}