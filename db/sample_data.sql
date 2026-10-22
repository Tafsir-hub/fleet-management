-- FMAS sample data for the fleet_management database (cool cars edition)
-- Run as root in MySQL Workbench (Local Fleet DB connection).
-- Re-running this script resets vehicles, drivers, trips, fuel and maintenance
-- back to this sample data. The users table is not touched: the login account
-- is created later, when login is built (slice 2).

USE fleet_management;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE maintenance;
TRUNCATE TABLE fuel;
TRUNCATE TABLE trips;
TRUNCATE TABLE drivers;
TRUNCATE TABLE vehicles;
SET FOREIGN_KEY_CHECKS = 1;

-- Drivers (2 are INACTIVE, for the 'inactive driver' test)
INSERT INTO drivers (driver_id, name, license_type, hire_date, status) VALUES
(1, 'J. Alvarez', 'CDL-A', '2019-03-11', 'ACTIVE'),
(2, 'M. Chen', 'CDL-B', '2020-06-22', 'ACTIVE'),
(3, 'S. Patel', 'Class C', '2021-01-18', 'ACTIVE'),
(4, 'D. Johnson', 'CDL-A', '2018-09-04', 'ACTIVE'),
(5, 'R. Okafor', 'Class C', '2022-04-25', 'ACTIVE'),
(6, 'L. Martinez', 'CDL-B', '2019-11-30', 'ACTIVE'),
(7, 'K. Williams', 'CDL-A', '2017-05-15', 'ACTIVE'),
(8, 'T. Nguyen', 'Class C', '2023-02-06', 'ACTIVE'),
(9, 'A. Brown', 'CDL-B', '2020-08-17', 'INACTIVE'),
(10, 'P. Rossi', 'Class C', '2021-10-12', 'INACTIVE');

-- Vehicles (7 ACTIVE, 2 IN_REPAIR, 1 RETIRED)
INSERT INTO vehicles (vehicle_id, make_model, year, vehicle_type, mileage, status) VALUES
(1, 'Mercedes-Benz Sprinter 4x4', 2022, 'Van', 34800, 'ACTIVE'),
(2, 'Ford Transit Trail', 2021, 'Van', 58900, 'ACTIVE'),
(3, 'Peterbilt 389', 2018, 'Semi Tractor', 163400, 'IN_REPAIR'),
(4, 'Mercedes-Benz Metris', 2022, 'Van', 11050, 'ACTIVE'),
(5, 'Ford F-150 Raptor', 2022, 'Pickup', 27400, 'ACTIVE'),
(6, 'Jeep Wrangler Rubicon 392', 2022, 'SUV', 29700, 'ACTIVE'),
(7, 'Chevrolet Corvette Stingray', 2022, 'Sports Car', 12900, 'ACTIVE'),
(8, 'Kenworth W900', 2017, 'Semi Tractor', 188400, 'ACTIVE'),
(9, 'Ram 2500 Power Wagon', 2020, 'Pickup', 68900, 'IN_REPAIR'),
(10, 'Nissan GT-R', 2015, 'Sports Car', 68200, 'RETIRED');

