package cl.duoc.ms_auth.config;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Un record que representa una respuesta paginada para las API.
 * Es una estructura de datos inmutable para devolver una porción (una página) de una colección más grande.
 *
 * @param <T> El tipo del contenido dentro de la página.
 * @param content La lista de elementos en la página actual.
 * @param page El número de la página actual (basado en cero).
 * @param size El tamaño de la página (cuántos elementos contiene).
 * @param totalElements El número total de elementos en todas las páginas.
 * @param totalPages El número total de páginas.
 * @param first Verdadero si esta es la primera página.
 * @param last Verdadero si esta es la última página.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    /**
     * Método de fábrica estático para crear una instancia de {@link PageResponse} a partir de un objeto {@link Page} de Spring Data.
     *
     * @param <T> El tipo del contenido de la página.
     * @param p El objeto {@link Page} del cual crear la respuesta.
     * @return una nueva instancia de {@link PageResponse} poblada con los datos de la página.
     */
    public static <T> PageResponse<T> from(Page<T> p) {
        return new PageResponse<>(
                p.getContent(), p.getNumber(), p.getSize(),
                p.getTotalElements(), p.getTotalPages(),
                p.isFirst(), p.isLast()
        );
    }
}
