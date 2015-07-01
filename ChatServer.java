/**
 * The EasyChat package is a concise set of simple chat server and client implementations
 */
package com.ffwhite.easychat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * @author Forest White
 * ChatServer listens on port 8080 for client connections, and adds clients to a client 
 * handler thread pool, which accepts incoming messages and broadcasts them to all other 
 * clients in the client handler pool. 
 */
public class ChatServer {
    private static LinkedList<ClientHandler> clientHandlerPool;
    
    private class ClientHandler implements Runnable{
        DataInputStream input;
        DataOutputStream output;
        
        public ClientHandler(DataInputStream in, DataOutputStream out){
            this.input = in;
            this.output = out;
        }
        
        public void run() {
            while(true){
                try{
                    String message = input.readUTF();
                    //send message
                    for (int i = 0; i < clientHandlerPool.size(); i++){
                        clientHandlerPool.get(i).output.writeUTF(message);
                    }
                }catch(IOException x){
                    //TODO Either cannot read input or send output to other clients
                }
            }
        }
    }
    
    public static void main(String[] args){
        ChatServer outer = new ChatServer();
        try{
            ServerSocket server = new ServerSocket(8080);
            clientHandlerPool = new LinkedList<ClientHandler>();
            while (true){
                Socket socket = server.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                ClientHandler clientHook = outer.new ClientHandler(input, output);

                clientHandlerPool.add(clientHook);
                Thread t = new Thread(clientHook);
                t.start();
                System.out.println("Client " + clientHandlerPool.size() + " connected ...");
            }
        }catch(IOException x){
            //TODO Remove the client connection from the connection handler pool
        }
    }
}
