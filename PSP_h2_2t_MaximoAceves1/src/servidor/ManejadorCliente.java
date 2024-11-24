package servidor;

import datos.GestorLibros;
import modelo.Libro;
import java.net.*;
import java.io.*;
import java.util.List;

public class ManejadorCliente implements Runnable {
    private Socket clienteSocket;
    private GestorLibros gestorLibros;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private volatile boolean conectado;
    
    public ManejadorCliente(Socket socket, GestorLibros gestorLibros) {
        this.clienteSocket = socket;
        this.gestorLibros = gestorLibros;
        this.conectado = true;
    }
    
    @Override
    public void run() {
        try {
            // Importante: crear primero la salida
            salida = new ObjectOutputStream(clienteSocket.getOutputStream());
            entrada = new ObjectInputStream(clienteSocket.getInputStream());
            
            while (conectado) {
                try {
                    String[] peticion = (String[]) entrada.readObject();
                    procesarPeticion(peticion);
                } catch (EOFException | SocketException e) {
                    System.out.println("Cliente desconectado");
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error en el manejador de cliente: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }
    
    private void procesarPeticion(String[] peticion) throws IOException {
        if (peticion == null || peticion.length < 2) {
            enviarError("Petición inválida");
            return;
        }
        
        String tipo = peticion[0].toLowerCase();
        String valor = peticion[1];
        List<Libro> resultados;
        
        try {
            resultados = switch (tipo) {
                case "categoria" -> gestorLibros.buscarPorCategoria(valor);
                case "titulo" -> gestorLibros.buscarPorTitulo(valor);
                case "autor" -> gestorLibros.buscarPorAutor(valor);
                case "todos" -> gestorLibros.obtenerTodosLosLibros();
                default -> {
                    enviarError("Tipo de búsqueda no válido: " + tipo);
                    yield null;
                }
            };
            
            if (resultados != null) {
                salida.writeObject(resultados);
            }
        } catch (Exception e) {
            enviarError("Error al procesar la petición: " + e.getMessage());
        }
    }
    
    private void enviarError(String mensaje) throws IOException {
        salida.writeObject(List.of());  // Lista vacía como indicador de error
        System.err.println("Error: " + mensaje);
    }
    
    private void cerrarConexion() {
        conectado = false;
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (clienteSocket != null) clienteSocket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar conexiones: " + e.getMessage());
        }
    }
}