package echo;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

// TCP connection / socket
// (server_ip, server_port, client_ip, client_port) uniquely identify a tcp connection
/// bidirectional connection

public class ThreadedEchoServer {
  public static final int SERVER_PORT = 8080;
  
  public static void main(String... args) throws IOException {
    System.out.println("Threaded echo server");
    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
    try {
      System.out.println("Start to accept incoming connections");
      while (true) {
        Socket clientSocket = serverSocket.accept();
        new Thread(new Handler(clientSocket)).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      serverSocket.close();
    }
  }
}
