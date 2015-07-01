/**
 * The EasyChat package is a concise set of simple chat server and client implementations
 */
package com.ffwhite.easychat;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Forest White
 * This is a refactored Chat Client built on the base class GUIChat. As such, it has the 
 * GUIChat UI which can send and recieve messages with a connected server.
 */
public class GUIChatClient extends GUIChat {
    
    /*
     * Initiates contact with the server for connection/IO
     * @returns Socket the socket connection made with the server
     */
    public Socket setupConnection() throws IOException{
        chatLogTA.append("Attempting to contact server on port 8080 ... \n");
        Socket socket = new Socket("localhost", 8080);
        chatLogTA.append("Server connected! \n");
        return socket;
    }
}
