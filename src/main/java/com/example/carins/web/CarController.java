package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.ClaimDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        // TODO: validate date format and handle errors consistently
        LocalDate d;
        try {
            d = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Invalid date format. Expected ISO-8601 (YYYY-MM-DD): " + date);
        }

        boolean valid = service.isInsuranceValid(carId, d);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));
    }

    @PostMapping("/cars/{carId}/claims")
    public ResponseEntity<ClaimDto> registerClaim(
            @PathVariable Long carId,
            @Valid @RequestBody ClaimDto request) {

        Claim createdClaim = service.addClaim(request, carId);

        ClaimDto createdClaimDto = this.toDto(createdClaim);

        URI location = URI.create(String.format("/api/cars/%s/claims/%s", carId, createdClaim.getId()));

        return ResponseEntity
                .created(location)
                .body(createdClaimDto);
    }

    @GetMapping("/cars/{carId}/history")
    public ResponseEntity<List<ClaimDto>> getHistory(@PathVariable Long carId) {
        List<Claim> claims = service.getHistory(carId);

        List<ClaimDto> dtos = claims.stream()
                .map(this::toDto)   // reuse your Claim -> ClaimDto mapper
                .toList();

        return ResponseEntity.ok(dtos);
    }


    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    private ClaimDto toDto(Claim claim) {
        if (claim == null) {
            return null;
        }
        return new ClaimDto(
                claim.getClaimDate(),
                claim.getDescription(),
                claim.getAmount()
        );
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}
}
