package ua.edu.nupp.kpp.server;

public class ChargingModule {
    private Long id;
    private long stationId;
    private String connectorType;
    private double power;
    private boolean fastCharge;

    public ChargingModule() {}

    public ChargingModule(Long id, long stationId, String connectorType, double power, boolean fastCharge) {
        this.id = id;
        this.stationId = stationId;
        this.connectorType = connectorType;
        this.power = power;
        this.fastCharge = fastCharge;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getStationId() { return stationId; }
    public void setStationId(long stationId) { this.stationId = stationId; }
    public String getConnectorType() { return connectorType; }
    public void setConnectorType(String connectorType) { this.connectorType = connectorType; }
    public double getPower() { return power; }
    public void setPower(double power) { this.power = power; }
    public boolean isFastCharge() { return fastCharge; }
    public void setFastCharge(boolean fastCharge) { this.fastCharge = fastCharge; }
}
