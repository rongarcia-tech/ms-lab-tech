package cl.duoc.ms_auth.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Convertidor de atributos de JPA para mapear un objeto {@link UUID} a un arreglo de bytes (RAW(16) en la base de datos)
 * y viceversa. Esto permite almacenar UUIDs de una manera más eficiente en la base de datos.
 */
@Converter(autoApply = false)
public class UuidRaw16Converter implements AttributeConverter<UUID, byte[]> {

    /**
     * Convierte un {@link UUID} en un arreglo de 16 bytes para ser almacenado en la base de datos.
     *
     * @param attribute El UUID a convertir.
     * @return Un arreglo de bytes que representa el UUID, o null si el UUID de entrada es null.
     */
    @Override
    public byte[] convertToDatabaseColumn(UUID attribute) {
        if (attribute == null) return null;
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(attribute.getMostSignificantBits());
        bb.putLong(attribute.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * Convierte un arreglo de 16 bytes de la base de datos en un {@link UUID}.
     *
     * @param dbData El arreglo de bytes a convertir.
     * @return El {@link UUID} resultante, o null si el arreglo de bytes es null o no tiene una longitud de 16.
     */
    @Override
    public UUID convertToEntityAttribute(byte[] dbData) {
        if (dbData == null || dbData.length != 16) return null;
        ByteBuffer bb = ByteBuffer.wrap(dbData);
        long most = bb.getLong();
        long least = bb.getLong();
        return new UUID(most, least);
    }
}
