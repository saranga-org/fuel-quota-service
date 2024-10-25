package FuelPass.Dev.FuelPass.controller;

import FuelPass.Dev.FuelPass.dto.FuelQuotaRequest;
import FuelPass.Dev.FuelPass.dto.GetBalanceRequest;
import FuelPass.Dev.FuelPass.dto.GetBalanceResponce;
import FuelPass.Dev.FuelPass.dto.ReduceQuotaReq;
import FuelPass.Dev.FuelPass.service.FuelQuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fuel-quota")
@RequiredArgsConstructor
public class FuelQuotaController {

    private final FuelQuotaService fuelQuotaService;

    @PostMapping("/add")
    public ResponseEntity<?> addFuelQuota(@RequestBody FuelQuotaRequest fuelQuotaRequest) {
        try {
            String qrCode = fuelQuotaService.addFuelQuota(fuelQuotaRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("QR Code generated: " + qrCode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding fuel quota: " + e.getMessage());
        }
    }


    @PostMapping("/reduce")
    public ResponseEntity<String> reduceQuota(@RequestBody ReduceQuotaReq reduceQuotaReq) {
        try {
            fuelQuotaService.reduceQuota(reduceQuotaReq);
            String message = "Fuel quota reduced by " + reduceQuotaReq.quota() + " liters for QR code " + reduceQuotaReq.qrCode();
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reducing fuel quota: " + e.getMessage());
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestParam String qrCode) {
        try {
            GetBalanceRequest getBalanceRequest = new GetBalanceRequest(qrCode);
            GetBalanceResponce balanceResponse = fuelQuotaService.getBalance(getBalanceRequest);
            return ResponseEntity.ok(balanceResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving balance: " + e.getMessage());
        }
    }

    @PutMapping("/reset-qrcode")
    public ResponseEntity<?> resetQrCode(@RequestParam String vehicleNo) {
        try {
            String newQrCode = fuelQuotaService.resetQrCode(vehicleNo);
            return ResponseEntity.ok("QR Code reset successfully. New QR Code: " + newQrCode);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting QR code: " + e.getMessage());
        }
    }

}

