package dev.andresbonelli.productcomparisonapi.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Custom Constructor to identify resource by id
     */
    public static ResourceNotFoundException byId(String resource, Long id) {
        return new ResourceNotFoundException(
                String.format("%s with ID %d not found", resource, id)
        );
    }
}
