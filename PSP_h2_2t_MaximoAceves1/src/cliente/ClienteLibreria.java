package cliente;

import modelo.Libro;
import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Scanner;

public class ClienteLibreria {
    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private Scanner scanner;
    private volatile boolean conectado;
    
    public ClienteLibreria() {
        scanner = new Scanner(System.in);
        conectado = false;
    }
    
    public void conectar() {
        try {
            socket = new Socket(HOST, PUERTO);
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            conectado = true;
            
            System.out.println("Conectado al servidor de libreria");
            menuPrincipal();
        } catch (ConnectException e) {
            System.err.println("No se pudo conectar al servidor.");
        } catch (Exception e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
    }
    
    private void menuPrincipal() {
        while (conectado) {
            try {
                mostrarMenu();
                int opcion = leerOpcion();
                
                if (opcion == 5) {
                    System.out.println("Gracias por usar mi sistema de libreria.");
                    break;
                }
                
                procesarOpcion(opcion);
            } catch (Exception e) {
                System.err.println("Error en el menu: " + e.getMessage());
                System.out.println("Intentando reconectar...");
                reconectar();
            }
        }
        cerrarConexion();
    }
    
    private void mostrarMenu() {
        System.out.println("\n=== SISTEMA DE LIBRERIA ===");
        System.out.println("1. Buscar por categoria");
        System.out.println("2. Buscar por titulo");
        System.out.println("3. Buscar por autor");
        System.out.println("4. Ver todos los libros");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opcion: ");
    }
    
    private int leerOpcion() {
        while (true) {
            try {
                String entrada = scanner.nextLine().trim();
                int opcion = Integer.parseInt(entrada);
                if (opcion >= 1 && opcion <= 5) {
                    return opcion;
                }
                System.out.println("Por favor, ingrese un numero entre 1 y 5.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un numero válido.");
            }
        }
    }
    
    
    private void procesarOpcion(int opcion) throws IOException, ClassNotFoundException {
        String tipo;
        String valor = "";
        
        switch (opcion) {
            case 1:
                tipo = "categoria";
                System.out.print("Introduce la categorua a buscar: ");
                valor = scanner.nextLine().trim();
                break;
            case 2:
                tipo = "titulo";
                System.out.print("Introduce el titulo a buscar: ");
                valor = scanner.nextLine().trim();
                break;
            case 3:
                tipo = "autor";
                System.out.print("Introduce el autor a buscar: ");
                valor = scanner.nextLine().trim();
                break;
            case 4:
                tipo = "todos";
                break;
            default:
                return;
        }
        
        // Enviar mi peticion al servidor
        enviarPeticion(tipo, valor);
        
        // Recibir y mostrar resultados
        @SuppressWarnings("unchecked")
        List<Libro> libros = (List<Libro>) entrada.readObject();
        mostrarResultados(libros);
    }
    
    private void enviarPeticion(String tipo, String valor) throws IOException {
        String[] peticion = new String[]{tipo, valor};
        salida.writeObject(peticion);
        salida.flush();
    }
    
    private void mostrarResultados(List<Libro> libros) {
        if (libros.isEmpty()) {
            System.out.println("No se encontraron resultados.");
            return;
        }
        
        System.out.println("\nResultados encontrados:");
        System.out.println("------------------------");
        for (Libro libro : libros) {
            System.out.println(libro);
            System.out.println("------------------------");
        }
    }
    
    private void reconectar() {
        int intentos = 0;
        final int maxIntentos = 3;
        
        while (intentos < maxIntentos) {
            try {
                cerrarConexion();
                Thread.sleep(2000); // Esperar 2 segundos entre intentos
                
                socket = new Socket(HOST, PUERTO);
                salida = new ObjectOutputStream(socket.getOutputStream());
                entrada = new ObjectInputStream(socket.getInputStream());
                conectado = true;
                
                System.out.println("Reconexión exitosa");
                return;
            } catch (Exception e) {
                intentos++;
                System.err.println("Intento " + intentos + " fallido: " + e.getMessage());
            }
        }
        
        System.err.println("No se pudo reconectar después de " + maxIntentos + " intentos");
        conectado = false;
    }
    
    private void cerrarConexion() {
        conectado = false;
        try {
            if (salida != null) salida.close();
            if (entrada != null) entrada.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar conexiones: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        ClienteLibreria cliente = new ClienteLibreria();
        cliente.conectar();
    }
}