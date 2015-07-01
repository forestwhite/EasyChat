/**
 * The EasyChat package is a concise set of simple chat server and client implementations
 */
package com.ffwhite.easychat;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;


/**
 * @author Forest White
 * This is the base class for GUIChat clients. It includes a simple Applet user interface 
 * that includes:
 * 1. a required name, which is added to all outgoing messages
 * 2. a text field that sends messages when the user types Enter in the message text field
 * 3. a text field for a Onetime Pad encryption key, which must also be entered in the 
 *    recipients key field to decrypt any message sent
 *  note: the cypher text for a message appears below the key field for the user's
 *        reference when sending an encoded message
 */
public abstract class GUIChat extends Applet implements ActionListener, Runnable {

    TextArea chatLogTA = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
    TextField messageTF = new TextField(); // field containing the message to send
    TextField nameTF = new TextField(); // test field for sender's name
    TextField keyTF = new TextField(); // text field for Onetime Pad key
    Label cypherLB = new Label("Cypher text: "); // Cypher Text display
    DataInputStream input;
    DataOutputStream output;
    String key;
    String name;

    /*
     * Abstract method intended to be implemented for a specific GUIChat 
     * network connection
     * @return socket the TCP socket to which the GUIchat connects. This socket 
     * is referenced by the actionListener that sends messages across the DataOutputStream
     * output and the thread that listens for messages on the DataInputStream input.
     */
    public abstract Socket setupConnection() throws IOException;

    /*
     * Initializes the chat window, adds an action listener to the message text field
     * to send messages, and starts a thread that handles incoming messages.
     */
    @Override
    public void init(){
        setLayout(null);    //no layout manager
        
        Label messageLB = new Label("Message:");
        Label nameLB = new Label("Name:");
        Label keyLB = new Label("Encryption Key:");
        
        chatLogTA.setBounds(10,10, 400, 400);
        messageLB.setBounds(10, 420, 100, 15);
        nameLB.setBounds(320, 420, 50, 15);
        messageTF.setBounds(10, 435, 300, 20);
        nameTF.setBounds(320, 435, 80, 20);
        keyLB.setBounds(10, 455, 100, 20);
        keyTF.setBounds(110, 455, 290, 20);
        cypherLB.setBounds(10, 475, 400, 20);
        
        add(chatLogTA);
        add(messageLB);
        add(nameLB);
        add(messageTF);
        add(nameTF);
        add(keyLB);
        add(keyTF);
        add(cypherLB);
        Thread t = new Thread(this);
        t.start();        
        messageTF.addActionListener(this);
    }
    
    /* 
     * Action listener that responds to user pressing enter in the message text field
     * by sending a message through the DataOutputStream output. Also copies message to the 
     * chat log in the display.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent arg0) {
        name = nameTF.getText();
        if (name.isEmpty()){
            chatLogTA.append("You must enter a name to send a message!\n");
        }
        else{
            String message = name + " says: " + messageTF.getText();
            //Server retransmits all messages, no need to echo on local client: chatLogTA.append(message + "\n");
            String cypherText = encrypt(message);
            cypherLB.setText("Cypher text: " + cypherText);
            try {
                output.writeUTF(cypherText);
            } catch (IOException e){
                chatLogTA.append("Unable to send message\n");
                //TODO Gracefully shut down the chat window
            }
            messageTF.setText("");
        }
    }
    
    /* 
     * Encryption/Decryption class that uses a OneTime Pad key from the Encryption key field
     * @param message the message to encrypt/decrypt
     * @return the encrypted/decrypted message
     */
    private String encrypt(String input) {
        String result = null;
        StringBuilder sb = new StringBuilder(); // make a string from encrypted/decrypted characters
        key = keyTF.getText();
        if (key.isEmpty()){
            return input; //no encryption key
        }
        else {
            if (key.length() < input.length()){
                return input; // key not long enough
            }
            else {
                for(int i = 0; i < input.length(); i++){
                    sb.append((char)(input.charAt(i) ^ key.charAt(i)));
                }
                result = sb.toString();
                System.out.println(result);
                return result;
            }
        }
    }

    /*
     * Run method that sets up the connection, input and output streams, and waits 
     * for input on the DataInputStream input. 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try{
            Socket socket = setupConnection();
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            chatLogTA.append("Type your name before sending a message.\n");
            while(true){
            	String cypherText = input.readUTF();
                String incomingMessage = encrypt(cypherText);
                System.out.println(incomingMessage);
                chatLogTA.append(incomingMessage + "\n");
            }
        }
        catch(IOException x){
            chatLogTA.append("Server socket unavailable\n");
            //TODO Gracefully shut down the chat window and close connection
        }
    }
}
