/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package main;

import clases.Mensaje;
import clases.User;
import dataAccess.DAO;
import dataAccess.DAOException;
import dataAccess.DAOFactory;
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
    private DAO dao=DAOFactory.getDao();
    /**
     * El constructor del Socket Hilo.
     * The constructor of the Socket Thread.
     * @param miSocket El socket creado / The previous created socket.
     * @param libre Un booleano que expresa si esta libre el servidor o no /A
     * boolean who expresses whether the server is free or not.
     */
    public SocketHilo(Socket miSocket,boolean libre){
        this.miSocket = miSocket;
        this.libre = libre;
    }
    /**
     * Este metodo es el inicio de la ejecución del hilo.
     * This method is the start of thread execution.
     */
    public void run(){
        ObjectInputStream flujo_entrada = null;
        ObjectOutputStream flujo_salida = null;
        try{
            //Iniciamos los flujos.
            flujo_entrada=new ObjectInputStream(miSocket.getInputStream());
            flujo_salida= new ObjectOutputStream(miSocket.getOutputStream());
            
            //Si esta libre el servidor tratamos al cliente.
            if(libre){
                String login = null;
                Mensaje mensajeFin=null;
                try{
                    //Tratamos el mensaje de petición del cliente.
                    LOGGER.info("Vamos a coger el mensaje");
                    Mensaje mensajeInicio = (Mensaje) flujo_entrada.readObject();
                    LOGGER.info("Vamos a intrepetar el mensaje");
                    switch(mensajeInicio.getOpc()){
                        case 1:
                            LOGGER.info("Iniciamos el REGRISTRO.");
                            
                            login= ((User)mensajeInicio.getData()).getLogin();
                            if(!dao.verificarLogin(login)){
                                dao.registrarUser((User)mensajeInicio.getData());
                                mensajeFin= new Mensaje(1,(Boolean)true);
                            }else{
                                mensajeFin= new Mensaje(-2,"Ya existe alguien con ese ID login.");
                            }
                            flujo_salida.writeObject(mensajeFin);
                            
                            //Servidor.liberarHilo();
                            LOGGER.info("REGRISTRO terminado.");
                            break;
                        case 2:
                            LOGGER.info("Iniciamos el INICIO SESIÓN.");
                            
                            login= ((User)mensajeInicio.getData()).getLogin();
                            String password =((User)mensajeInicio.getData()).getPassword();
                            //DAO.Verificar
                            User user=dao.verificarLoginPassword(login, password);
                            if(user!=null){
                                dao.ultimoAcceso(login);
                                LOGGER.info("Ultimo acceso actualizado.");
                                mensajeFin=new Mensaje(2,user);
                            }else{
                                if(dao.verificarLogin(login)){
                                    mensajeFin=new Mensaje(-4,"La constraseña no es correcta");
                                }else{
                                    mensajeFin=new Mensaje(-3,"El id de login no existe");
                                }
                            }
                            flujo_salida.writeObject(mensajeFin);
                            
                            
                            //Servidor.liberarHilo();
                            LOGGER.info("INICIO SESIÓN terminado.");
                            break;
                            
                    }
                }catch(ClassNotFoundException e){
                    LOGGER.severe("ClassNotFoundException "+e.getMessage());
                    //Enviamos una respuesta del error
                    mensajeFin=new Mensaje(-6,"Error al interpretar el mensaje");
                    flujo_salida.writeObject(mensajeFin);
                } catch (DAOException e) {
                    LOGGER.severe("DAOException "+e.getMessage());
                    //Enviamos una respuesta del error
                    mensajeFin=new Mensaje(-5,"Fallo de la base de datos");
                    flujo_salida.writeObject(mensajeFin);
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
                Servidor.liberarHilo();
            }catch(IOException e){
                LOGGER.severe("NO SE HAN CERRADO BIEN LOS FLUJOS/SOCKET ||"+e.getMessage());
            }
        }
    }
}
