/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;


/**
 * La clase main del lado servidor.
 * The main class of the server side.
 * @author Ricardo Peinado Lastra
 */
public class Servidor {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("main.Servidor");
    private static int puerto = Integer.parseInt(ResourceBundle.getBundle("main.serverConf").getString("port"));
    private static int hilos = Integer.parseInt(ResourceBundle.getBundle("main.serverConf").getString("threads"));
    private static int contador=0;
    
    public static void main(String[] args){
        ServerSocket serverSocket=null;
        try{
            serverSocket = new ServerSocket(puerto);
            while(true){
                LOGGER.info("HILOS ACTUALES "+contador);
                //Version 1
                Socket socket = serverSocket.accept();
                if(contador<=hilos){
                    contador++;
                    SocketHilo socketHilo=new SocketHilo(socket,true);
                    socketHilo.start();
                }else{
                    SocketHilo socketHilo=new SocketHilo(socket,false);
                    socketHilo.start();
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
}
