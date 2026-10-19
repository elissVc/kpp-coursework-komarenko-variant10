package ua.edu.nupp.kpp.server;

public class Station {
    private Long id;
    private String address;
    private double powerCapacity;
    private String status;

    public Station() {}

    public Station(Long id, String address, double powerCapacity, String status) {
        this.id = id;
        this.address = address;
        this.powerCapacity = powerCapacity;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getPowerCapacity() { return powerCapacity; }
    public void setPowerCapacity(double powerCapacity) { this.powerCapacity = powerCapacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}