package cl.duoc.ms_auth.servicios;

import cl.duoc.ms_auth.dtos.AuthLoginRequest;
import cl.duoc.ms_auth.dtos.AuthLoginResponse;

public interface AuthService {
    AuthLoginResponse login(AuthLoginRequest request);
}
