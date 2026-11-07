package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {

    public void add(Incident incident) throws SQLException {
        String sql = "INSERT INTO incident (module_id, state, f1, f2, f3, risk_index) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, incident);
            ps.executeUpdate();
        }
    }

    public List<Incident> getAll() throws SQLException {
        String sql = "SELECT id, module_id, state, f1, f2, f3, risk_index FROM incident ORDER BY id";
        List<Incident> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Incident getById(long id) throws SQLException {
        String sql = "SELECT id, module_id, state, f1, f2, f3, risk_index FROM incident WHERE id = ?";
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

    public void update(Incident incident) throws SQLException {
        String sql = "UPDATE incident SET module_id = ?, state = ?, f1 = ?, f2 = ?, f3 = ?, risk_index = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, incident);
            ps.setLong(7, incident.getId());
            ps.executeUpdate();
        }
    }

    public void updateState(long id, IncidentState state) throws SQLException {
        String sql = "UPDATE incident SET state = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, state.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM incident WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Incident i) throws SQLException {
        ps.setLong(1, i.getModuleId());
        ps.setString(2, i.getState().name());
        ps.setDouble(3, i.getF1());
        ps.setDouble(4, i.getF2());
        ps.setDouble(5, i.getF3());
        ps.setDouble(6, i.getRiskIndex());
    }

    private Incident map(ResultSet rs) throws SQLException {
        return new Incident(
                rs.getLong("id"),
                rs.getLong("module_id"),
                IncidentState.valueOf(rs.getString("state")),
                rs.getDouble("f1"),
                rs.getDouble("f2"),
                rs.getDouble("f3"),
                rs.getDouble("risk_index")
        );
    }
}
