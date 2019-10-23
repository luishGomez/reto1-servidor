/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccess;

import clases.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Esta clase es la implementación de la interfaz DAO, que permite la conexión
 * con nuestra base de datos.
 * @author Luis Gómez
 */
public class DAOImplementation implements DAO{
    private static final Logger LOGGER = Logger.getLogger("dataAccess.DAOImplementation");
    private Connection con;
    private PreparedStatement pstmt;
    private ConexionPool poolBD;
    private ResourceBundle configFile;
    private String driverBD;
    private String urlBD;
    private String userBD;
    private String passwordBD;

    public DAOImplementation(ConexionPool poolBD, String configFile) {
        this.poolBD = poolBD;
        this.configFile = ResourceBundle.getBundle(configFile);
        this.driverBD = this.configFile.getString("Driver");
        this.urlBD = this.configFile.getString("Conn");
        this.userBD = this.configFile.getString("DBUser");
        this.passwordBD = this.configFile.getString("DBPass");
        LOGGER.getLogger("");
    }
    
    /**
     * Obtiene una conexión del pool para la base de datos.
     */
    private void abrirConexion(){
        LOGGER.info("Obteniendo conexión con la BD.");
        try{
           if(poolBD != null){
               this.con = poolBD.extraerConexion();
           }
           else{
               Class.forName(this.driverBD);
               this.con = 
                    DriverManager.getConnection(urlBD,userBD,passwordBD);
           }
           this.con.setAutoCommit(true);
        }catch(SQLException e){
            LOGGER.severe("Error al crear conexión con BD." + 
                    "No se puede obtener conexión:" + e.getMessage());
            /*throw new DAOException("Error al crear Conexión con BD."+
                           "No se puede obtener conexión:"+e.getMessage());*/
            
        }catch(ClassNotFoundException e){
            LOGGER.severe("Error al crear conexión con BD:" + 
                    "No se puede cargar la clase del Driver.");
            /*throw new DAOException("Error al crear Conexión con BD:"+
                           "No se puede cargar la clase del Driver.");*/
        }
    }
    
    /**
     * Cierra la conexión establecida con la base de datos.
     */
    private void cerrarConexion(){
        LOGGER.info("Liberando conexión con la BD.");
       try{
            //Si hay pool liberamos la conexión hacia el pool
           if(poolBD != null){
               poolBD.liberarConexion(con);
               this.con=null;
           }
           //Si no cerramos la conexión creada mediante el DriverManager
           else{
               this.con.close();
               this.con = null;
           }
        }catch(SQLException e){
            LOGGER.severe("Error al liberar Conexión con BD:\n" + 
                    "SQLERROR="+e.getMessage());
            /*throw new DAOException("Error al liberar Conexión con BD:\n"+
                           "SQLERROR="+e.getMessage());*/
            
        }
    }
    /**
     * Comprueba que el login no exista ya en la base de datos.
     * @param login login del usuario.
     * @return Boolean a true si ya existe ese login en la base de datos.
     * @throws DAOException 
     */
    @Override
    public boolean verificarLogin(String login) throws DAOException {
        boolean loginExiste = false;
        try{
            this.abrirConexion();
            String select = "select * from user where login='" + login+"'";
            pstmt = this.con.prepareStatement(select);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                loginExiste = true;
            }        
            rs.close();
            this.cerrarConexion();
        }catch (Exception e){
            LOGGER.severe(e.getMessage());
            this.cerrarConexion();
        }
        return loginExiste;
    }
    /**
     * Comprueba que el login y la contraseña existen y son correctos.
     * @param login login del usuario.
     * @param password contraseña del usuario.
     * @return Objeto User con toda la información del usuario.
     * @throws DAOException 
     */
    @Override
    public User verificarLoginPassword(String login, String password) 
            throws DAOException {
        boolean loginCorrecto = false;
        User usuario = null;
        try{
            this.abrirConexion();
            String select = "select * from user where login='" + login + 
                    "' and password='" + password+"'";
            pstmt = this.con.prepareStatement(select);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                loginCorrecto = true;
            }
            rs.close();
            if(loginCorrecto){
                select = "select * from user where login='" + login+"'";
                pstmt = this.con.prepareStatement(select);
                rs = pstmt.executeQuery();
                while(rs.next()){
                    UserStatus status;
                    UserPrivilege privilege;
                    if(rs.getInt("status") == 0){
                        status = UserStatus.ENABLED;
                    }else{
                        status = UserStatus.DISABLED;
                    }
                    if(rs.getInt("privilege") == 0){
                        privilege = UserPrivilege.ADMIN;
                    }else{
                        privilege = UserPrivilege.USER;
                    }
                    usuario = new User(rs.getInt("id"),
                            rs.getString("login"),rs.getString("email"),
                            rs.getString("fullname"),status,privilege,
                            rs.getString("password"),rs.getDate("lastAccess"),
                            rs.getDate("lastPasswordChange"));
                }
                rs.close();
            }
            this.cerrarConexion();
        }catch (Exception e){
            LOGGER.severe(e.getMessage());
            this.cerrarConexion();
        }
        return usuario;
    }
    /**
     * Añade un objeto usuario a la base de datos.
     * @param usuario Objeto registrado por el usuario.
     * @return Boolean a true si se ha insertado de forma correcta y false si no.
     * @throws DAOException 
     */
    @Override
    public boolean registrarUser(User usuario) throws DAOException {
        boolean registradoCorrecto = false;
        try{
            this.abrirConexion();
            String insert = "insert into user (login,email,fullname,status,"
                    + "privilege, password, lastAccess, lastPasswordChange) "
                    + "values('"+usuario.getLogin()+"','"+usuario.getEmail()+"','"
                    + usuario.getFullname()+"',0,"
                    + "1"+",'"+usuario.getPassword()+"',"
                    + "date(now())"+","
                    + "date(now())"+");";
            pstmt.executeUpdate(insert);
            this.cerrarConexion();
        }catch(Exception e){
            LOGGER.severe(e.getMessage());
            this.cerrarConexion();
        }
        return registradoCorrecto;
    }

    /**
     * Actualiza la fecha del ultimo acceso.
     * @param login login del usuario.
     * @throws DAOException 
     */
    @Override
    public void ultimoAcceso(String login) throws DAOException {
        try{
            this.abrirConexion();
            String update = "update user set lastAccess=date(now())";
            pstmt.executeUpdate(update);
            this.cerrarConexion();
        }catch(Exception e){
            LOGGER.severe(e.getMessage());
            this.cerrarConexion();
        }
    }
}