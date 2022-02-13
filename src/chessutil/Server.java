/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessutil;

import chessgame.GameController;
import chessgui.GameRequest;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

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
        ArrayBlockingQueue<GameRequest> tasks = new ArrayBlockingQueue<GameRequest>(25);
        ArrayBlockingQueue<GameRequest> responses = new ArrayBlockingQueue<GameRequest>(25);
        GameController gameController = new GameController(tasks, responses);
        new Thread(gameController).start();

        try
        {
            ObjectInputStream objectInputStream = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(s.getOutputStream());
            while (true)
            {
                Object obj = objectInputStream.readObject();
                System.out.println("[" + new Date().toString() + "] Receiving request: " + obj);
                tasks.add((GameRequest)obj);

                while (responses.isEmpty()) { Thread.sleep(100); }
                while (!responses.isEmpty()) {
                    Object objToSend = responses.remove();
                    objectOutputStream.writeObject(objToSend);
                    objectOutputStream.flush();

                    System.out.println("[" + new Date().toString() + "] Sending response: " + objToSend);
                }

            }
        } catch (Exception e)
        {
            System.out.println("Error in Socket: " + e);
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
