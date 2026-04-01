// javac lab6/Server.java
// java lab6.Server

package lab6;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final int PORT = 8000;
    private static int clientCounter = 0;
    
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Сервер запущен");

        new Thread(Server::readConsole).start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Новый клиент подключен" + socket);

                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readConsole(){
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try{
                String message = console.readLine();
                broadcast("Server: " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private String name;

        public ClientHandler(Socket socket){
            this.socket = socket;
            this.name = "Client-" + (++clientCounter);
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.out.println("Вы подключены как " + name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))){
                
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println(name + ": " + msg);
                    Server.broadcast(name + ": " + msg);
                }
            } catch (IOException e) {
                System.out.println("Ошибка связи с клиентом");
            } finally {
                disconnect();
            }
        }
        public void sendMessage(String message) {
            out.println(message);
        }

        private void disconnect() {
            try{
                clients.remove(this);
                if (socket != null && !socket.isClosed()){
                    socket.close();
                }System.out.println(name + " отключен и удален");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