-- Trips (May to mid-October 2026)
INSERT INTO trips (vehicle_id, driver_id, trip_date, origin, destination, miles) VALUES
(6, 3, '2026-05-04', 'Newark, NJ', 'Baltimore, MD', 199.5),
(1, 4, '2026-05-18', 'Newark, NJ', 'Harrisburg, PA', 160.5),
(4, 5, '2026-05-19', 'Newark, NJ', 'Washington, DC', 228.4),
(6, 10, '2026-05-19', 'Newark, NJ', 'Philadelphia, PA', 91.3),
(4, 8, '2026-05-20', 'Newark, NJ', 'Boston, MA', 222.7),
(4, 6, '2026-05-21', 'Newark, NJ', 'Hartford, CT', 114.6),
(6, 10, '2026-05-21', 'Elizabeth, NJ', 'Wilmington, DE', 104.6),
(1, 7, '2026-05-22', 'Newark, NJ', 'Hartford, CT', 115.0),
(4, 6, '2026-05-27', 'Newark, NJ', 'Albany, NY', 147.3),
(7, 5, '2026-05-27', 'Newark, NJ', 'Atlantic City, NJ', 125.8),
(1, 9, '2026-05-28', 'Jersey City, NJ', 'Stamford, CT', 42.0),
(9, 7, '2026-05-29', 'Edison, NJ', 'Allentown, PA', 80.0),
(6, 5, '2026-06-01', 'Newark, NJ', 'Boston, MA', 233.3),
(5, 7, '2026-06-08', 'Newark, NJ', 'Philadelphia, PA', 97.5),
(3, 1, '2026-06-09', 'Newark, NJ', 'Boston, MA', 220.1),
(7, 6, '2026-06-10', 'Newark, NJ', 'Watkins Glen, NY', 230.4),
(3, 1, '2026-06-16', 'Newark, NJ', 'Pittsburgh, PA', 378.5),
(2, 9, '2026-06-19', 'Jersey City, NJ', 'Stamford, CT', 42.8),
(8, 7, '2026-06-22', 'Newark, NJ', 'Richmond, VA', 358.5),
(7, 9, '2026-06-25', 'Jersey City, NJ', 'Newport, RI', 194.7),
(6, 6, '2026-06-26', 'Newark, NJ', 'Baltimore, MD', 198.6),
(3, 4, '2026-06-30', 'Newark, NJ', 'Baltimore, MD', 197.9),
(3, 1, '2026-07-06', 'Newark, NJ', 'Baltimore, MD', 202.7),
(8, 1, '2026-07-09', 'Elizabeth, NJ', 'Syracuse, NY', 246.4),
(5, 2, '2026-07-14', 'Edison, NJ', 'Allentown, PA', 76.6),
(5, 4, '2026-07-14', 'Elizabeth, NJ', 'Wilmington, DE', 101.9),
(8, 4, '2026-07-14', 'Newark, NJ', 'Boston, MA', 221.3),
(6, 8, '2026-07-30', 'Jersey City, NJ', 'Stamford, CT', 43.7),
(8, 4, '2026-07-31', 'Newark, NJ', 'Boston, MA', 228.1),
(9, 2, '2026-08-03', 'Newark, NJ', 'Philadelphia, PA', 96.4),
(7, 4, '2026-08-04', 'Elizabeth, NJ', 'Pocono Raceway, PA', 101.3),
(1, 2, '2026-08-05', 'Newark, NJ', 'Baltimore, MD', 205.2),
(3, 4, '2026-08-05', 'Newark, NJ', 'Boston, MA', 223.6),
(2, 1, '2026-08-07', 'Newark, NJ', 'Baltimore, MD', 200.6),
(9, 7, '2026-08-12', 'Newark, NJ', 'Washington, DC', 225.5),
(1, 1, '2026-08-13', 'Newark, NJ', 'Boston, MA', 227.3),
(5, 3, '2026-08-14', 'Newark, NJ', 'Baltimore, MD', 195.7),
(5, 3, '2026-08-18', 'Newark, NJ', 'Trenton, NJ', 55.0),
(6, 8, '2026-08-24', 'Newark, NJ', 'Albany, NY', 150.6),
(9, 2, '2026-08-25', 'Newark, NJ', 'Washington, DC', 229.7);

