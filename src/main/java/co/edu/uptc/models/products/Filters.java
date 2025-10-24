package co.edu.uptc.models.products;

public class Filters extends Product {
    private String vehicleType;
    private String filterType;

    public Filters(String id, String name, double price, String vehicleType, String filterType, String brand) {
        super(id, name, brand, price, 0);
        this.vehicleType = vehicleType;
        this.filterType = filterType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }   
}
