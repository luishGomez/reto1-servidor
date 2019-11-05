package dataAccess;

/**
 *  
 * @author sergio
 */
public class DAOFactory {
    static ConexionPool pool=new ConexionPool();
    /**
     * 
     * @return un DAO
     */
    public synchronized static DAO getDao(){
        return new DAOImplementation(pool);
    }
    
}
