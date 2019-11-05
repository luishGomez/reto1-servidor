package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;


/**
 * La clase main del lado servidor que gestiona las peticiones de los clientes.
 * The main class of the server side that handles client requests.
 * @author Ricardo Peinado Lastra
 */
public class Servidor {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("main.Servidor");
    private static final int puerto = Integer.parseInt(ResourceBundle.getBundle("main.serverConf").getString("port"));
    private static final int hilos = Integer.parseInt(ResourceBundle.getBundle("main.serverConf").getString("threads"));
    private static int contador=0;
    
    public static void main(String[] args){
        ServerSocket serverSocket=null;
        try{
            serverSocket = new ServerSocket(puerto);
            LOGGER.info("SERVER ACTIVO!!!");
            while(true){
                
                try{
                    Socket socket = serverSocket.accept();
                    if(acceso()){
                        incrementarHilo();
                        SocketHilo socketHilo=new SocketHilo(socket,true);
                        socketHilo.start();
                    }else{
                        SocketHilo socketHilo=new SocketHilo(socket,false);
                        socketHilo.start();
                    }
                }catch(IOException e){
                    LOGGER.severe("Petición faillda "+e.getMessage());
                }
                
            }
        }catch(IOException e){
            LOGGER.severe(e.getMessage());
        }
    }
    /**
     * Disminuye el contador de hilos del servidor.
     * Decrease the thread count of the server.
     */
    synchronized static public void liberarHilo(){
        contador--;
        LOGGER.info("HILOS ACTUALES "+contador);
    }
    /**
     * Incrementa el contador de hilos del servidor.
     * Increase the thread count of the server.
     */
    synchronized static public void incrementarHilo(){
        contador++;
        LOGGER.info("HILOS ACTUALES "+contador);
    }
    /**
     * Compara el contador de clientes actuales con los hilos máximos que pueden
     * haber.
     * Compare the current client counter with the maximum threads that may exist.
     * @return True si hay menos clientes que el máximo de hilos permitidos | 
     * False en cualquier otro caso / True if there are fewer customers than the 
     * maximum allowed threads | False in any other case
     */
    synchronized static public boolean acceso(){
        boolean resu=false;
        if(contador<hilos){
            resu=true;
        }
        return resu;
    }
}
