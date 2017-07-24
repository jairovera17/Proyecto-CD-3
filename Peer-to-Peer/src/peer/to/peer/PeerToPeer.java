/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer.to.peer;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jairo
 */
public class PeerToPeer {

    static String name;
    static InetAddress ipBroadcast;
    static String interfaz;
    static int portEscucha;
    static int portEnvio;
    static ArrayList<Nodo> lista;
    public static void main(String[] args) throws UnknownHostException {
        
        if(args.length<4){
            System.out.println("Faltan Datos");
            System.exit(0);
        }
        lista = new ArrayList<>();
        interfaz=args[0];
        name=args[1];
        System.out.println(name);
        portEscucha=Integer.parseInt(args[2]);
        portEnvio=Integer.parseInt(args[3]);
        
                        
        empezarP2P();
        
          
    }
    
    static void empezarP2P(){
        ipBroadcast= setBroadcastip();
        System.out.println("ipBroadCast\t"+ipBroadcast.getHostAddress());
        if(ipBroadcast == null){
            System.out.println("No se pudo obtener la ipBroadCast de la red");
            System.exit(0);
        }
        
        ExecutorService ex = Executors.newCachedThreadPool();
     
        ex.submit(new Salida(ipBroadcast, portEnvio, name,lista));
        ex.submit(new Ingreso(ipBroadcast, portEscucha,name,lista));   
        
        
       
    }
    

    private static InetAddress setBroadcastip(){
        try {
            
            NetworkInterface network = NetworkInterface.getByName(interfaz);
            for(InterfaceAddress temp : network.getInterfaceAddresses()){
                InetAddress add = temp.getBroadcast();
                if(add ==null)
                    continue;
                else{
                   
                    return add;
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(PeerToPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
