package dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Stack;
import java.util.ResourceBundle;

/**
 * Conexión con la base de datos.
 * Connection to the database.
 * @author sergio
 */
public class ConexionPool {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("dataAccess.ConexionPool");
    private static String config="dataAccess.dbConf";
    private static ResourceBundle configFile;
    private static Stack pool;
    private static String connectionURL;
    private static String userName;
    private static String password;
    
    /**
     * Inicializa los valores de conexion a la base de datos a partir del config.
     * Initializes the connection values to the base data from the config.
     */
    public ConexionPool(){
        configFile=ResourceBundle.getBundle(config);
        connectionURL = configFile.getString("Conn");
        userName = configFile.getString("DBUser");
        password = configFile.getString("DBPass");
        pool = new Stack();
    }
    /**
     * Crea una conexión a la base de datos.
     * Creates a connection to the database.
     * @return una conexion nueva o una ya abierta / a new connection or one
     * already opened
     * @throws SQLException Si ocurre algun error al conectarse / If occurs error
     * when it try connecting.
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
     * Guarda las conexiones.
     * Pick up the connection.
     * @param conn La conexión a cerrar / The connection to save.
     */
    public synchronized  void liberarConexion(Connection conn){
        pool.push(conn);
    }
    
    
}
