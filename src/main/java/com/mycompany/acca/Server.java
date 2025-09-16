/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.acca;

/**
 *
 * @author tello
 */
import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final List<SSLSocket> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        int port = 8443;

        // Cargar keystore
        KeyStore ks = KeyStore.getInstance("JKS");
ks.load(new FileInputStream("/home/tello/NetBeansProjects/acca/serverkeystore.jks"), "admin1234".toCharArray());


        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, "admin1234".toCharArray());

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), null, null);

        SSLServerSocket sss = (SSLServerSocket) sc.getServerSocketFactory().createServerSocket(port);
        System.out.println("Servidor TLS Chat escuchando en puerto " + port);

        while (true) {
            SSLSocket socket = (SSLSocket) sss.accept();
            synchronized (clients) {
                clients.add(socket);
            }
            new Thread(() -> handleClient(socket)).start();
        }
    }

    private static void handleClient(SSLSocket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                broadcast(line, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (clients) {
                clients.remove(socket);
            }
        }
    }

    private static void broadcast(String message, SSLSocket sender) {
        synchronized (clients) {
            for (SSLSocket client : clients) {
                if (client != sender) {
                    try {
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
