package datos;

import modelo.Libro;
import java.sql.*;
import java.util.*;

public class GestorLibros {
    private static final String URL = "jdbc:mysql://localhost:3306/libreria";
    private static final String USUARIO = "root";
    private static final String PASSWORD = ""; 
    
    // Constructor que verifica la conexión
    public GestorLibros() throws SQLException {
        try (Connection conn = obtenerConexion()) {
            System.out.println("Conexion OK");
        }
    }
    
    private Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
    
    public List<Libro> buscarPorCategoria(String categoria) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE LOWER(categoria) LIKE LOWER(?)";
        
        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + categoria + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                libros.add(crearLibroDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por categoria: " + e.getMessage());
        }
        return libros;
    }
    
    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE LOWER(titulo) LIKE LOWER(?)";
        
        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + titulo + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                libros.add(crearLibroDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por titulo: " + e.getMessage());
        }
        return libros;
    }
    
    public List<Libro> buscarPorAutor(String autor) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE LOWER(autor) LIKE LOWER(?)";
        
        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + autor + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                libros.add(crearLibroDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por autor: " + e.getMessage());
        }
        return libros;
    }
    
    public List<Libro> obtenerTodosLosLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY id";
        
        try (Connection conn = obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                libros.add(crearLibroDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los libros: " + e.getMessage());
        }
        return libros;
    }
    
    public boolean agregarLibro(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, categoria, precio) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getCategoria());
            stmt.setDouble(4, libro.getPrecio());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar libro: " + e.getMessage());
            return false;
        }
    }
    
    private Libro crearLibroDesdeResultSet(ResultSet rs) throws SQLException {
        return new Libro(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("autor"),
            rs.getString("categoria"),
            rs.getDouble("precio")
        );
    }
}