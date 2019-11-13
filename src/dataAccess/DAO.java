package dataAccess;

import clases.*;

/**
 * Interfaz DAO, con las querys a la base de datos.
 * DAO interface, with the querys to the data base.
 * @author Luis Gómez
 */
public interface DAO {
    
    public boolean verificarLogin(String login) throws DAOException;
    
    public User verificarLoginPassword(String login, String password) throws DAOException;
    
    public boolean registrarUser(User usuario) throws DAOException;
    
    public void ultimoAcceso(String login) throws DAOException;

     
}
