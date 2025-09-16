/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_app;

/**
 *
 * @author tello
 */
import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class Client1 {

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
        System.out.println("Conectado al servidor TLS Chat en " + host + ":" + port);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        // Hilo para leer mensajes del servidor
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("Otro cliente: " + msg);
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
