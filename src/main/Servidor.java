/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import clases.Mensaje;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
               //Version 1
               Socket socket = serverSocket.accept();
               if(contador<=hilos){
               
               }else{
                   ObjectInputStream flujoEntrada = new ObjectInputStream(socket.getInputStream());
                   ObjectOutputStream flujoSalida= new ObjectOutputStream(socket.getOutputStream());
                   Object objeto = flujoEntrada.readObject();
                   Mensaje mensaje = new Mensaje(-1,"Estamos ocupados");
                   flujoSalida.writeObject(mensaje);
                   flujoEntrada.close();
                   flujoSalida.close();
                   socket.close();
               }
           }
        }catch(IOException e){
            LOGGER.severe(e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.severe(e.getMessage());
        }
    }
}
