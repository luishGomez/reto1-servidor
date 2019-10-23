/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Stack;
import java.util.ResourceBundle;
import java.util.logging.Logger;


/**
 * Conexion con la base de datos.
 * @author sergio.
 */

public class ConexionPool {
    private static final Logger LOGGER = Logger.getLogger("dataAccess.ConexionPool");
    private   static String config="dataAccess.dbConf";
    private   static ResourceBundle configFile;
    protected static Stack pool;
    protected static String connectionURL;
    protected static String userName;
    protected static String password;
    
    /**
     *inicializa los valores de conexion a la base a partir del config.
     */
    public ConexionPool(){
        configFile=ResourceBundle.getBundle(config);
        connectionURL = configFile.getString("Conn");
        userName = configFile.getString("DBUser");
        password = configFile.getString("DBPass");
        pool = new Stack();
    }
    /**
     * crea una conexin a la base de datos.
     * @return una conexion nueva o una ya abierta.
     * @throws SQLException .
     */
    public synchronized static Connection extraerConexion() throws SQLException{ 
        LOGGER.info("Creando conexion a la base de datos.");
        if(!pool.empty()) { 
            return (Connection) pool.pop();
        } 
        else {  
            return DriverManager.getConnection(connectionURL, userName, password);
        } 
    }
    /**
     * liberamos la conexion a la base de datos.
     * @param conn la conexion que se quiere cerrar.
     * @throws SQLException .
     */
     public synchronized static void liberarConexion(Connection conn)throws SQLException{
         LOGGER.info("Liberando conexion a la base de datos.");
	 pool.push(conn);
    }
    
}
