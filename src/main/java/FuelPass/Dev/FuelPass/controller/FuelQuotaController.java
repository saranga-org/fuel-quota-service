package FuelPass.Dev.FuelPass.controller;

import FuelPass.Dev.FuelPass.dto.FuelQuotaRequest;
import FuelPass.Dev.FuelPass.service.FuelQuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fuel-quota")
@RequiredArgsConstructor
public class FuelQuotaController {
    private final FuelQuotaService fuelQuotaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addFuelQuota(@RequestBody FuelQuotaRequest fuelQuotaRequest){
        fuelQuotaService.addFuelQuota(fuelQuotaRequest);
        return "Fuel Quota Added Successfully !";
    }
}
