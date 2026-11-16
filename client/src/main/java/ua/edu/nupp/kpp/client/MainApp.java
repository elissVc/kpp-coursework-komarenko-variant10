package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpEvChargingClient api = new HttpEvChargingClient();

    private final TableView<Station> stations = new TableView<>();
    private final TableView<ChargingModule> modules = new TableView<>();
    private final TableView<Incident> incidents = new TableView<>();
    private final TableView<RepairTask> tasks = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Станції", stationPane()),
                new Tab("Зарядні модулі", modulePane()),
                new Tab("Інциденти", incidentPane()),
                new Tab("Ремонтні завдання", taskPane())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));

        stage.setScene(new Scene(tabs, 1080, 650));
        stage.setTitle("Обслуговування зарядних станцій електромобілів (Клієнт)");
        stage.show();
        refreshAll();
    }

    private Pane stationPane() {
        stations.getColumns().setAll(
                col("ID", "id"),
                col("Адреса", "address"),
                col("Потужність (кВт)", "powerCapacity"),
                col("Статус", "status")
        );

        TextField address = new TextField();
        TextField powerCapacity = new TextField();
        TextField status = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addStation(new Station(
                    null,
                    address.getText(),
                    Double.parseDouble(powerCapacity.getText().trim()),
                    status.getText()
            ));
            refreshStations();
        }));

        update.setOnAction(e -> safe(() -> {
            Station s = selected(stations);
            s.setAddress(address.getText());
            s.setPowerCapacity(Double.parseDouble(powerCapacity.getText().trim()));
            s.setStatus(status.getText());
            api.updateStation(s);
            refreshStations();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteStation(selected(stations).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshStations());

        stations.getSelectionModel().selectedItemProperty().addListener((o, oldVal, s) -> {
            if (s != null) {
                address.setText(s.getAddress());
                powerCapacity.setText(String.valueOf(s.getPowerCapacity()));
                status.setText(s.getStatus());
            }
        });

        VBox form = new VBox(8,
                new Label("Адреса"), address,
                new Label("Потужність (кВт)"), powerCapacity,
                new Label("Статус"), status,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(stations, form);
    }

    private Pane modulePane() {
        modules.getColumns().setAll(
                col("ID", "id"),
                col("ID станції", "stationId"),
                col("Тип роз'єму", "connectorType"),
                col("Потужність (кВт)", "power"),
                col("Швидка зарядка", "fastCharge")
        );

        TextField stationId = new TextField();
        TextField connectorType = new TextField();
        TextField power = new TextField();
        CheckBox fastCharge = new CheckBox("Швидка зарядка (Fast Charge)");

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addModule(readModule(null, stationId, connectorType, power, fastCharge));
            refreshModules();
        }));

        update.setOnAction(e -> safe(() -> {
            ChargingModule m = selected(modules);
            api.updateModule(readModule(m.getId(), stationId, connectorType, power, fastCharge));
            refreshModules();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteModule(selected(modules).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshModules());

        modules.getSelectionModel().selectedItemProperty().addListener((o, oldVal, m) -> {
            if (m != null) {
                stationId.setText(String.valueOf(m.getStationId()));
                connectorType.setText(m.getConnectorType());
                power.setText(String.valueOf(m.getPower()));
                fastCharge.setSelected(m.isFastCharge());
            }
        });

        VBox form = new VBox(8,
                new Label("ID станції"), stationId,
                new Label("Тип роз'єму"), connectorType,
                new Label("Потужність (кВт)"), power,
                fastCharge,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(modules, form);
    }

    private Pane incidentPane() {
        incidents.getColumns().setAll(
                col("ID", "id"),
                col("ID модуля", "moduleId"),
                col("Стан", "state"),
                col("F1", "f1"),
                col("F2", "f2"),
                col("F3", "f3"),
                col("Інтегральний показник (I)", "riskIndex")
        );

        TextField moduleId = new TextField();
        ComboBox<IncidentState> state = new ComboBox<>(FXCollections.observableArrayList(IncidentState.values()));
        state.setMaxWidth(Double.MAX_VALUE);

        Button add = new Button("Створити");
        Button update = new Button("Перерахувати");
        Button change = new Button("Змінити стан");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addIncident(Long.parseLong(moduleId.getText().trim()));
            refreshIncidents();
        }));

        update.setOnAction(e -> safe(() -> {
            api.updateIncident(selected(incidents).getId(), Long.parseLong(moduleId.getText().trim()));
            refreshIncidents();
        }));

        change.setOnAction(e -> safe(() -> {
            if (state.getValue() == null) {
                throw new IllegalArgumentException("Виберіть новий стан");
            }
            api.changeIncidentState(selected(incidents).getId(), state.getValue());
            refreshIncidents();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteIncident(selected(incidents).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshIncidents());

        incidents.getSelectionModel().selectedItemProperty().addListener((o, oldVal, v) -> {
            if (v != null) {
                moduleId.setText(String.valueOf(v.getModuleId()));
                state.setValue(v.getState());
            }
        });

        VBox form = new VBox(8,
                new Label("ID зарядного модуля"), moduleId,
                new Label("Новий стан"), state,
                new HBox(8, add, update, change, delete, refresh)
        );
        return layout(incidents, form);
    }

    private Pane taskPane() {
        tasks.getColumns().setAll(
                col("ID", "id"),
                col("ID інциденту", "incidentId"),
                col("Опис", "description"),
                col("Планова дата", "plannedDate")
        );

        TextField incidentId = new TextField();
        TextField description = new TextField();
        TextField date = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addTask(new RepairTask(
                    null,
                    Long.parseLong(incidentId.getText().trim()),
                    description.getText(),
                    date.getText()
            ));
            refreshTasks();
        }));

        update.setOnAction(e -> safe(() -> {
            RepairTask t = selected(tasks);
            api.updateTask(new RepairTask(
                    t.getId(),
                    Long.parseLong(incidentId.getText().trim()),
                    description.getText(),
                    date.getText()
            ));
            refreshTasks();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteTask(selected(tasks).getId());
            refreshTasks();
        }));

        refresh.setOnAction(e -> refreshTasks());

        tasks.getSelectionModel().selectedItemProperty().addListener((o, oldVal, t) -> {
            if (t != null) {
                incidentId.setText(String.valueOf(t.getIncidentId()));
                description.setText(t.getDescription());
                date.setText(t.getPlannedDate());
            }
        });

        VBox form = new VBox(8,
                new Label("ID інциденту"), incidentId,
                new Label("Опис завдання"), description,
                new Label("Дата (YYYY-MM-DD)"), date,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(tasks, form);
    }

    private ChargingModule readModule(Long id, TextField stationId, TextField connectorType, TextField power, CheckBox fastCharge) {
        return new ChargingModule(
                id,
                Long.parseLong(stationId.getText().trim()),
                connectorType.getText(),
                Double.parseDouble(power.getText().trim()),
                fastCharge.isSelected()
        );
    }

    private void refreshAll() {
        refreshStations();
        refreshModules();
        refreshIncidents();
        refreshTasks();
    }

    private void refreshStations() {
        safe(() -> stations.setItems(FXCollections.observableArrayList(api.getStations())));
    }

    private void refreshModules() {
        safe(() -> modules.setItems(FXCollections.observableArrayList(api.getModules())));
    }

    private void refreshIncidents() {
        safe(() -> incidents.setItems(FXCollections.observableArrayList(api.getIncidents())));
    }

    private void refreshTasks() {
        safe(() -> tasks.setItems(FXCollections.observableArrayList(api.getTasks())));
    }

    private <T> T selected(TableView<T> table) {
        T v = table.getSelectionModel().getSelectedItem();
        if (v == null) {
            throw new IllegalArgumentException("Виберіть запис у таблиці");
        }
        return v;
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Throwable e) {
        Alert d = new Alert(Alert.AlertType.ERROR, e == null ? "Помилка" : e.getMessage(), ButtonType.OK);
        d.setHeaderText("Помилка виконання операції");
        d.showAndWait();
    }

    private Pane layout(TableView<?> table, Pane form) {
        SplitPane p = new SplitPane(table, form);
        p.setDividerPositions(0.68);
        BorderPane root = new BorderPane(p);
        root.setPadding(new Insets(10));
        return root;
    }

    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }

    public static void main(String[] args) {
        launch(args);
    }
    private void readAndSendTestCsv() {
        try {
            Path file = Path.of("sample-data", "stations.csv");
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(";", -1);

                Station s = new Station(
                        null,
                        p[0].trim(),
                        Double.parseDouble(p[1].trim()),
                        p[2].trim()
                );
                api.addStation(s);

                List<Station> allStations = api.getStations();
                long newStationId = allStations.get(allStations.size() - 1).getId();

                ChargingModule m = new ChargingModule(
                        null,
                        newStationId,
                        p[3].trim(),
                        Double.parseDouble(p[4].trim()),
                        Boolean.parseBoolean(p[5].trim())
                );
                api.addModule(m);
            }
            refreshAll();
            LOG.info("Початковий імпорт stations.csv виконано!");
        } catch (Exception e) {
            showError(e);
        }
    }
}