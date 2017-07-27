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
import java.util.Random;

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
    ArrayList<Integer> globalRandom;
    
    public Salida(InetAddress ipBroadcast,int port,String name,ArrayList<Nodo> lista){
        this.name=name;
        this.ipBroadcast=ipBroadcast;
        this.port=port;
        this.lista=lista;
        globalRandom = new ArrayList<>();
        try {
            this.socket=new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(Salida.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
    }
    @Override
    public void run() {
       verDisponibles();
     enviar();
    }
    
    private void verDisponibles(){
        double start = System.currentTimeMillis();
        double end=start;
        
        
        do{
        try {
            byte [] buf = new byte[256];
          
             
            String msj = "--listar";
           
            buf=msj.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,ipBroadcast,port);
            socket.send(packet);
            
            
              
             end=System.currentTimeMillis();
            
          
            
            
        } catch (IOException ex) {
            Logger.getLogger(Salida.class.getName()).log(Level.SEVERE, null, ex);
        }   
        
        }while((end-start)<5000);
         System.out.println("Usuarios en la red para iniciar juego incluyendome: "+lista.size());
         for(Nodo nodo: lista){
                        System.out.println("Usuario Connectado: "+nodo.name+"\t"+nodo.address);
                    }
             if(lista.get(0).name.equals(name)){
                 System.out.println("YO soy dealer\n Empiezo a repartir");
                 globalRandom.clear();
                 int range=1;
                for(Nodo nodo : lista){
                    System.out.println("Enviando a "+nodo.address);
                     ArrayList<Integer> misRandom = getRandom(10,lista.size()*10);
                     enviarMSJ(nodo.name+"@inicio"+"@"+(range*10), arrayToString(misRandom));
                   range++;
                }
                
                 
             }
        
        
        
    }
    
    private ArrayList<Integer> getRandom(int len,int size){
        ArrayList<Integer> randomList = new ArrayList<>();
        Random random = new Random();
        while(true){
            int temp = random.nextInt(size)+1;
             if(randomList.size()==10){
                 break;
             }
            if(!(randomList.contains(temp)||globalRandom.contains(temp))){
                randomList.add(temp);
                globalRandom.add(temp);
            }
           
        }
        return randomList;
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
    
    private String arrayToString(ArrayList<Integer> num){
        String salida="";
        for(int i=0;i<num.size()-1;i++){
            salida+=num.get(i)+"|";
        }
        salida+=num.get(num.size()-1);
        return salida;
        
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
    
   
    
}
