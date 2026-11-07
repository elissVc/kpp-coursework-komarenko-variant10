package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StationDAO {

    public void add(Station station) throws SQLException {
        String sql = "INSERT INTO station (address, power_capacity, status) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, station.getAddress());
            ps.setDouble(2, station.getPowerCapacity());
            ps.setString(3, station.getStatus());
            ps.executeUpdate();
        }
    }

    public List<Station> getAll() throws SQLException {
        String sql = "SELECT id, address, power_capacity, status FROM station ORDER BY id";
        List<Station> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Station(
                        rs.getLong("id"),
                        rs.getString("address"),
                        rs.getDouble("power_capacity"),
                        rs.getString("status")
                ));
            }
        }
        return list;
    }

    public Station getById(long id) throws SQLException {
        String sql = "SELECT id, address, power_capacity, status FROM station WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Station(
                            rs.getLong("id"),
                            rs.getString("address"),
                            rs.getDouble("power_capacity"),
                            rs.getString("status")
                    );
                }
            }
        }
        return null;
    }

    public void update(Station station) throws SQLException {
        String sql = "UPDATE station SET address = ?, power_capacity = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, station.getAddress());
            ps.setDouble(2, station.getPowerCapacity());
            ps.setString(3, station.getStatus());
            ps.setLong(4, station.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM station WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}
