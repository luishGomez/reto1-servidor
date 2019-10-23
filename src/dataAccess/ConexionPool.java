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

//import java.util.Stack;

/**
 * Conexion con la base de datos
 * @author sergio
 */
import java.util.ResourceBundle;
//import java.util.logging.Logger;
public class ConexionPool {
    private   static String config="dataAccess.dbConf";
    private   static ResourceBundle configFile;
    protected static Stack pool;
    protected static String connectionURL;
    protected static String userName;
    protected static String password;
    
    /**
     *inicializa los valores de conexion a la base a partir del config
     */
    public ConexionPool(){
        configFile=ResourceBundle.getBundle(config);
        connectionURL = configFile.getString("Conn");
        userName = configFile.getString("DBUser");
        password = configFile.getString("DBPass");
        pool = new Stack();
    }
    /**
     * crea una conexin a la base de datos
     * @return una conexion nueva o una ya abierta
     * @throws SQLException 
     */
    public synchronized  Connection extraerConexion() throws SQLException{   
	   if(!pool.empty()) { 
               return (Connection) pool.pop();
           } 
           else {  
               return DriverManager.getConnection(connectionURL, userName, password);
	   } 
    }
    /**
     * liberamos la conexion a la base de datos
     * @param conn la conexion que se quiere cerrar
     * @throws SQLException
     */
     public synchronized  void liberarConexion(Connection conn){ 
         //throws SQLException
	    pool.push(conn);
    }
    
}
