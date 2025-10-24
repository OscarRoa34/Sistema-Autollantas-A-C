package co.edu.uptc.models.registers;

public class SellingRegister extends Register {
    private String customerName;

    public SellingRegister(String invoiceNumber, String date, double total, String status, String customerName) {
        super(invoiceNumber, date, total, status);
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return "SellingRegister{" +
                "customerName='" + customerName + '\'' +
                ", invoiceNumber='" + getInvoiceNumber() + '\'' +
                ", date='" + getDate() + '\'' +
                ", total=" + getTotal() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
