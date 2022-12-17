
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.*;
import java.net.*;

public class Server {
    int counter = 0;

    int primaryKey = 0;
    Semaphore mutex = new Semaphore(1);

    static ServerSocket serverSocket;

    static {
        try {
            serverSocket = new ServerSocket(1337);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void run2() throws TTransportException, IOException, InterruptedException { //NEW METHOD

        System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
        Socket server = serverSocket.accept();
        HTTPHandler handler = new HTTPHandler(server, mutex, primaryKey++);
        handler.start();
    }


    public static void main(String[] args) throws InterruptedException, TTransportException, IOException {
        Server client = new Server();

        while (true) {
            client.run2();
        }
    }
}
