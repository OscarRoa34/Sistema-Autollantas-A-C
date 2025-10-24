package co.edu.uptc.models.products;

public class Lubricant extends Product {
    private String type;

    public Lubricant(String id, String name, double price, String type, String brand) {
        super(id, name, brand, price, 0);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
