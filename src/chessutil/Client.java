/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessutil;

import java.net.Socket;
import java.io.PrintStream;
import java.net.InetAddress;

/**
 *
 * @author John
 */
public class Client implements Runnable
{

    @Override
    public void run()
    {
        System.out.println("Starting Client");
        boolean success = false;
        Socket sock = null;
        while (!success)
        {
            try
            {
                InetAddress ia = InetAddress.getByName("192.168.0.100");
                sock = new Socket(ia, Server.PORT_NUMBER, true);
                success = true;
            } catch (Exception e)
            {
                System.out.println("Client error: " + e);
            }
        }
        while (true)
        {
            try
            {
                PrintStream ps = new PrintStream(sock.getOutputStream());
                ps.print("Hi There");
                ps.flush();
            } catch (Exception e)
            {
            }
        }
    }

    public static void main(String args[])
    {
        Client c = new Client();
        new Thread(c).start();
    }
}
