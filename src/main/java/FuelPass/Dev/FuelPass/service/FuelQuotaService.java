package FuelPass.Dev.FuelPass.service;

import FuelPass.Dev.FuelPass.dto.FuelQuotaRequest;
import FuelPass.Dev.FuelPass.dto.ReduceQuotaReq;
import FuelPass.Dev.FuelPass.model.FuelQuota;
import FuelPass.Dev.FuelPass.repository.FuelQuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public void reduceQuota(ReduceQuotaReq reduceQuotaReq){
        Optional<FuelQuota> fuelQuotaOptional = fuelQuotaRepository.findByVehicleNumber(reduceQuotaReq.vehicleNumber());

        if(fuelQuotaOptional.isPresent()){
            FuelQuota fuelQuota = fuelQuotaOptional.get();

            Integer updatedQuota = fuelQuota.getQuota() - reduceQuotaReq.quota();

            fuelQuota.setQuota(Math.max(updatedQuota,0));

            fuelQuotaRepository.save(fuelQuota);
        } else {
            throw new RuntimeException("Vehicle with number " + reduceQuotaReq.vehicleNumber() + " not found !");
        }
    }
}
