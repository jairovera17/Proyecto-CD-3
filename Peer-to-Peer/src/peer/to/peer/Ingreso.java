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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import java.util.logging.Level;
import java.util.logging.Logger;
import static peer.to.peer.PeerToPeer.disponibleCARTAS;
import static peer.to.peer.PeerToPeer.initCartas;
import static peer.to.peer.PeerToPeer.misCartas;
import static peer.to.peer.PeerToPeer.noCartas;
import static peer.to.peer.PeerToPeer.listaPalabras;
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
   private int rango;
   
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
        if(disponibleCARTAS==true){
            
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
                String asign = tokens.nextToken();
           
                for(Nodo nodo: lista){
                if(nodo.address.equals(hostAddress)){
                System.out.println(nodo.name+" desde "+hostAddress+" dice:");
               
                return asign;
              
                }     
                }   
               
                
               
               
            
                System.out.println("Alguien en la red te dice: ");
                return asign;
            }
             System.out.println("Alguien en la red les dice a todos: ");
            return tokens.nextToken(); 
        }
        
        
        if(index.equals("jugar")){
            
            for(Nodo nodo: lista){
                if(nodo.address.equals(hostAddress)){
                    System.out.println(nodo.name+" desde "+nodo.address+" entrega las siguientes cartas");
                    break;
                }
            }
            
            for(int i=0;i<lista.size()*10;i++){
                
                byte [] buf = new byte[256];
                DatagramPacket packet =  new DatagramPacket(buf,buf.length);
                try {
                    socket.receive(packet);
                } catch (IOException ex) {
                    Logger.getLogger(Ingreso.class.getName()).log(Level.SEVERE, null, ex);
                }
                String msjRecv = new String(packet.getData(), 0, packet.getLength());
                System.out.println("msj recv "+msjRecv);
                
                if(!msjRecv.equals("jugar")){
                    StringTokenizer tokensJuego =new StringTokenizer(msjRecv,"@");
                    String nombre = tokensJuego.nextToken();
                    int carta = Integer.parseInt(tokensJuego.nextToken());
                    String nombrePalabra= tokensJuego.nextToken();
                    
                     System.err.println(nombre+" nombre original " +nombre+" "+carta+" "+nombrePalabra);
            
                     if(nombre.equals(this.name)){
                         initCartas.add(carta+"");
                         this.rango=Integer.parseInt(tokensJuego.nextToken());
                     }
                }
                
                
                
            }
              
        ordenar(this.rango);
             try {
                 Thread.sleep(1000);
             } catch (InterruptedException ex) {
                 Logger.getLogger(Ingreso.class.getName()).log(Level.SEVERE, null, ex);
             }
        intercambiar();
         
        }
        
         ///////////////////////////////////////////////////////////////////////////////////
       
        if(index.equals("intercambio")){
            int carta = Integer.parseInt(tokens.nextToken());
        if((this.rango-1)*10<carta&&(this.rango)*10>=carta){
            misCartas.add(carta);
            System.out.println("Mis cartas: "+Arrays.toString(misCartas.toArray()));
            
            if(misCartas.size()==10){
                finJuego();
            }
            }
        }
           
        if(index.equals("report")){
            
            String nodo =tokens.nextToken();
          
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
       
        return null;
        
        
    
        
        
    }
    
       private void finJuego(){
           System.out.println("Fin del Juego: \n"+Arrays.toString(misCartas.toArray()));
       }
       
    private void enviarMSJ(String tag,String msj){
       
            try {
                byte [] buf = new byte[256];
                
               String reporte=tag+"@"+msj;
                buf = reporte.getBytes();
               
               DatagramPacket packet = new DatagramPacket(buf,buf.length,ipBroadcast,port);     
                socket.send(packet);
              
            } catch (IOException ex) {
                Logger.getLogger(Salida.class.getName()).log(Level.SEVERE, null, ex);
            }
        
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
    
   
    
    private void ordenar(int rango){
        
        for(String cadena:initCartas){
            int aux = Integer.parseInt(cadena);
            if(((rango-1)*10)<=aux && ((rango)*10)>=aux){
                misCartas.add(aux);
            }
            else{
                noCartas.add(aux);
            }
            
        }
           Collections.sort(misCartas);
           Collections.sort(noCartas);
      
    }
    private void intercambiar(){
        byte[] intercambio = new byte[256];
        for(Integer num : noCartas){
            String cambio = "intercambio@"+num+"@"+listaPalabras.get(num);
            intercambio = cambio.getBytes();
            DatagramPacket packet = new DatagramPacket(intercambio,intercambio.length,ipBroadcast,port);
            try {
                socket.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(Ingreso.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
