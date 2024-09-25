package FuelPass.Dev.FuelPass.dto;

public record FuelQuotaRequest(Long id, String vehicleNumber, String vehicleType, String fuelType, String qrCode, Integer quota) {
}
