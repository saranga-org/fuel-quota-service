package FuelPass.Dev.FuelPass.service;

import FuelPass.Dev.FuelPass.dto.FuelQuotaRequest;
import FuelPass.Dev.FuelPass.model.FuelQuota;
import FuelPass.Dev.FuelPass.repository.FuelQuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FuelQuotaService {

    private final FuelQuotaRepository fuelQuotaRepository;

    public void addFuelQuota(FuelQuotaRequest fuelQuotaRequest){

        // Map vehicle request to vehicle object
        FuelQuota fuelQuota = new FuelQuota();

        fuelQuota.setVehicleNumber(fuelQuotaRequest.vehicleNumber());
        fuelQuota.setVehicleType(fuelQuotaRequest.vehicleType());
        fuelQuota.setFuelType(fuelQuotaRequest.fuelType());
        fuelQuota.setQrCode(fuelQuotaRequest.qrCode());
        fuelQuota.setQuota(fuelQuotaRequest.quota());

        // Save vehicle ot FuelPass Repository

        fuelQuotaRepository.save(fuelQuota);
    }
}
