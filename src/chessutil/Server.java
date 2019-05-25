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

    public static int PORT_NUMBER = 52645;

    private ServerSocket serveSock;

    private void listenToSocket(Socket s)
    {
        while (true)
        {
            try
            {
                InputStream is = s.getInputStream();
                while (is.available() > 0)
                {
                    System.out.println((char) is.read());
                }
            } catch (Exception e)
            {
                System.out.println("Error in Socket: " + e);
            }
        }
    }

    public Server()
    {
        System.out.println("Starting Server");
        boolean success = false;
        Socket sock = null;
        try
        {
            serveSock = new ServerSocket(PORT_NUMBER);
            System.out.println("Server port is: " + serveSock.getLocalPort());
            while (!success)
            {
                sock = serveSock.accept();
                success = true;
            }
        } catch (Exception e)
        {
            System.out.println("Error in Server Initialization");
        }
        if (sock == null)
        {
            return;
        }
        System.out.println("Listening to socket");
        listenToSocket(sock);
    }

    public static void main(String args[])
    {
        Server s = new Server();
    }
}
