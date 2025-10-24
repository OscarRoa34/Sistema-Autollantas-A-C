package co.edu.uptc.models.registers;

public abstract class Register {
    private String invoiceNumber;
    private String date;
    private double total;
    private String status;

    public Register(String invoiceNumber, String date, double total, String status) {
        this.invoiceNumber = invoiceNumber;
        this.date = date;
        this.total = total;
        this.status = status;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Register{" +
                "invoiceNumber='" + invoiceNumber + '\'' +
                ", date='" + date + '\'' +
                ", total=" + total +
                ", status='" + status + '\'' +
                '}';
    }
}