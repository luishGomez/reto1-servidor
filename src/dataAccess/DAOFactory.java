
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
    public synchronized static DAOFactory getDao(){
        return new DAOImplementation();
    }
}
