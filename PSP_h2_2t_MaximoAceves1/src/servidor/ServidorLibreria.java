package servidor;

import datos.GestorLibros;
import java.net.*;
import java.io.*;
import java.sql.SQLException;

public class ServidorLibreria {
    private static final int PUERTO = 12345;
    private ServerSocket serverSocket;
    private GestorLibros gestorLibros;
    private volatile boolean ejecutando;
    
    public ServidorLibreria() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            gestorLibros = new GestorLibros();
            ejecutando = true;
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver de MySQL: " + e.getMessage());
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void iniciar() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado en el puerto " + PUERTO);
            
            while (ejecutando) {
                try {
                    Socket clienteSocket = serverSocket.accept();
                    System.out.println("Nuevo cliente conectado desde: " + 
                                     clienteSocket.getInetAddress().getHostAddress());
                    
                    ManejadorCliente manejador = new ManejadorCliente(clienteSocket, gestorLibros);
                    new Thread(manejador).start();
                } catch (IOException e) {
                    if (ejecutando) {
                        System.err.println("Error al aceptar cliente: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        } finally {
            detener();
        }
    }
    
    public void detener() {
        ejecutando = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        ServidorLibreria servidor = new ServidorLibreria();
        
        // Manejador de cierre limpio
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nCerrando el servidor...");
            servidor.detener();
        }));
        
        servidor.iniciar();
    }
}