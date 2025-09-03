package com.example.carins.controller;

import com.example.carins.model.Car;
import com.example.carins.model.Owner;
import com.example.carins.service.CarService;
import com.example.carins.web.CarController;
import com.example.carins.web.dto.CarDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    private Owner mockOwner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockOwner = new Owner();
        mockOwner.setEmail("test@gmail.com");
        mockOwner.setName("John Doe");
    }

    @Test
    void testGetCars() {
        // Arrange
        Car car1 = new Car("VIN12345", "Toyota", "Corolla", 2020, mockOwner);
        Car car2 = new Car("VIN67890", "Honda", "Civic", 2019, mockOwner);
        when(carService.listCars()).thenReturn(List.of(car1, car2));

        // Act
        List<CarDto> result = carController.getCars();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Toyota", result.get(0).make());
        assertEquals("Honda", result.get(1).make());
        assertEquals("VIN12345", result.get(0).vin());
        verify(carService, times(1)).listCars();
    }

    @Test
    void testIsInsuranceValid_ValidDate() {
        // Arrange
        Long carId = 1L;
        String date = "2025-09-03";
        LocalDate parsedDate = LocalDate.parse(date);
        when(carService.isInsuranceValid(carId, parsedDate)).thenReturn(true);

        // Act
        ResponseEntity<?> response = carController.isInsuranceValid(carId, date);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(carService, times(1)).isInsuranceValid(carId, parsedDate);
    }

    @Test
    void testIsInsuranceValid_InvalidDate() {
        // Arrange
        Long carId = 1L;
        String invalidDate = "2025-99-99";

        // Act
        ResponseEntity<?> response = carController.isInsuranceValid(carId, invalidDate);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid date format"));
        verify(carService, never()).isInsuranceValid(anyLong(), any());
    }
}
