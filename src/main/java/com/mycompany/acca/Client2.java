package com.mycompany.acca;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class Client2 {

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8443;

        // Cargar truststore
        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(new FileInputStream("/home/tello/NetBeansProjects/acca/serverkeystore.jks"), "admin1234".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, tmf.getTrustManagers(), null);

        SSLSocket socket = (SSLSocket) sc.getSocketFactory().createSocket(host, port);
        System.out.println("Client2 conectado al servidor TLS en " + host + ":" + port);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        // Hilo para leer mensajes del servidor
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("Otro cliente dice: " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Hilo principal para enviar mensajes
        String line;
        while ((line = stdin.readLine()) != null) {
            out.println(line);
        }
    }
}

