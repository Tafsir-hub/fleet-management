CREATE DATABASE IF NOT EXISTS fleet_management;
USE fleet_management;

CREATE TABLE `vehicles` (
  `vehicle_id` int NOT NULL AUTO_INCREMENT,
  `make_model` varchar(100) NOT NULL,
  `year` int NOT NULL,
  `vehicle_type` varchar(50) DEFAULT NULL,
  `mileage` int DEFAULT '0',
  `status` varchar(20) DEFAULT 'ACTIVE',
  PRIMARY KEY (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `drivers` (
  `driver_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `license_type` varchar(30) NOT NULL,
  `hire_date` date NOT NULL,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`driver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(30) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `trips` (
  `trip_id` int NOT NULL AUTO_INCREMENT,
  `vehicle_id` int NOT NULL,
  `driver_id` int NOT NULL,
  `trip_date` date NOT NULL,
  `origin` varchar(100) NOT NULL,
  `destination` varchar(100) NOT NULL,
  `miles` decimal(8,1) NOT NULL,
  PRIMARY KEY (`trip_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `fuel` (
  `fuel_id` int NOT NULL AUTO_INCREMENT,
  `vehicle_id` int NOT NULL,
  `fuel_date` date NOT NULL,
  `gallons` decimal(6,2) NOT NULL,
  `price_per_gallon` decimal(5,3) NOT NULL,
  `total_cost` decimal(8,2) NOT NULL,
  PRIMARY KEY (`fuel_id`),
  KEY `vehicle_id_idx` (`vehicle_id`),
  CONSTRAINT `vehicle_id` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `maintenance` (
  `maint_id` int NOT NULL AUTO_INCREMENT,
  `vehicle_id` int NOT NULL,
  `service_date` date NOT NULL,
  `service_type` varchar(60) NOT NULL,
  `cost` decimal(8,2) NOT NULL,
  `mileage` int NOT NULL,
  PRIMARY KEY (`maint_id`),
  KEY `vehicle_id_idx` (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE OR REPLACE VIEW v_fleet_summary AS
SELECT v.vehicle_id,
       CONCAT('V-', LPAD(v.vehicle_id, 3, '0'))             AS vehicle_code,
       v.make_model,
       v.vehicle_type,
       v.status,
       v.mileage,
       COALESCE(f.fuel_cost, 0)                              AS fuel_cost,
       COALESCE(m.maintenance_cost, 0)                       AS maintenance_cost,
       COALESCE(f.fuel_cost, 0) + COALESCE(m.maintenance_cost, 0) AS total_cost,
       COALESCE(t.trip_miles, 0)                             AS trip_miles,
       COALESCE(f.gallons, 0)                                AS gallons,
       ROUND(t.trip_miles / NULLIF(f.gallons, 0), 1)         AS mpg
FROM vehicles v
LEFT JOIN (SELECT vehicle_id, SUM(total_cost) AS fuel_cost, SUM(gallons) AS gallons
           FROM fuel GROUP BY vehicle_id) f ON f.vehicle_id = v.vehicle_id
LEFT JOIN (SELECT vehicle_id, SUM(cost) AS maintenance_cost
           FROM maintenance GROUP BY vehicle_id) m ON m.vehicle_id = v.vehicle_id
LEFT JOIN (SELECT vehicle_id, SUM(miles) AS trip_miles
           FROM trips GROUP BY vehicle_id) t ON t.vehicle_id = v.vehicle_id;

CREATE OR REPLACE VIEW v_fleet_activity AS
SELECT e.event_type, e.event_date, e.vehicle_id,
       CONCAT('V-', LPAD(e.vehicle_id, 3, '0')) AS vehicle_code,
       v.make_model, v.vehicle_type, v.status,
       e.driver_name, e.service_type,
       e.gallons, e.fuel_cost, e.maintenance_cost, e.trip_miles, e.trips
FROM (
    SELECT 'Fuel' AS event_type, fuel_date AS event_date, vehicle_id,
           CAST(NULL AS CHAR(100)) AS driver_name, CAST(NULL AS CHAR(60)) AS service_type,
           gallons, total_cost AS fuel_cost, 0 AS maintenance_cost, 0 AS trip_miles, 0 AS trips
    FROM fuel
    UNION ALL
    SELECT 'Maintenance', service_date, vehicle_id, NULL, service_type, 0, 0, cost, 0, 0
    FROM maintenance
    UNION ALL
    SELECT 'Trip', t.trip_date, t.vehicle_id, CONCAT(d.first_name, ' ', d.last_name), NULL, 0, 0, 0, t.miles, 1
    FROM trips t JOIN drivers d ON d.driver_id = t.driver_id
) e
JOIN vehicles v ON v.vehicle_id = e.vehicle_id;


