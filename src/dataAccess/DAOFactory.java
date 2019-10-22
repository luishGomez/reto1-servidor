
package dataAccess;

/**
 *  
 * @author sergio
 */
public class DAOFactory {
    /**
     * 
     * @return un DAO
     */
    public synchronized static DAO getDao(){
        return new DAOImplementation(new ConexionPool(),"dataAccess.dbConf");
    }
}
