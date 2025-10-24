package co.edu.uptc.models.registers;

public class PurchaseRegister extends Register {
    private String supplierName;

    public PurchaseRegister(String invoiceNumber, String date, double total, String status, String supplierName) {
        super(invoiceNumber, date, total, status);
        this.supplierName = supplierName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Override
    public String toString() {
        return "PurchaseRegister{" +
                "supplierName='" + supplierName + '\'' +
                ", invoiceNumber='" + getInvoiceNumber() + '\'' +
                ", date='" + getDate() + '\'' +
                ", total=" + getTotal() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
