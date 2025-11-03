package cl.duoc.ms_auth.security;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Componente encargado de proveer las claves criptográficas RSA utilizadas para firmar y verificar los tokens JWT.
 * Lee las claves pública y privada en formato PEM desde las propiedades de la aplicación.
 */
@Component
public class KeyProvider {
    private final RSAKey rsaKey;

    /**
     * Constructor que inicializa el proveedor de claves.
     * Carga las claves pública y privada desde las propiedades de la aplicación, las combina y las parsea
     * en un objeto {@link RSAKey} de Nimbus.
     *
     * @param publicPem La clave pública en formato PEM, inyectada desde la propiedad "auth.jwt.rsa.public".
     * @param privatePem La clave privada en formato PEM, inyectada desde la propiedad "auth.jwt.rsa.private".
     * @throws Exception si ocurre un error al parsear las claves.
     */
    public KeyProvider(
            @Value("${auth.jwt.rsa.public}") String publicPem,
            @Value("${auth.jwt.rsa.private}") String privatePem) throws Exception {
        // Se combinan ambos PEM (público y privado) para construir el objeto RSAKey completo.
        this.rsaKey = RSAKey.parseFromPEMEncodedObjects(publicPem + "\n" + privatePem).toRSAKey();
    }

    /**
     * Devuelve la clave RSA completa (pública y privada).
     * Esta clave se utiliza para firmar los tokens JWT.
     *
     * @return la instancia de {@link RSAKey} completa.
     */
    public RSAKey rsaKey() { return rsaKey; }

    /**
     * Devuelve solo la parte pública de la clave RSA en formato JWK (JSON Web Key).
     * Esta clave se expone públicamente (por ejemplo, en el endpoint de JWKS)
     * para que los clientes puedan verificar la firma de los tokens.
     *
     * @return una instancia de {@link RSAKey} que contiene solo la clave pública.
     */
    public RSAKey publicJwk() { return rsaKey.toPublicJWK(); }
}
