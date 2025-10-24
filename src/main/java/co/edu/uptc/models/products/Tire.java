package co.edu.uptc.models.products;

public class Tire extends Product {
    private String size;
    private String type;

    public Tire(String id, String name, double price, String size, String type, String brand) {
        super(id, name, brand, price, 0);
        this.size = size;
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
