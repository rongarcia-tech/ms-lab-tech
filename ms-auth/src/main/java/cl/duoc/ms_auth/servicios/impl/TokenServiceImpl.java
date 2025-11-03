package cl.duoc.ms_auth.servicios.impl;

import cl.duoc.ms_auth.entidades.Role;
import cl.duoc.ms_auth.entidades.User;
import cl.duoc.ms_auth.servicios.TokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de tokens {@link TokenService}.
 * Se encarga de la generación de tokens de acceso JWT utilizando claves asimétricas RSA.
 */
@Service
public class TokenServiceImpl implements TokenService {

    private final RSAKey rsaKey;
    private final String issuer;
    private final long expirationMinutes;

    /**
     * Constructor que inicializa el servicio de tokens.
     * Carga y valida las claves RSA pública y privada, el emisor y el tiempo de expiración desde las propiedades de la aplicación.
     *
     * @param privatePemRaw     La clave privada en formato PEM (PKCS#8), leída de las propiedades.
     * @param publicPemRaw      La clave pública en formato PEM, leída de las propiedades.
     * @param issuer            El emisor del token (issuer), leído de las propiedades.
     * @param expirationMinutes El tiempo de vida del token en minutos, leído de las propiedades.
     * @throws IllegalStateException si hay un error al cargar o validar las claves.
     */
    public TokenServiceImpl(
            @Value("${auth.jwt.rsa.private:}") String privatePemRaw,
            @Value("${auth.jwt.rsa.public:}")  String publicPemRaw,
            @Value("${auth.jwt.issuer}")        String issuer,
            @Value("${auth.jwt.expiration-minutes}") long expirationMinutes
    ) {
        try {
            // 1. Normalizar los strings de las claves PEM (quitar caracteres extraños, normalizar saltos de línea).
            String privatePem = normalizePem(privatePemRaw);
            String publicPem  = normalizePem(publicPemRaw);

            // 2. Realizar validaciones de pre-condición.
            require(!publicPem.isBlank(),  "auth.jwt.rsa.public vacío o no definido");
            require(!privatePem.isBlank(), "auth.jwt.rsa.private vacío o no definido");
            require(publicPem.contains("BEGIN PUBLIC KEY"),   "El PUBLIC PEM no tiene cabecera BEGIN PUBLIC KEY");
            require(privatePem.contains("BEGIN PRIVATE KEY"), "La PRIVATE PEM debe ser PKCS#8 (BEGIN PRIVATE KEY)");

            // 3. Parsear las claves. Nimbus puede parsear un string que contiene ambas claves (pública y privada).
            RSAKey parsed = RSAKey.parseFromPEMEncodedObjects(publicPem + "\n" + privatePem).toRSAKey();

            // 4. Asegurarse de que tanto la parte pública como la privada de la clave se hayan cargado correctamente.
            require(parsed.toRSAPublicKey() != null,  "No se pudo obtener la clave pública desde el PEM");
            require(parsed.toRSAPrivateKey() != null, "No se pudo obtener la clave privada (¿está en PKCS#8?)");

            this.rsaKey = parsed;
            this.issuer = issuer;
            this.expirationMinutes = expirationMinutes;

        } catch (Exception e) {
            // Si alguna validación falla, lanzar una excepción clara para facilitar la depuración.
            throw new IllegalStateException(
                    "Error inicializando TokenServiceImpl: " + rootMsg(e) +
                            ". Revisa auth.jwt.rsa.public / auth.jwt.rsa.private en application.properties (.env). " +
                            "La privada debe ser PKCS#8.", e);
        }
    }

    /**
     * Normaliza un string de clave PEM para su correcto procesamiento.
     * Limpia caracteres de escape y normaliza los saltos de línea.
     */
    private static String normalizePem(String raw) {
        if (raw == null) return "";
        // Elimina comillas y comas que a veces se añaden en variables de entorno.
        String clean = raw.replace("\"", "").replace(",", "");
        // Convierte '\n' literales a saltos de línea reales.
        String newlineNormalized = clean.replace("\\n", "\n");
        return newlineNormalized.trim();
    }

    /**
     * Helper para lanzar una excepción si una condición no se cumple.
     */
    private static void require(boolean cond, String msg) {
        if (!cond) throw new IllegalArgumentException(msg);
    }

    /**
     * Helper para obtener el mensaje de la causa raíz de una excepción.
     */
    private static String rootMsg(Throwable t) {
        Throwable x = t;
        while (x.getCause() != null) x = x.getCause();
        return x.getMessage();
    }


    /**
     * Genera un token de acceso JWT para un usuario específico.
     *
     * @param user La entidad del usuario para quien se genera el token.
     * @return Un string que representa el token JWT firmado y serializado.
     */
    @Override
    public String generateAccessToken(User user) {
        try {
            // 1. Definir el tiempo de emisión (ahora) y de expiración.
            var now = Instant.now();
            var exp = now.plus(expirationMinutes, ChronoUnit.MINUTES);

            // 2. Recopilar los roles del usuario.
            var roles = user.getRoles().stream().map(Role::getName).sorted().collect(Collectors.toList());

            // 3. Construir el conjunto de claims (payload) del JWT.
            var claims = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .subject(user.getUsername())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(exp))
                    .claim("userId", user.getExternalId().toString())
                    .claim("roles", roles)
                    .claim("labCode", roles.contains("LAB_TECH") ? user.getLabCode() : null)
                    .build();

            // 4. Crear la cabecera del JWS (JWT Firmado) especificando el algoritmo.
            var header = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();

            // 5. Crear el objeto JWT firmado.
            var jwt = new SignedJWT(header, claims);

            // 6. Firmar el JWT con la clave privada RSA.
            jwt.sign(new RSASSASigner(rsaKey.toPrivateKey()));

            // 7. Serializar el JWT a su formato de string compacto.
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generando JWT", e);
        }
    }

    /**
     * Calcula y devuelve el instante exacto en que un nuevo token expiraría.
     *
     * @return El {@link Instant} de expiración.
     */
    @Override
    public Instant getExpirationInstant() {
        return Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);
    }
}
