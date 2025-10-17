package com.tlapaleria.backend.controller;

import com.tlapaleria.backend.model.Producto;
import com.tlapaleria.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // 🔹 Obtener todos los productos
    @GetMapping
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    // 🔹 Crear un nuevo producto
    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    // 🔹 Obtener un producto por ID
    @GetMapping("/{id}")
    public Producto getProductoById(@PathVariable Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    // 🔹 Actualizar un producto existente
    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto detallesProducto) {
        Producto productoExistente = productoRepository.findById(id).orElse(null);

        if (productoExistente != null) {
            productoExistente.setCodigo_barras(detallesProducto.getCodigo_barras());
            productoExistente.setClave(detallesProducto.getClave());
            productoExistente.setDescripcion(detallesProducto.getDescripcion());
            productoExistente.setCosto(detallesProducto.getCosto());
            productoExistente.setPrecio(detallesProducto.getPrecio());
            productoExistente.setExistencia(detallesProducto.getExistencia());
            productoExistente.setExistencia_min(detallesProducto.getExistencia_min());
            productoExistente.setUnidad(detallesProducto.getUnidad());
            productoExistente.setActivo(detallesProducto.getActivo());

            return productoRepository.save(productoExistente);
        } else {
            return null;
        }
    }

    // 🔹 Eliminar un producto por ID
    @DeleteMapping("/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return "Producto eliminado correctamente";
        } else {
            return "Producto no encontrado";
        }
    }
}
