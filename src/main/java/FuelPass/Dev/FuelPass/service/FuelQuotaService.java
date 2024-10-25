package FuelPass.Dev.FuelPass.service;

import FuelPass.Dev.FuelPass.dto.FuelQuotaRequest;
import FuelPass.Dev.FuelPass.dto.GetBalanceRequest;
import FuelPass.Dev.FuelPass.dto.GetBalanceResponce;
import FuelPass.Dev.FuelPass.dto.ReduceQuotaReq;
import FuelPass.Dev.FuelPass.model.FuelQuota;
import FuelPass.Dev.FuelPass.repository.FuelQuotaRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class FuelQuotaService {

    private final FuelQuotaRepository fuelQuotaRepository;

    private RestTemplate restTemplate;

    private QRGenerationService qrGenerationService;

    public String addFuelQuota(FuelQuotaRequest fuelQuotaRequest) {
        FuelQuota fuelQuota = new FuelQuota();
        fuelQuota.setVehicleNumber(fuelQuotaRequest.vehicleNumber());
        fuelQuota.setVehicleType(fuelQuotaRequest.vehicleType());
        fuelQuota.setFuelType(fuelQuotaRequest.fuelType());

        // Generate and set the QR code
        String generatedQrCode = qrGenerationService.generateCombinedKey(fuelQuotaRequest.vehicleNumber());
        fuelQuota.setQrCode(generatedQrCode);

        fuelQuota.setQuota(fuelQuotaRequest.quota());
        fuelQuota.setContactNo(fuelQuotaRequest.contactNo());

        // Save to database
        fuelQuotaRepository.save(fuelQuota);

        // Send notification
        String message = "Your vehicle " + fuelQuotaRequest.vehicleNumber() +
                " has registered successfully. You have " + fuelQuotaRequest.quota() +
                " liters of " + fuelQuotaRequest.fuelType() + " per week.";
        sendNotificationAsync(fuelQuotaRequest.contactNo(), message);

        return generatedQrCode;
    }

    public void reduceQuota(ReduceQuotaReq reduceQuotaReq){
        Optional<FuelQuota> fuelQuotaOptional = fuelQuotaRepository.findByVehicleNumber(reduceQuotaReq.vehicleNo());

        if(fuelQuotaOptional.isPresent()){
            FuelQuota fuelQuota = fuelQuotaOptional.get();

            Integer updatedQuota = fuelQuota.getQuota() - reduceQuotaReq.quota();

            fuelQuota.setQuota(Math.max(updatedQuota,0));

            fuelQuotaRepository.save(fuelQuota);

            String message = "An amount of " + reduceQuotaReq.quota() + " liters is reduced from your quota";
            sendNotificationAsync(fuelQuota.getContactNo(), message);
        } else {
            throw new RuntimeException("Vehicle not found !");
        }
    }

    public GetBalanceResponce getBalance(GetBalanceRequest getBalanceRequest) {
        Optional<FuelQuota> fuelQuotaOptional = fuelQuotaRepository.findByQrCode(getBalanceRequest.qrCode());

        if (fuelQuotaOptional.isPresent()) {
            FuelQuota fuelQuota = fuelQuotaOptional.get();

            String vehicleNo = fuelQuota.getVehicleNumber();
            String remainingQuota = String.valueOf(fuelQuota.getQuota());


            return new GetBalanceResponce(vehicleNo, remainingQuota);
        }
        throw new RuntimeException("Fuel vehicle not found for the given QR code");
    }


    public String resetQrCode(String vehicleNo) {
        String newQrCode = qrGenerationService.generateCombinedKey(vehicleNo);

        Optional<FuelQuota> fuelQuotaOptional = fuelQuotaRepository.findByVehicleNumber(vehicleNo);
        if (fuelQuotaOptional.isPresent()) {
            FuelQuota fuelQuota = fuelQuotaOptional.get();
            fuelQuota.setQrCode(newQrCode);

            fuelQuotaRepository.save(fuelQuota);
            return newQrCode;
        } else {
            throw new RuntimeException("Fuel quota not found for vehicle number: " + vehicleNo);
        }
    }



    @Scheduled(cron = "0 0 0 * * SUN")
    public void resetQuota(){
        List<FuelQuota> allQuota = fuelQuotaRepository.findAll();
        for(FuelQuota fuelQuota: allQuota){
            fuelQuota.setQuota(50);
            fuelQuotaRepository.save(fuelQuota);
        }
        String message = "Your fuel quota is renewed";
        sendNotificationAsync("+94788762785",message);
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> sendNotificationAsync(String phoneNumber, String message) {
        String url = "http://localhost:8083/api/notification/send";

        // Create the request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("phoneNumber", phoneNumber);
        requestBody.put("message", message);

        // Set headers if needed
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create an HttpEntity containing the request body and headers
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send the POST request asynchronously
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Message sent successfully!");
            } else {
                System.out.println("Failed to send the message, status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Error occurred while sending the message: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);  // Return a completed future for async handling
    }
}


