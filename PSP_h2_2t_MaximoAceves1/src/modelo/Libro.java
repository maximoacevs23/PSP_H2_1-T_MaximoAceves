package modelo;

import java.io.Serializable;

public class Libro implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String titulo;
    private String autor;
    private String categoria;
    private double precio;
    
    public Libro(int id, String titulo, String autor, String categoria, double precio) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.precio = precio;
    }
    
    // Constructor sin el ID para que se puedan añadir nuevos libros
    public Libro(String titulo, String autor, String categoria, double precio) {
        this(-1, titulo, autor, categoria, precio);
    }
    
    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    @Override
    public String toString() {
        return String.format("""
            ID: %d
            Título: %s
            Autor: %s
            Categoría: %s
            Precio: %.2f€
            """, id, titulo, autor, categoria, precio);
    }
}