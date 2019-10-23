
package dataAccess;

import java.util.logging.Logger;

/**
 *  
 * @author sergio.
 */
public class DAOFactory {
    private static final Logger LOGGER = Logger.getLogger("dataAccess.daoFactory");
    /**
     * Crea un dao y lo devuelve.
     * @return un DAO.
     */
    public synchronized static DAO getDao(){
        LOGGER.info("Retornando un DAO.");
        return new DAOImplementation(new ConexionPool(),"dataAccess.dbConf");
    }
}
