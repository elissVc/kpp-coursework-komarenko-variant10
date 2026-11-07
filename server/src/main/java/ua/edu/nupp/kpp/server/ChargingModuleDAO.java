package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChargingModuleDAO {

    public void add(ChargingModule m) throws SQLException {
        String sql = "INSERT INTO charging_module (station_id, connector_type, power, is_fast_charge) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, m);
            ps.executeUpdate();
        }
    }

    public List<ChargingModule> getAll() throws SQLException {
        String sql = "SELECT id, station_id, connector_type, power, is_fast_charge FROM charging_module ORDER BY id";
        List<ChargingModule> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public ChargingModule getById(long id) throws SQLException {
        String sql = "SELECT id, station_id, connector_type, power, is_fast_charge FROM charging_module WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void update(ChargingModule m) throws SQLException {
        String sql = "UPDATE charging_module SET station_id = ?, connector_type = ?, power = ?, is_fast_charge = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, m);
            ps.setLong(5, m.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM charging_module WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, ChargingModule m) throws SQLException {
        ps.setLong(1, m.getStationId());
        ps.setString(2, m.getConnectorType());
        ps.setDouble(3, m.getPower());
        ps.setBoolean(4, m.isFastCharge());
    }

    private ChargingModule map(ResultSet rs) throws SQLException {
        return new ChargingModule(
                rs.getLong("id"),
                rs.getLong("station_id"),
                rs.getString("connector_type"),
                rs.getDouble("power"),
                rs.getBoolean("is_fast_charge")
        );
    }
}
