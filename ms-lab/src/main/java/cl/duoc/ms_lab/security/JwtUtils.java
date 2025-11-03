package cl.duoc.ms_lab.security;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.*;

public class JwtUtils {
    private final RSAPublicKey publicKey;
    private final String expectedIssuer;

    public JwtUtils(RSAPublicKey publicKey, String expectedIssuer) {
        this.publicKey = publicKey;
        this.expectedIssuer = expectedIssuer;
    }

    public Optional<JwtPayload> validateAndParse(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            var header = jwt.getHeader();
            if (!Objects.equals(header.getType(), JOSEObjectType.JWT)) return Optional.empty();

            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            if (!jwt.verify(verifier)) return Optional.empty();

            var claims = jwt.getJWTClaimsSet();
            if (expectedIssuer != null && !expectedIssuer.equals(claims.getIssuer())) return Optional.empty();

            var now = Instant.now();
            if (claims.getExpirationTime() == null || now.isAfter(claims.getExpirationTime().toInstant())) return Optional.empty();

            String subject = claims.getSubject();
            String userId  = Objects.toString(claims.getClaim("userId"), null);
            String labCode = Objects.toString(claims.getClaim("labCode"), null);

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.getClaim("roles");
            if (roles == null) roles = List.of();

            return Optional.of(new JwtPayload(subject, userId, roles, labCode));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static RSAPublicKey toPublicKey(RSAKey rsaKey) {
        try { return rsaKey.toRSAPublicKey(); } catch (Exception e) { throw new RuntimeException(e); }
    }

    public record JwtPayload(String username, String userId, List<String> roles, String labCode) {}
}
