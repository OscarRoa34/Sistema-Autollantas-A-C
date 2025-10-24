package co.edu.uptc.models.products;

public class Battery extends Product {
    private String voltage;
    private String capacity;

    public Battery(String id, String name, double price, String voltage, String capacity, String brand) {
        super(id, name, brand, price, 0);
        this.voltage = voltage;
        this.capacity = capacity;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }   
}
