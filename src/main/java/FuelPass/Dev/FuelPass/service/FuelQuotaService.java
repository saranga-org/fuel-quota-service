package FuelPass.Dev.FuelPass.service;

import FuelPass.Dev.FuelPass.dto.FuelQuotaRequest;
import FuelPass.Dev.FuelPass.dto.ReduceQuotaReq;
import FuelPass.Dev.FuelPass.model.FuelQuota;
import FuelPass.Dev.FuelPass.repository.FuelQuotaRepository;
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
@RequiredArgsConstructor
public class FuelQuotaService {

    private final FuelQuotaRepository fuelQuotaRepository;
    @Autowired
    private RestTemplate restTemplate;

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

        String message = "An amount of " + fuelQuotaRequest.quota() + " liters is added to your quota";

        sendNotificationAsync("+94788762785",message);
    }

    public void reduceQuota(ReduceQuotaReq reduceQuotaReq){
        Optional<FuelQuota> fuelQuotaOptional = fuelQuotaRepository.findByVehicleNumber(reduceQuotaReq.vehicleNumber());

        if(fuelQuotaOptional.isPresent()){
            FuelQuota fuelQuota = fuelQuotaOptional.get();

            Integer updatedQuota = fuelQuota.getQuota() - reduceQuotaReq.quota();

            fuelQuota.setQuota(Math.max(updatedQuota,0));

            fuelQuotaRepository.save(fuelQuota);

            String message = "An amount of " + reduceQuotaReq.quota() + " liters is reduced from your quota";
            sendNotificationAsync("+94788762785",message);
        } else {
            throw new RuntimeException("Vehicle with number " + reduceQuotaReq.vehicleNumber() + " not found !");
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


