DROP DATABASE IF EXISTS ev_charging;
CREATE DATABASE ev_charging CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ev_charging;

CREATE TABLE station (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         address VARCHAR(200) NOT NULL,
                         power_capacity DOUBLE NOT NULL,
                         status VARCHAR(50) NOT NULL
);

CREATE TABLE charging_module (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 station_id BIGINT NOT NULL,
                                 connector_type VARCHAR(50) NOT NULL,
                                 power DOUBLE NOT NULL,
                                 is_fast_charge BOOLEAN NOT NULL,
                                 FOREIGN KEY (station_id) REFERENCES station(id)
);

CREATE TABLE incident (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          module_id BIGINT NOT NULL,
                          state VARCHAR(30) NOT NULL,
                          f1 DOUBLE NOT NULL,
                          f2 DOUBLE NOT NULL,
                          f3 DOUBLE NOT NULL,
                          risk_index DOUBLE NOT NULL,
                          FOREIGN KEY (module_id) REFERENCES charging_module(id)
);

CREATE TABLE repair_task (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             incident_id BIGINT NOT NULL,
                             description VARCHAR(500) NOT NULL,
                             planned_date VARCHAR(10),
                             FOREIGN KEY (incident_id) REFERENCES incident(id)
);

INSERT INTO station (address, power_capacity, status) VALUES
                                                          ('м. Полтава, вул. Європейська, 10', 150.0, 'Активна'),
                                                          ('м. Полтава, вул. Зіньківська, 6', 50.0, 'Активна');

INSERT INTO charging_module (station_id, connector_type, power, is_fast_charge) VALUES
                                                                                    (1, 'Type 2', 22.0, FALSE),
                                                                                    (1, 'CCS2', 120.0, TRUE),
                                                                                    (2, 'CHAdeMO', 50.0, TRUE);