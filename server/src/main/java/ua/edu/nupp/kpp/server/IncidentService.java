package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class IncidentService {

    private static final Logger LOG = Logger.getLogger(IncidentService.class.getName());
    private final IncidentDAO dao = new IncidentDAO();
    private final ChargingModuleDAO moduleDAO = new ChargingModuleDAO();

    public List<Incident> getAll() throws SQLException {
        return dao.getAll();
    }

    public Incident getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено інцидент");
    }

    private Incident fromChargingModule(Long id, long moduleId, IncidentState state) throws SQLException {
        ChargingModule m = moduleDAO.getById(moduleId);
        if (m == null) {
            throw new IllegalArgumentException("Зарядний модуль не знайдено");
        }

        double maxLoad = m.getPower();

        double currentLoad = maxLoad * 0.85;

        double f1 = IncidentCalculator.factor1(currentLoad, maxLoad);

        double f2 = 75.0;
        double f3 = 40.0;

        double priorityIndex = IncidentCalculator.calculate(f1, f2, f3);

        return new Incident(id, moduleId, state, f1, f2, f3, priorityIndex);
    }

    public void add(long moduleId) throws SQLException {
        Incident incident = fromChargingModule(null, moduleId, IncidentState.RECORDED);
        dao.add(incident);
        LOG.info("Додано новий інцидент");
    }

    public void update(long id, long moduleId) throws SQLException {
        Incident old = dao.getById(id);
        if (old == null) {
            throw new IllegalArgumentException("Інцидент не знайдено");
        }
        Incident updated = fromChargingModule(id, moduleId, old.getState());
        dao.update(updated);
        LOG.info("Оновлено інцидент з id: " + id);
    }

    public static boolean isAllowedTransition(IncidentState from, IncidentState to) {
        return (from == IncidentState.RECORDED && to == IncidentState.DIAGNOSED)
                || (from == IncidentState.DIAGNOSED && to == IncidentState.REPAIR)
                || (from == IncidentState.REPAIR && to == IncidentState.RESTORED);
    }

    public void changeState(long id, IncidentState next) throws SQLException {
        Incident current = dao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Інцидент не знайдено");
        }
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану інциденту ID " + id + ": " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану");
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан інциденту ID " + id + " на " + next);
    }
}