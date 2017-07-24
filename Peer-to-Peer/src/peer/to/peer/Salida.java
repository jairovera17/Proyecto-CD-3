/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer.to.peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author jairo
 */
public class Salida implements Runnable {

    String name;
    InetAddress ipBroadcast;
    DatagramSocket socket;
    int port;
    ArrayList<Nodo> lista;
    
    public Salida(InetAddress ipBroadcast,int port,String name,ArrayList<Nodo> lista){
        this.name=name;
        this.ipBroadcast=ipBroadcast;
        this.port=port;
        this.lista=lista;
        try {
            this.socket=new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(Salida.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run() {
     enviar();
    }
    
    private void enviar(){
           
        byte [] buf = new byte[256];
            
        while(true){
            try {
             
                
            
                String temp = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                
                temp = in.readLine();
                if(temp.equals("--listar")){
                    lista.clear(); 
                   
                }
                   
                
                
                buf = temp.getBytes();
               
               DatagramPacket packet = new DatagramPacket(buf,buf.length,ipBroadcast,port);     
                socket.send(packet);
                 if(temp.equals("--listar")){
                    Thread.sleep(1000);
                    for(Nodo nodo: lista){
                        System.out.println("Usuario Connectado: "+nodo.name+"\t"+nodo.address);
                    }
                   
                }
              
            } catch (IOException ex) {
                Logger.getLogger(Salida.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Salida.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
