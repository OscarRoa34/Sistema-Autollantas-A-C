package co.edu.uptc.presenter;

import java.util.List;
import java.util.Date;

/*
 * NOTA PARA EL DESARROLLADOR DEL MODELO:
 * Las clases como 'TireDTO', 'SaleDTO', 'ReportDTO', 'ServiceDTO', etc., mencionadas
 * en los parámetros de los métodos, son DTOs (Data Transfer Objects).
 * Deberás crearlas como clases Java normales (POJOs) con sus respectivos
 * atributos privados, constructor y métodos getters/setters.
 */

public class Presenter {

    // =======================================================
    // ==               APPLICATION LIFECYCLE               ==
    // =======================================================

    /**
     * Inicia la aplicación, prepara la vista principal y carga los datos iniciales.
     * Debe ser llamado al arrancar el programa.
     */
    public void startApplication() {}

    /**
     * Carga todos los datos esenciales desde el Modelo la primera vez.
     * La Vista debe mostrar un indicador de carga mientras esto ocurre.
     */
    public void loadInitialData() {}

    /**
     * Realiza las acciones necesarias para cerrar la aplicación de forma segura,
     * como guardar cualquier cambio pendiente.
     */
    public void closeApplication() {}


    // =======================================================
    // ==              INVENTORY MANAGEMENT                 ==
    // =======================================================

    // ----- Llantas (Tires) -----
    /** Solicita la lista completa de llantas para mostrarla en la vista. */
    public void requestTireList() {}
    /** Solicita guardar una llanta nueva o actualizada. */
    public void saveTire(TireDTO tire) {}
    /** Solicita la eliminación de una llanta por su ID. */
    public void requestTireDeletion(String tireId) {}
    /** Solicita la lista de llantas filtrada por texto de búsqueda y marcas. */
    public void filterTires(String searchText, List<String> brands) {}

    // ----- Lubricantes y Filtros (Lubricants & Filters) -----
    /** Solicita la lista completa de lubricantes y filtros. */
    public void requestLubricantList() {}
    /** Solicita guardar un lubricante nuevo o actualizado. */
    public void saveLubricant(LubricantDTO lubricant) {}
    /** Solicita la eliminación de un lubricante por su ID. */
    public void requestLubricantDeletion(String lubricantId) {}
    /** Solicita la lista de lubricantes filtrada. */
    public void filterLubricants(String searchText, List<String> brands) {}

    // ----- Baterías (Batteries) -----
    /** Solicita la lista completa de baterías. */
    public void requestBatteryList() {}
    /** Solicita guardar una batería nueva o actualizada. */
    public void saveBattery(BatteryDTO battery) {}
    /** Solicita la eliminación de una batería por su ID. */
    public void requestBatteryDeletion(String batteryId) {}
    /** Solicita la lista de baterías filtrada. */
    public void filterBatteries(String searchText, List<String> brands) {}
    
    // ----- Pastillas de Freno (Brake Pads) -----
    /** Solicita la lista completa de pastillas de freno. */
    public void requestBrakePadList() {}
    /** Solicita guardar una pastilla de freno nueva o actualizada. */
    public void saveBrakePad(BrakePadDTO brakePad) {}
    /** Solicita la eliminación de una pastilla de freno por su ID. */
    public void requestBrakePadDeletion(String brakePadId) {}
    /** Solicita la lista de pastillas de freno filtrada. */
    public void filterBrakePads(String searchText, List<String> brands) {}

    // =======================================================
    // ==                SERVICES MANAGEMENT                ==
    // =======================================================

    /** Solicita la lista completa de servicios. */
    public void requestServiceList() {}
    /** Solicita guardar un servicio nuevo o actualizado. */
    public void saveService(ServiceDTO service) {}
    /** Solicita la eliminación de un servicio por su ID. */
    public void requestServiceDeletion(String serviceId) {}
    /** Solicita la lista de servicios filtrada por el nombre. */
    public void filterServices(String searchText) {}


    // =======================================================
    // ==                 SALES & PURCHASES                 ==
    // =======================================================

    // ----- Ventas (Sales) -----
    /** Solicita el historial completo de ventas. */
    public void requestSalesHistory() {}
    /** Registra una nueva venta. El Modelo debe actualizar el stock automáticamente. */
    public void registerNewSale(SaleDTO sale) {}
    /** Solicita los detalles de una venta específica para visualización o edición. */
    public void requestSaleDetails(String saleId) {}
    /** Solicita la anulación (eliminación) de una venta. El Modelo debe revertir el stock. */
    public void requestSaleCancellation(String saleId) {}
    
    // ----- Compras (Purchases) -----
    /** Solicita el historial completo de compras a proveedores. */
    public void requestPurchasesHistory() {}
    /** Registra una nueva compra. El Modelo debe actualizar el stock automáticamente. */
    public void registerNewPurchase(PurchaseDTO purchase) {}
    /** Solicita los detalles de una compra específica. */
    public void requestPurchaseDetails(String purchaseId) {}
    /** Solicita la anulación (eliminación) de una compra. El Modelo debe revertir el stock. */
    public void requestPurchaseCancellation(String purchaseId) {}


    // =======================================================
    // ==                 REPORTS & ALERTS                  ==
    // =======================================================

    /**
     * Solicita la lista de productos con bajo stock para mostrar alertas en la UI.
     * Debería llamarse periódicamente o al iniciar la app.
     */
    public void requestLowStockAlerts() {}

    /**
     * Genera y solicita un reporte de ventas en un rango de fechas.
     * El resultado (ReportDTO) será enviado a la Vista para ser mostrado.
     * @param startDate Fecha de inicio del reporte.
     * @param endDate Fecha de fin del reporte.
     */
    public void generateSalesReport(Date startDate, Date endDate) {}

    /**
     * Genera y solicita un reporte de valoración total del inventario actual.
     */
    public void generateInventoryValuationReport() {}

    /**
     * Genera y solicita un reporte de los productos más vendidos.
     * @param limit El número de productos a mostrar (ej. Top 10).
     */
    public void generateTopSellingProductsReport(int limit) {}

}