INSERT INTO trips (vehicle_id, driver_id, trip_date, origin, destination, miles) VALUES
(4, 8, '2026-08-28', 'Jersey City, NJ', 'Stamford, CT', 40.9),
(8, 1, '2026-09-01', 'Newark, NJ', 'Boston, MA', 225.8),
(4, 5, '2026-09-02', 'Elizabeth, NJ', 'Syracuse, NY', 244.8),
(7, 4, '2026-09-16', 'Newark, NJ', 'Philadelphia, PA', 96.5),
(7, 7, '2026-09-16', 'Newark, NJ', 'Philadelphia, PA', 96.0),
(1, 1, '2026-09-18', 'Newark, NJ', 'Hartford, CT', 122.5),
(5, 6, '2026-09-21', 'Newark, NJ', 'Philadelphia, PA', 97.5),
(5, 1, '2026-09-21', 'Newark, NJ', 'Washington, DC', 225.4),
(2, 7, '2026-09-24', 'Newark, NJ', 'Albany, NY', 153.7),
(2, 8, '2026-09-29', 'Newark, NJ', 'Albany, NY', 149.3),
(1, 3, '2026-09-30', 'Newark, NJ', 'Trenton, NJ', 54.5),
(8, 4, '2026-10-01', 'Newark, NJ', 'Baltimore, MD', 203.1),
(2, 3, '2026-10-05', 'Elizabeth, NJ', 'Wilmington, DE', 107.0),
(8, 7, '2026-10-05', 'Newark, NJ', 'Harrisburg, PA', 161.4),
(6, 3, '2026-10-07', 'Jersey City, NJ', 'Stamford, CT', 41.0),
(5, 7, '2026-10-08', 'Elizabeth, NJ', 'Syracuse, NY', 249.5),
(7, 4, '2026-10-08', 'Newark, NJ', 'Watkins Glen, NY', 226.4),
(4, 7, '2026-10-13', 'Elizabeth, NJ', 'Wilmington, DE', 104.7),
(2, 2, '2026-10-14', 'Elizabeth, NJ', 'Wilmington, DE', 106.1);

-- Fuel purchases (total_cost = gallons x price per gallon)
INSERT INTO fuel (vehicle_id, fuel_date, gallons, price_per_gallon, total_cost) VALUES
(6, '2026-05-04', 14.45, 3.705, 53.54),
(1, '2026-05-18', 9.81, 3.780, 37.08),
(4, '2026-05-19', 12.11, 3.715, 44.99),
(4, '2026-05-21', 11.63, 3.608, 41.96),
(6, '2026-05-21', 14.47, 3.764, 54.47),
(4, '2026-05-28', 14.23, 3.934, 55.98),
(1, '2026-05-29', 9.43, 3.951, 37.26),
(9, '2026-05-29', 7.23, 3.746, 27.08),
(6, '2026-06-01', 16.46, 3.748, 61.69),
(3, '2026-06-10', 36.38, 4.182, 152.14),
(7, '2026-06-11', 17.23, 4.459, 76.83),
(3, '2026-06-16', 61.22, 4.452, 272.55),
(8, '2026-06-22', 67.71, 4.296, 290.88),
(6, '2026-06-26', 14.95, 3.953, 59.10),
(7, '2026-06-26', 9.43, 4.335, 40.88),
(3, '2026-07-01', 34.55, 4.408, 152.30),
(3, '2026-07-06', 34.78, 4.569, 158.91),
(8, '2026-07-10', 44.40, 4.364, 193.76),
(5, '2026-07-14', 11.38, 3.708, 42.20),
(8, '2026-07-14', 37.39, 4.412, 164.96),
(8, '2026-07-31', 40.71, 4.295, 174.85),
(9, '2026-08-04', 8.45, 3.880, 32.79),
(3, '2026-08-05', 39.61, 4.520, 179.04),
(1, '2026-08-06', 11.96, 3.761, 44.98),
(2, '2026-08-07', 16.45, 3.760, 61.85),
(9, '2026-08-12', 19.36, 3.947, 76.41),
(1, '2026-08-14', 14.64, 3.806, 55.72),
(5, '2026-08-14', 19.47, 3.933, 76.58),
(6, '2026-08-24', 13.00, 3.640, 47.32),
(9, '2026-08-25', 19.74, 3.671, 72.47),
(4, '2026-09-02', 14.82, 3.926, 58.18),
(8, '2026-09-02', 42.29, 4.314, 182.44),
(7, '2026-09-17', 9.38, 4.142, 38.85),
(1, '2026-09-18', 7.46, 3.954, 29.50),
(5, '2026-09-21', 9.81, 3.946, 38.71),
(5, '2026-09-21', 15.46, 3.909, 60.43),
(2, '2026-09-25', 9.43, 3.676, 34.66),
(1, '2026-09-30', 3.45, 3.631, 12.53),
(2, '2026-09-30', 9.83, 3.705, 36.42),
(8, '2026-10-02', 34.37, 4.476, 153.84);

