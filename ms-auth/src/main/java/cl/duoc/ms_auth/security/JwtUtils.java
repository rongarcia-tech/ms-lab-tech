package cl.duoc.ms_auth.security;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.*;

/**
 * Clase de utilidad para manejar la validación y el parseo de JSON Web Tokens (JWT).
 * Utiliza la biblioteca Nimbus JOSE+JWT para procesar los tokens.
 */
public class JwtUtils {
    private final RSAPublicKey publicKey;
    private final String expectedIssuer;

    /**
     * Constructor para inicializar la utilidad con la clave pública y el emisor esperado.
     *
     * @param publicKey La clave pública RSA utilizada para verificar la firma del token.
     * @param expectedIssuer El emisor (issuer) que se espera encontrar en los claims del token.
     */
    public JwtUtils(RSAPublicKey publicKey, String expectedIssuer) {
        this.publicKey = publicKey;
        this.expectedIssuer = expectedIssuer;
    }

    /**
     * Valida y parsea un token JWT.
     * Realiza una serie de verificaciones: tipo de token, firma, emisor y fecha de expiración.
     * Si todas las validaciones son exitosas, extrae el payload y lo devuelve.
     *
     * @param token El string del token JWT a validar.
     * @return Un {@link Optional} que contiene el {@link JwtPayload} si el token es válido, o un Optional vacío en caso contrario.
     */
    public Optional<JwtPayload> validateAndParse(String token) {
        try {
            // 1. Parsear el token.
            SignedJWT jwt = SignedJWT.parse(token);

            // 2. Verificar el tipo de cabecera (debe ser "JWT").
            var header = jwt.getHeader();
            if (!Objects.equals(header.getType(), JOSEObjectType.JWT)) return Optional.empty();

            // 3. Verificar la firma del token usando la clave pública.
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            if (!jwt.verify(verifier)) return Optional.empty();

            // 4. Obtener los claims (payload) del token.
            var claims = jwt.getJWTClaimsSet();

            // 5. Verificar el emisor (issuer).
            if (expectedIssuer != null && !expectedIssuer.equals(claims.getIssuer())) return Optional.empty();

            // 6. Verificar la fecha de expiración.
            var now = Instant.now();
            if (claims.getExpirationTime() == null || now.isAfter(claims.getExpirationTime().toInstant())) return Optional.empty();

            // 7. Extraer los datos del payload.
            String subject = claims.getSubject(); // Generalmente el username
            String userId  = Objects.toString(claims.getClaim("userId"), null);
            String labCode = Objects.toString(claims.getClaim("labCode"), null);

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.getClaim("roles");
            if (roles == null) roles = List.of();

            // 8. Devolver el payload si todo es correcto.
            return Optional.of(new JwtPayload(subject, userId, roles, labCode));
        } catch (Exception e) {
            // Si ocurre cualquier excepción durante el proceso, el token se considera inválido.
            return Optional.empty();
        }
    }

    /**
     * Método de utilidad estático para convertir una {@link RSAKey} de Nimbus a una {@link RSAPublicKey} de Java.
     *
     * @param rsaKey La clave RSA en formato Nimbus.
     * @return La clave pública en formato estándar de Java.
     * @throws RuntimeException si la conversión falla.
     */
    public static RSAPublicKey toPublicKey(RSAKey rsaKey) {
        try { return rsaKey.toRSAPublicKey(); } catch (Exception e) { throw new RuntimeException(e); }
    }

    /**
     * Un record que representa el payload extraído de un token JWT válido.
     *
     * @param username El nombre de usuario (del claim "sub").
     * @param userId El ID del usuario.
     * @param roles La lista de roles del usuario.
     * @param labCode El código de laboratorio asociado al usuario.
     */
    public record JwtPayload(String username, String userId, List<String> roles, String labCode) {}
}
