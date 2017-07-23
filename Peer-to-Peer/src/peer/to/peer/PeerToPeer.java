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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jairo
 */
public class PeerToPeer {

    static String name="Jairox";
    static InetAddress ipBroadcast;
    public static void main(String[] args) throws UnknownHostException {
        
        
      
        empezarP2P();
        
          
    }
    
    static void empezarP2P(){
        ipBroadcast= setBroadcastip();
        if(ipBroadcast == null){
            System.out.println("No se pudo obtener la ipBroadCast de la red");
            System.exit(0);
        }
        
        ExecutorService ex = Executors.newCachedThreadPool();
     
        ex.submit(new Salida(ipBroadcast, 6000, name));
        ex.submit(new Ingreso(ipBroadcast, 5000,name));   
        
        
       
    }
    

    private static InetAddress setBroadcastip(){
        try {
            //wlp8s0
            NetworkInterface network = NetworkInterface.getByName("wlp8s0");
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
