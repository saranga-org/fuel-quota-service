package FuelPass.Dev.FuelPass.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fuel_quota")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FuelQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String vehicleNumber;
    private String vehicleType;
    private String fuelType;
    private String qrCode;
    private Integer quota;
    private String contactNo;
}
