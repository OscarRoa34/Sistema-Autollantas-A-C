package co.edu.uptc.presenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import co.edu.uptc.models.products.Battery;
import co.edu.uptc.models.products.BrakePad;
import co.edu.uptc.models.products.Filters;
import co.edu.uptc.models.products.Lubricant;
import co.edu.uptc.models.products.Product;
import co.edu.uptc.models.products.Tire;
import co.edu.uptc.models.registers.PurchaseRegister;
import co.edu.uptc.models.registers.Register;
import co.edu.uptc.models.registers.SellingRegister;
import co.edu.uptc.persistence.Persistence;
import co.edu.uptc.persistence.Persistence.RuntimeTypeAdapterFactory;

public class Presenter {

    // Instancias de Persistence para cada tipo de producto
    private Persistence<Product> tiresPersistence;
    private Persistence<Product> lubFiltersPersistence;
    private Persistence<Product> batteriesPersistence;
    private Persistence<Product> brakePadsPersistence;
    
    // Instancias de Persistence para registros
    private Persistence<Register> purchasesPersistence;
    private Persistence<Register> salesPersistence;

    // Constructor - Inicializa todas las instancias de Persistence
    public Presenter() {
        initializePersistence();
    }

    /**
     * Inicializa todas las instancias de Persistence con sus respectivos TypeAdapters
     */
    private void initializePersistence() {
        // TypeAdapter para productos
        RuntimeTypeAdapterFactory<Product> productAdapter = RuntimeTypeAdapterFactory
            .of(Product.class, "productType")
            .registerSubtype(Tire.class, "Tire")
            .registerSubtype(Lubricant.class, "Lubricant")
            .registerSubtype(Filters.class, "Filters")
            .registerSubtype(Battery.class, "Battery")
            .registerSubtype(BrakePad.class, "BrakePad");

        // Persistence para cada tipo de producto
        tiresPersistence = new Persistence<>("tires.json", productAdapter);
        lubFiltersPersistence = new Persistence<>("lubricants_filters.json", productAdapter);
        batteriesPersistence = new Persistence<>("batteries.json", productAdapter);
        brakePadsPersistence = new Persistence<>("brakepads.json", productAdapter);

        // TypeAdapter para registros
        RuntimeTypeAdapterFactory<Register> registerAdapter = RuntimeTypeAdapterFactory
            .of(Register.class, "registerType")
            .registerSubtype(PurchaseRegister.class, "PurchaseRegister")
            .registerSubtype(SellingRegister.class, "SellingRegister");

        // Persistence para registros
        purchasesPersistence = new Persistence<>("purchases.json", registerAdapter);
        salesPersistence = new Persistence<>("sales.json", registerAdapter);
    }

    // =======================================================
    // ==               APPLICATION LIFECYCLE               ==
    // =======================================================

    public void startApplication() {
        // Inicializar aplicación
        loadInitialData();
    }

    public void loadInitialData() {
        try {
            // Cargar todos los datos iniciales
            requestTireList();
            requestLubricantList();
            requestBatteryList();
            requestBrakePadList();
            requestPurchasesHistory();
            requestSalesHistory();
        } catch (Exception e) {
            System.err.println("Error cargando datos iniciales: " + e.getMessage());
        }
    }

    public void closeApplication() {
        // Guardar cambios pendientes si es necesario
        System.out.println("Cerrando aplicación...");
    }

    // =======================================================
    // ==              INVENTORY MANAGEMENT                 ==
    // =======================================================

