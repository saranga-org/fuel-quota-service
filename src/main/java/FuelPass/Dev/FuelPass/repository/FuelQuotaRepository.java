package FuelPass.Dev.FuelPass.repository;

import FuelPass.Dev.FuelPass.model.FuelQuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuelQuotaRepository extends JpaRepository<FuelQuota, Long> {
    Optional<FuelQuota> findByVehicleNumber(String vehicleNumber);
    Optional<FuelQuota> findByQrCode(String qrCode);
}
