CREATE TABLE fuel_quota(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(255),
    vehicle_type VARCHAR(255),
    fuel_type VARCHAR(255),
    qr_code VARCHAR(255),
    quota INT(11)
);