INSERT INTO fuel (vehicle_id, fuel_date, gallons, price_per_gallon, total_cost) VALUES
(8, '2026-10-05', 29.71, 4.196, 124.66),
(5, '2026-10-08', 17.00, 3.591, 61.05),
(7, '2026-10-08', 15.16, 4.451, 67.48),
(4, '2026-10-13', 5.21, 3.580, 18.65),
(2, '2026-10-14', 13.59, 3.745, 50.89);

-- Maintenance records
INSERT INTO maintenance (vehicle_id, service_date, service_type, cost, mileage) VALUES
(10, '2024-10-14', 'Coolant flush', 388.49, 60241),
(8, '2024-12-06', 'Coolant flush', 343.39, 164120),
(10, '2025-03-22', 'Air filter replacement', 83.56, 65695),
(8, '2025-04-21', 'Brake inspection', 134.43, 169601),
(3, '2025-07-17', 'Tire rotation', 108.48, 149696),
(8, '2025-09-26', 'Air filter replacement', 148.73, 175616),
(7, '2025-10-20', 'Coolant flush', 333.36, 370),
(1, '2025-11-09', 'Oil change', 134.89, 23617),
(2, '2025-11-19', 'Oil change', 112.31, 48973),
(5, '2025-11-26', 'Annual inspection', 157.38, 12850),
(3, '2025-12-06', 'Tire rotation', 101.40, 154198),
(9, '2026-01-15', 'Annual inspection', 106.34, 57453),
(8, '2026-02-03', 'Oil change', 171.91, 180659),
(1, '2026-03-09', 'Annual inspection', 169.79, 28999),
(7, '2026-03-27', 'Air filter replacement', 100.37, 4876),
(4, '2026-04-06', 'Tire rotation', 55.48, 5437),
(2, '2026-04-25', 'Oil change', 98.90, 54023),
(6, '2026-05-01', 'Oil change', 132.21, 20957),
(5, '2026-05-10', 'Oil change', 147.65, 19176),
(9, '2026-06-02', 'Oil change', 133.87, 63372),
(1, '2026-06-26', 'Tire replacement', 998.93, 33528),
(3, '2026-08-20', 'Brake system overhaul', 1925.00, 163377),
(3, '2026-08-22', 'Engine repair', 4780.50, 163271),
(4, '2026-08-22', 'Oil change', 88.13, 9983),
(9, '2026-09-04', 'Engine repair', 2576.28, 68723),
(5, '2026-09-05', 'Air filter replacement', 67.18, 24257),
(6, '2026-09-07', 'Annual inspection', 256.34, 26855),
(7, '2026-09-11', 'Oil change', 198.12, 10658);

-- Quick check: this query prints the row count of each table
SELECT 'vehicles' AS table_name, COUNT(*) AS row_count FROM vehicles
UNION ALL SELECT 'drivers', COUNT(*) FROM drivers
UNION ALL SELECT 'trips', COUNT(*) FROM trips
UNION ALL SELECT 'fuel', COUNT(*) FROM fuel
UNION ALL SELECT 'maintenance', COUNT(*) FROM maintenance;
