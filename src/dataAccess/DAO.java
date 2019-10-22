/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccess;

import clases.*;

/**
 * Interfaz DAO, con las querys a la base de datos.
 * @author Luis Gómez
 */
public interface DAO {
    
    public boolean verificarLogin(String login) throws DAOException;
    
    public User verificarLoginPassword(String login, String password) throws DAOException;
    
    public boolean registrarUser(User usuario) throws DAOException;
}
