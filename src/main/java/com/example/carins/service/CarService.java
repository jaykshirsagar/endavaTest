package com.example.carins.service;

import com.example.carins.exception.CarNotFoundException;
import com.example.carins.exception.DateFormatInvalidException;
import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.ClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.ClaimDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.zip.DataFormatException;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final ClaimRepository claimRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, ClaimRepository claimRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) return false;
        // TODO: optionally throw NotFound if car does not exist
        if (!carRepository.existsById(carId)) {
            throw new CarNotFoundException(carId);
        }
        if (date.isBefore(LocalDate.of(2020, 1, 1)) || date.isAfter(LocalDate.of(2030, 12, 31))) {
            throw new DateFormatInvalidException(date);
        }
        return policyRepository.existsActiveOnDate(carId, date);
    }

    public Claim addClaim(ClaimDto claimDto, Long carId) {
        if (carId == null || !carRepository.existsById(carId)) throw new CarNotFoundException(carId);
        Car car = carRepository.getReferenceById(carId);
        Claim addedClaim = new Claim(car, claimDto.claimDate(), claimDto.description(), claimDto.amount());
        return claimRepository.save(addedClaim);
    }

    public List<Claim> getHistory(Long carId)
    {
        if(carId == null || !carRepository.existsById(carId)) throw new CarNotFoundException(carId);

        List<Claim> claimList = claimRepository.findByCarId(carId)
                .stream()
                .sorted(Comparator.comparing(Claim::getClaimDate))
                .toList();

        return claimList;
    }

}
