/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessutil;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author John
 */
public class Server
{

    public static int PORT_NUMBER = 12345;

    private ServerSocket serveSock;

    private void listenToSocket(Socket s)
    {
        System.out.println("Listening to socket");
        try
        {
            InputStream is = s.getInputStream();
            while (is.available() > 0)
            {
                System.out.print((char) is.read());
            }
            System.out.println("");
        } catch (Exception e)
        {
            System.out.println("Error in Socket: " + s);
        }
    }

    public Server()
    {
        System.out.println("Starting Server");
        try
        {
            serveSock = new ServerSocket(PORT_NUMBER);
            while (true)
            {
                Socket sock = serveSock.accept();
                listenToSocket(sock);
            }
        } catch (Exception e)
        {
            System.out.println("Error in Server Initialization");
        }
    }

    public static void main(String args[])
    {
        new Thread(new Client()).start();
        Server s = new Server();
    }
}
