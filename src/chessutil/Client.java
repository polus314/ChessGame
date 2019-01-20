/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessutil;

import java.net.Socket;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author John
 */
public class Client implements Runnable
{

    private static int serialNumber = 0;

    @Override
    public void run()
    {
        System.out.println("Starting Client");
        while (true)
        {
            try
            {
                Socket sock = new Socket("localhost", Server.PORT_NUMBER, true);
                OutputStream stream = sock.getOutputStream();
                PrintStream ps = new PrintStream(stream);
                ps.print(++serialNumber + ": Hello World");
                ps.flush();
                Thread.sleep(1000);
            } catch (Exception e)
            {
                System.out.println("Client error");
            }
        }
    }
}
