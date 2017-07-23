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
    public Ingreso(InetAddress ipBroadcast,int port,String name){
        this.ipBroadcast = ipBroadcast;
        this.name=name;
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
               
                System.out.println(received);
            } catch (IOException ex) {
                Logger.getLogger(Ingreso.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
}