    // ----- Llantas (Tires) -----
    public List<Tire> requestTireList() {
        try {
            List<Product> products = tiresPersistence.loadList();
            return products.stream()
                .filter(p -> p instanceof Tire)
                .map(p -> (Tire) p)
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error al cargar llantas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveTire(Tire tire) {
        try {
            List<Product> products = tiresPersistence.loadList();
            
            // Buscar si ya existe (por ID) para actualizar o agregar nuevo
            boolean exists = false;
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId().equals(tire.getId())) {
                    products.set(i, tire);
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                products.add(tire);
            }
            
            tiresPersistence.saveList(products);
        } catch (IOException e) {
            System.err.println("Error al guardar llanta: " + e.getMessage());
        }
    }

    public void requestTireDeletion(String tireId) {
        try {
            List<Product> products = tiresPersistence.loadList();
            products.removeIf(p -> p.getId().equals(tireId));
            tiresPersistence.saveList(products);
        } catch (IOException e) {
            System.err.println("Error al eliminar llanta: " + e.getMessage());
        }
    }

    public List<Tire> filterTires(String searchText, List<String> brands) {
        List<Tire> allTires = requestTireList();
        
        return allTires.stream()
            .filter(t -> searchText == null || searchText.isEmpty() || 
                        t.getName().toLowerCase().contains(searchText.toLowerCase()))
            .filter(t -> brands == null || brands.isEmpty() || brands.contains(t.getBrand()))
            .collect(Collectors.toList());
    }

    // ----- Lubricantes y Filtros (Lubricants & Filters) -----
    public List<Product> requestLubricantList() {
        try {
            return lubFiltersPersistence.loadList();
        } catch (IOException e) {
            System.err.println("Error al cargar lubricantes/filtros: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveLubricant(Product lubricantOrFilter) {
        try {
            List<Product> products = lubFiltersPersistence.loadList();
            
            boolean exists = false;
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId().equals(lubricantOrFilter.getId())) {
                    products.set(i, lubricantOrFilter);
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                products.add(lubricantOrFilter);
            }
            
            lubFiltersPersistence.saveList(products);
        } catch (IOException e) {
            System.err.println("Error al guardar lubricante/filtro: " + e.getMessage());
        }
    }

    public void requestLubricantDeletion(String lubricantId) {
        try {
            List<Product> products = lubFiltersPersistence.loadList();
            products.removeIf(p -> p.getId().equals(lubricantId));
            lubFiltersPersistence.saveList(products);
        } catch (IOException e) {
            System.err.println("Error al eliminar lubricante/filtro: " + e.getMessage());
        }
    }

    public List<Product> filterLubricants(String searchText, List<String> brands) {
        List<Product> allProducts = requestLubricantList();
        
        return allProducts.stream()
            .filter(p -> searchText == null || searchText.isEmpty() || 
                        p.getName().toLowerCase().contains(searchText.toLowerCase()))
            .filter(p -> brands == null || brands.isEmpty() || brands.contains(p.getBrand()))
            .collect(Collectors.toList());
    }

    // ----- Baterías (Batteries) -----
    public List<Battery> requestBatteryList() {
        try {
            List<Product> products = batteriesPersistence.loadList();
            return products.stream()
                .filter(p -> p instanceof Battery)
                .map(p -> (Battery) p)
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error al cargar baterías: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveBattery(Battery battery) {
        try {
            List<Product> products = batteriesPersistence.loadList();
            
            boolean exists = false;
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId().equals(battery.getId())) {
                    products.set(i, battery);
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                products.add(battery);
            }
            
            batteriesPersistence.saveList(products);
        } catch (IOException e) {
            System.err.println("Error al guardar batería: " + e.getMessage());
        }
    }

    public void requestBatteryDeletion(String batteryId) {
        try {
            List<Product> products = batteriesPersistence.loadList();
            products.removeIf(p -> p.getId().equals(batteryId));
            batteriesPersistence.saveList(products);
        } catch (IOException e) {
            System.err.println("Error al eliminar batería: " + e.getMessage());
        }
    }

    public List<Battery> filterBatteries(String searchText, List<String> brands) {
        List<Battery> allBatteries = requestBatteryList();
        
        return allBatteries.stream()
            .filter(b -> searchText == null || searchText.isEmpty() || 
                        b.getName().toLowerCase().contains(searchText.toLowerCase()))
            .filter(b -> brands == null || brands.isEmpty() || brands.contains(b.getBrand()))
            .collect(Collectors.toList());
    }
    
    // ----- Pastillas de Freno (Brake Pads) -----
    public List<BrakePad> requestBrakePadList() {
        try {
            List<Product> products = brakePadsPersistence.loadList();
            return products.stream()
                .filter(p -> p instanceof BrakePad)
                .map(p -> (BrakePad) p)
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error al cargar pastillas de freno: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveBrakePad(BrakePad brakePad) {
        try {
            List<Product> products = brakePadsPersistence.loadList();
            
            boolean exists = false;
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId().equals(brakePad.getId())) {
                    products.set(i, brakePad);
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                products.add(brakePad);
            }
            
            brakePadsPersistence.saveList(products);
        } catch (IOException e) {
            System.err.println("Error al guardar pastilla de freno: " + e.getMessage());
        }
    }

    public void requestBrakePadDeletion(String brakePadId) {
        try {
            List<Product> products = brakePadsPersistence.loadList();
            products.removeIf(p -> p.getId().equals(brakePadId));
            brakePadsPersistence.saveList(products);
        } catch (IOException e) {
            System.err.println("Error al eliminar pastilla de freno: " + e.getMessage());
        }
    }

    public List<BrakePad> filterBrakePads(String searchText, List<String> brands) {
        List<BrakePad> allBrakePads = requestBrakePadList();
        
        return allBrakePads.stream()
            .filter(b -> searchText == null || searchText.isEmpty() || 
                        b.getName().toLowerCase().contains(searchText.toLowerCase()))
            .filter(b -> brands == null || brands.isEmpty() || brands.contains(b.getBrand()))
            .collect(Collectors.toList());
    }

    // =======================================================
    // ==                 SALES & PURCHASES                 ==
    // =======================================================

    // ----- Ventas (Sales) -----
    public List<SellingRegister> requestSalesHistory() {
        try {
            List<Register> registers = salesPersistence.loadList();
            return registers.stream()
                .filter(r -> r instanceof SellingRegister)
                .map(r -> (SellingRegister) r)
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error al cargar ventas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void registerNewSale(SellingRegister sale) {
        try {
            List<Register> registers = salesPersistence.loadList();
            registers.add(sale);
            salesPersistence.saveList(registers);
            
            // TODO: Actualizar stock automáticamente
        } catch (IOException e) {
            System.err.println("Error al registrar venta: " + e.getMessage());
        }
    }

    public SellingRegister requestSaleDetails(String saleId) {
        List<SellingRegister> sales = requestSalesHistory();
        return sales.stream()
            .filter(s -> s.getInvoiceNumber().equals(saleId))
            .findFirst()
            .orElse(null);
    }

    public void requestSaleCancellation(String saleId) {
        try {
            List<Register> registers = salesPersistence.loadList();
            registers.removeIf(r -> r.getInvoiceNumber().equals(saleId));
            salesPersistence.saveList(registers);
            
            // TODO: Revertir stock automáticamente
        } catch (IOException e) {
            System.err.println("Error al cancelar venta: " + e.getMessage());
        }
    }
    
    // ----- Compras (Purchases) -----
    public List<PurchaseRegister> requestPurchasesHistory() {
        try {
            List<Register> registers = purchasesPersistence.loadList();
            return registers.stream()
                .filter(r -> r instanceof PurchaseRegister)
                .map(r -> (PurchaseRegister) r)
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error al cargar compras: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void registerNewPurchase(PurchaseRegister purchase) {
        try {
            List<Register> registers = purchasesPersistence.loadList();
            registers.add(purchase);
            purchasesPersistence.saveList(registers);
            
            // TODO: Actualizar stock automáticamente
        } catch (IOException e) {
            System.err.println("Error al registrar compra: " + e.getMessage());
        }
    }

    public PurchaseRegister requestPurchaseDetails(String purchaseId) {
        List<PurchaseRegister> purchases = requestPurchasesHistory();
        return purchases.stream()
            .filter(p -> p.getInvoiceNumber().equals(purchaseId))
            .findFirst()
            .orElse(null);
    }

    public void requestPurchaseCancellation(String purchaseId) {
        try {
            List<Register> registers = purchasesPersistence.loadList();
            registers.removeIf(r -> r.getInvoiceNumber().equals(purchaseId));
            purchasesPersistence.saveList(registers);
            
            // TODO: Revertir stock automáticamente
        } catch (IOException e) {
            System.err.println("Error al cancelar compra: " + e.getMessage());
        }
    }

    // =======================================================
    // ==                 REPORTS & ALERTS                  ==
    // =======================================================

    public List<Product> requestLowStockAlerts() {
        List<Product> lowStockProducts = new ArrayList<>();
        int LOW_STOCK_THRESHOLD = 10;
        
        try {
            // Verificar stock bajo en todos los tipos de productos
            lowStockProducts.addAll(tiresPersistence.loadList().stream()
                .filter(p -> p.getstock() < LOW_STOCK_THRESHOLD)
                .collect(Collectors.toList()));
                
            lowStockProducts.addAll(lubFiltersPersistence.loadList().stream()
                .filter(p -> p.getstock() < LOW_STOCK_THRESHOLD)
                .collect(Collectors.toList()));
                
            lowStockProducts.addAll(batteriesPersistence.loadList().stream()
                .filter(p -> p.getstock() < LOW_STOCK_THRESHOLD)
                .collect(Collectors.toList()));
                
            lowStockProducts.addAll(brakePadsPersistence.loadList().stream()
                .filter(p -> p.getstock() < LOW_STOCK_THRESHOLD)
                .collect(Collectors.toList()));
                
        } catch (IOException e) {
            System.err.println("Error al verificar stock bajo: " + e.getMessage());
        }
        
        return lowStockProducts;
    }

    public void generateSalesReport(Date startDate, Date endDate) {
        // TODO: Implementar generación de reporte de ventas
        System.out.println("Generando reporte de ventas de " + startDate + " a " + endDate);
    }

    public void generateInventoryValuationReport() {
        // TODO: Implementar valoración de inventario
        System.out.println("Generando reporte de valoración de inventario");
    }

    public void generateTopSellingProductsReport(int limit) {
        // TODO: Implementar reporte de productos más vendidos
        System.out.println("Generando reporte de top " + limit + " productos más vendidos");
    }
}