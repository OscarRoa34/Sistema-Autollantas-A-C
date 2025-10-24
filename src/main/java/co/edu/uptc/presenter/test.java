package co.edu.uptc.presenter;

import co.edu.uptc.models.products.Product;
import co.edu.uptc.models.products.Tire;
import co.edu.uptc.persistence.Persistence;
import co.edu.uptc.persistence.Persistence.RuntimeTypeAdapterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        System.out.println("=== TEST DE PERSISTENCIA ===\n");

        // Configurar el RuntimeTypeAdapterFactory para manejar polimorfismo
        RuntimeTypeAdapterFactory<Product> typeAdapter = RuntimeTypeAdapterFactory
                .of(Product.class, "productType")
                .registerSubtype(Tire.class, "Tire");

        // Crear la persistencia
        Persistence<Product> persistence = new Persistence<>("test.json", typeAdapter);

        try {
            // ===== PASO 1: GUARDAR PRODUCTOS =====
            System.out.println("1. Creando y guardando productos...");
            
            List<Product> products = new ArrayList<>();
            
            // Llanta Michelin
            Tire michelin = new Tire(
                "T001", 
                "Llanta Pilot Sport 4", 
                250.99, 
                "205/55R16", 
                "Deportivo", 
                "Michelin"
            );
            michelin.setstock(15);
            
            // Llanta Bridgestone
            Tire bridgestone = new Tire(
                "T002", 
                "Llanta Dueler H/T", 
                180.50, 
                "265/70R17", 
                "Todo Terreno", 
                "Bridgestone"
            );
            bridgestone.setstock(8);
            
            products.add(michelin);
            products.add(bridgestone);
            
            // Guardar en JSON
            persistence.saveList(products);
            System.out.println("✓ Productos guardados exitosamente en test.json\n");
            
            // Mostrar productos guardados
            System.out.println("Productos guardados:");
            for (Product p : products) {
                System.out.println("  - " + p.getName() + " (" + p.getBrand() + ")");
                if (p instanceof Tire) {
                    Tire t = (Tire) p;
                    System.out.println("    Tamaño: " + t.getSize() + ", Tipo: " + t.getType());
                }
            }
            System.out.println();
            
            // ===== PASO 2: CARGAR PRODUCTOS =====
            System.out.println("2. Cargando productos desde test.json...");
            
            List<Product> loadedProducts = persistence.loadList();
            System.out.println("✓ Se cargaron " + loadedProducts.size() + " productos\n");
            
            // Mostrar productos cargados
            System.out.println("Productos cargados:");
            for (Product p : loadedProducts) {
                System.out.println("  - ID: " + p.getId());
                System.out.println("    Nombre: " + p.getName());
                System.out.println("    Marca: " + p.getBrand());
                System.out.println("    Precio: $" + p.getPrice());
                System.out.println("    Stock: " + p.getstock());
                
                if (p instanceof Tire) {
                    Tire t = (Tire) p;
                    System.out.println("    Tamaño: " + t.getSize());
                    System.out.println("    Tipo: " + t.getType());
                }
                System.out.println();
            }
            
            // ===== PASO 3: VERIFICAR INTEGRIDAD =====
            System.out.println("3. Verificando integridad de los datos...");
            
            boolean integridadOk = true;
            if (loadedProducts.size() != products.size()) {
                System.out.println("✗ Error: Número de productos no coincide");
                integridadOk = false;
            }
            
            for (int i = 0; i < loadedProducts.size(); i++) {
                Product original = products.get(i);
                Product cargado = loadedProducts.get(i);
                
                if (!original.getId().equals(cargado.getId())) {
                    System.out.println("✗ Error: ID no coincide en producto " + i);
                    integridadOk = false;
                }
                
                if (!original.getName().equals(cargado.getName())) {
                    System.out.println("✗ Error: Nombre no coincide en producto " + i);
                    integridadOk = false;
                }
                
                if (original.getPrice() != cargado.getPrice()) {
                    System.out.println("✗ Error: Precio no coincide en producto " + i);
                    integridadOk = false;
                }
            }
            
            if (integridadOk) {
                System.out.println("✓ Todos los datos se guardaron y cargaron correctamente\n");
            }
            
            // ===== PASO 4: INFORMACIÓN DEL ARCHIVO =====
            System.out.println("4. Información del archivo:");
            System.out.println("  - Existe: " + (persistence.exists() ? "Sí" : "No"));
            System.out.println("  - Ubicación: test.json");
            System.out.println("  - Productos almacenados: " + loadedProducts.size());
            
        } catch (IOException e) {
            System.err.println("✗ Error durante la persistencia:");
            e.printStackTrace();
        }
        
        System.out.println("\n=== TEST FINALIZADO ===");
    }
}