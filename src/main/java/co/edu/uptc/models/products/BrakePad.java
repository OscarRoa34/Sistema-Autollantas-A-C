package co.edu.uptc.models.products;

public class BrakePad extends Product {
    private String materialType;
    private String vehicleCompatibility;

    public BrakePad(String id, String name, String brand, double price, String materialType, String vehicleCompatibility) {
        super(id, name, brand, price, 0);
        this.materialType = materialType;
        this.vehicleCompatibility = vehicleCompatibility;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getVehicleCompatibility() {
        return vehicleCompatibility;
    }

    public void setVehicleCompatibility(String vehicleCompatibility) {
        this.vehicleCompatibility = vehicleCompatibility;
    }

    @Override
    public String toString() {
        return "BrakePad{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", price=" + getPrice() +
                ", materialType='" + materialType + '\'' +
                ", vehicleCompatibility='" + vehicleCompatibility + '\'' +
                '}';
    }
}
