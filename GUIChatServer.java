/**
 * The EasyChat package is a concise set of simple chat server and client implementations
 */
package com.ffwhite.easychat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Forest White
 * This is a refactored Chat Server built on the base class GUIChat. As such, it has the 
 * GUIChat UI which can send and recieve messages with a connected client, but acts only
 * as a single peer-to-peer endpoint once connected. For a true server that handles 
 * multiple client connections, use the ChatServer class in this package.
 */
public class GUIChatServer extends GUIChat {

    /*
     * Starts a simple server that prepares and blocks until it recieves a connection
     * from a client on the server socket, after which peer-to-peer communication
     * commences.
     * @returns Socket the socket connection created and connected with the client
     */
    public Socket setupConnection() throws IOException{
        chatLogTA.append("Waiting for client connection on port 8080 ... \n");
        ServerSocket server = new ServerSocket(8080);
        Socket socket = server.accept();
        chatLogTA.append("Client connected! \n");
        return socket;
    }
}
