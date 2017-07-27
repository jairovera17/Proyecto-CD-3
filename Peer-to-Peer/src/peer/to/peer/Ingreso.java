/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer.to.peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jairo
 */
public class Ingreso implements Runnable{

    private InetAddress ipBroadcast;
    private DatagramSocket socket;
    private String name;
    private int port;
    private ArrayList<Nodo> lista;
    
   
   
    public Ingreso(InetAddress ipBroadcast,int port,String name,ArrayList<Nodo> lista){
        this.ipBroadcast = ipBroadcast;
        this.name=name;
        this.port=port;
        this.lista=lista;
       
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException ex) {
            Logger.getLogger(Ingreso.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    @Override
    public void run() {
     escuchar();
    }
    
    private void escuchar(){
        byte buf [] = new byte[256];
        while(true){
            
            try {
               
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                
                String received = new String(packet.getData(), 0, packet.getLength());
              
                String parser=parsear(received,packet.getAddress().getHostAddress());
               if(parser!=null){
                System.out.println(parser);   
               }
                
            } catch (IOException ex) {
                Logger.getLogger(Ingreso.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private String parsear(String msj, String hostAddress){
       
         if(msj.equals("--listar")){
            enviarNombre();
        return null;
        }
        StringTokenizer tokens = new StringTokenizer(msj,"@");
        String index=tokens.nextToken();
        if(index.equals(name)||index.equals("global")){
            if(index.equals("global")){
            for(Nodo nodo: lista){
                if(nodo.address.equals(hostAddress)){
                System.out.println(nodo.name+" desde "+hostAddress+" les dice a todos:");
              
                break;
                
                }
               
            }     
            }
              
            if(index.equals(name)){
                for(Nodo nodo: lista){
                if(nodo.address.equals(hostAddress)){
                System.out.println(nodo.name+" desde "+hostAddress+" dice:");
              
                break;
                }
               
            }     
            }
            return tokens.nextToken(); 
        }
           
        if(index.equals("report")){
            String nodo =tokens.nextToken();
            
            /*for(Nodo aux:lista){
                System.out.println(aux.address+"\t"+hostAddress);
                if(aux.address.equals(hostAddress)){
                    System.out.println("Repetido");
                
                    return null;}
            }*/
            Nodo nuevoNodo = new Nodo();
            nuevoNodo.name=nodo;
            nuevoNodo.address=hostAddress;
            boolean nuevo = true;
            for(Nodo aux:lista){
                if(aux.address.equals(nuevoNodo.address)){
                    nuevo=false;
                    break;    
                }
                    
                
            }    
            if(nuevo)
            {
            lista.add(nuevoNodo);
            Collections.sort(lista,new Comparator<Nodo>() {
         @Override
        public int compare(Nodo s1, Nodo s2) {
                return s1.name.compareToIgnoreCase(s2.name);
        }
    });
            }
            
                
            //    System.out.println("Usuario Connectado: "+nodo+"\t"+hostAddress);
                return null;
            }
                  
        return null;
        
        
        
    }
    private void enviarNombre(){
        
            try {
                byte [] buf = new byte[256];
                
               String reporte="report@"+name;
                buf = reporte.getBytes();
               
               DatagramPacket packet = new DatagramPacket(buf,buf.length,ipBroadcast,port);     
                socket.send(packet);
              
            } catch (IOException ex) {
                Logger.getLogger(Salida.class.getName()).log(Level.SEVERE, null, ex);
            }
      
    }
    
    
}
