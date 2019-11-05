package dataAccess;

/**
 * Una clase de excepción para los errores del acceso a datos.
 * The class that extends of exception for data access errors.
 * @author Ricardo Peinado Lastra
 */
public class DAOException extends Exception {

    /**
     * Creates a new instance of <code>DAOException</code> without detail
     * message.
     */
    public DAOException() {
    }

    /**
     * Constructs an instance of <code>DAOException</code> with the specified
     * detail message.
     *
     * @param msg El mensaje / the detail message.
     */
    public DAOException(String msg) {
        super(msg);
    }
}
