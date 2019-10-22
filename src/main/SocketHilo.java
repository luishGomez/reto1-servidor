/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package main;

import clases.Mensaje;
import clases.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Es la clase que tiene el hilo que maneja el lado servidor de una consulta de
 * un cliente.
 * Is the server side class with the thread to manage the consult from the user
 * to the server.
 * @author Ricardo Peinado Lastra
 */
public class SocketHilo extends Thread{
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("main.Servidor");
    private Socket miSocket;
    private boolean libre;
    public SocketHilo(Socket miSocket,boolean libre){
        this.miSocket = miSocket;
        this.libre = libre;
    }
    public void run(){
        ObjectInputStream flujo_entrada = null;
        ObjectOutputStream flujo_salida = null;
        try{
            flujo_entrada=new ObjectInputStream(miSocket.getInputStream());
            flujo_salida= new ObjectOutputStream(miSocket.getOutputStream());
            
            //Empezamos a interpretar
            if(libre){
                try{
                    Mensaje mensajeInicio = (Mensaje) flujo_entrada.readObject();
                    switch(mensajeInicio.getOpc()){
                        case 1:
                            String login= ((User)mensajeInicio.getData()).getLogin();
                            Mensaje mensajeFin=null;
                            //if(dao.verificaLogin(login)){
                            if(true){
                                //DAO.REGISTRAR
                                mensajeFin= new Mensaje(1,true);
                            }else{
                                mensajeFin= new Mensaje(-2,"Ya existe alguien con ese ID login.");
                            }
                            flujo_salida.writeObject(mensajeFin);
                            break;
                        case 2:
                            
                    }
                }catch(ClassNotFoundException e){
                    LOGGER.severe(e.getMessage());
                }
            }else{
                try{
                    Mensaje peticion =(Mensaje) flujo_entrada.readObject();
                }catch(ClassNotFoundException e){
                    LOGGER.severe(e.getMessage());
                }
                Mensaje mensaje = new Mensaje(-1,"Estamos ocupados");
                flujo_salida.writeObject(mensaje);
            }
            
            
            flujo_salida.close();
            flujo_entrada.close();
            miSocket.close();
        }catch(IOException e){
            LOGGER.severe(e.getMessage());
        }finally{
            try{
                if(flujo_salida!=null)
                    flujo_salida.close();
                if(flujo_entrada!=null)
                    flujo_entrada.close();
                if(miSocket!=null)
                    miSocket.close();
            }catch(IOException e){
                LOGGER.severe(e.getMessage());
            }
        }
    }
}
