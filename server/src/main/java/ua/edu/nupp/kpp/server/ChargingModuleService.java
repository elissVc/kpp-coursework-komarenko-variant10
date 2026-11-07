package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ChargingModuleService {
    private static final Logger LOG = Logger.getLogger(ChargingModuleService.class.getName());
    private final ChargingModuleDAO dao = new ChargingModuleDAO();
    private final StationDAO stationDAO = new StationDAO();

    public List<ChargingModule> getAll() throws SQLException {
        return dao.getAll();
    }

    public ChargingModule getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(ChargingModule m) throws SQLException {
        validate(m);
        requireStation(m.getStationId());
        dao.add(m);
        LOG.info("Додано зарядний модуль");
    }

    public void update(ChargingModule m) throws SQLException {
        validate(m);
        requireStation(m.getStationId());
        dao.update(m);
        LOG.info("Оновлено зарядний модуль");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено зарядний модуль");
    }

    static void validate(ChargingModule m) {
        if (m == null || m.getStationId() <= 0) {
            throw new IllegalArgumentException("Некоректні основні дані зарядного модуля");
        }
    }

    private void requireStation(long id) throws SQLException {
        if (stationDAO.getById(id) == null) {
            throw new IllegalArgumentException("Станцію не знайдено");
        }
    }
}
