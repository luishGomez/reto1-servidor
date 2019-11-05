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
 * This class is the implementation of the DAO interface, which allows 
 * connection to our database.
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
    
    public DAOImplementation(ConexionPool poolBD) {
        
        this.poolBD = poolBD;
        
    }
    
    /**
     * Comprueba que el login no exista ya en la base de datos.
     * Check that the login no longer exists in the database.
     * @param login login del usuario / The login of the user.
     * @return Boolean a true si ya existe ese login en la base de datos. / 
     * Boolean true if exists the login in the data base.
     * @throws DAOException Si ocurre algun error con la base de datos. / If
     * occurs a error with the data base.
     */
    @Override
    public boolean verificarLogin(String login) throws DAOException {
        boolean loginExiste = false;
        try{
            this.con=this.poolBD.extraerConexion();
            this.con.setAutoCommit(true);
            String select = "select * from user where login='" + login+"'";
            pstmt = this.con.prepareStatement(select);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                loginExiste = true;
            }
            rs.close();
        }catch (Exception e){
            LOGGER.severe(e.getMessage());
            throw new DAOException();
        }finally{
            this.poolBD.liberarConexion(this.con); 
        }
        return loginExiste;
    }
    /**
     * Comprueba que el login y la contraseña existen y son correctos.
     * Check that the login and password exist and are correct.
     * @param login login del usuario. / The login of the user.
     * @param password contraseña del usuario. / The password of the user.
     * @return Objeto User con toda la información del usuario. / The User 
     * object with the all information.
     * @throws DAOException Si ocurre algun error con la base de datos. / If
     * occurs a error with the data base.
     */
    @Override
    public User verificarLoginPassword(String login, String password)
            throws DAOException {
        boolean loginCorrecto = false;
        User usuario = null;
        try{
            this.con=this.poolBD.extraerConexion();
            this.con.setAutoCommit(true);
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
        }catch (Exception e){
            LOGGER.severe(e.getMessage());
            throw new DAOException();
        }finally{
            this.poolBD.liberarConexion(this.con); 
        }
        return usuario;
    }
    /**
     * Añade un objeto usuario a la base de datos. 
     * Adds a user object in the data base.
     * @param usuario Objeto registrado por el usuario. / Object registered by 
     * the user.
     * @return Boolean a true si se ha insertado de forma correcta y false si no.
     * Boolean with true if has been inserted correctly or false if hasn't.
     * @throws DAOException Si ocurre algun error con la base de datos. / If
     * occurs a error with the data base.
     */
    @Override
    public boolean registrarUser(User usuario) throws DAOException {
        boolean registradoCorrecto = false;
        try{
            this.con=this.poolBD.extraerConexion();
            this.con.setAutoCommit(true);
            String insert = "insert into user (login,email,fullname,status,"
                    + "privilege, password, lastAccess, lastPasswordChange) "
                    + "values('"+usuario.getLogin()+"','"+usuario.getEmail()+"','"
                    + usuario.getFullname()+"',0,"
                    + "1"+",'"+usuario.getPassword()+"',"
                    + "date(now())"+","
                    + "date(now())"+");";
            pstmt.executeUpdate(insert);
        }catch(Exception e){
            LOGGER.severe(e.getMessage());
            throw new DAOException();
        }finally{
            this.poolBD.liberarConexion(this.con); 
        }
        return registradoCorrecto;
    }
    
    /**
     * Actualiza la fecha del ultimo acceso.
     * Update the date of the last access.
     * @param login login del usuario. / The User login.
     * @throws DAOException Si ocurre algun error con la base de datos. / If
     * occurs a error with the data base.
     */
    @Override
    public void ultimoAcceso(String login) throws DAOException {
        try{
            this.con=this.poolBD.extraerConexion();
            this.con.setAutoCommit(true);
            String update = "update user set lastAccess=date(now())";
            pstmt.executeUpdate(update);
        }catch(Exception e){
            LOGGER.severe(e.getMessage());
            throw new DAOException();
        }finally{
            this.poolBD.liberarConexion(this.con); 
        }
    }
}