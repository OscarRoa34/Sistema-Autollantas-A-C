package co.edu.uptc.presenter;
import co.edu.uptc.models.products.Product;

public class test {
 
    public static void main(String[] args) {
        Product product = new Product("1", "Tire", "Michelin", 100.0, 50) {
        };
        System.out.println("Product created: " + product.getName());
    }
    
}