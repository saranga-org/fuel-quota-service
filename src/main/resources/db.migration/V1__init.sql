CREATE TABLE fuel_quota(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    vehicleNumber VARCHAR(255),
    vehicleType VARCHAR(255),
    fuelType VARCHAR(255),
    qrCode VARCHAR(255),
    quota VARCHAR(255)
);