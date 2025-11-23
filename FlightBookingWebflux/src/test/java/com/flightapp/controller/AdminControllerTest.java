package com.flightapp.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flightapp.entity.Flight;
import com.flightapp.entity.User;
import com.flightapp.service.AuthService;
import com.flightapp.service.FlightService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AdminControllerTest {

    private AuthService authService;
    private FlightService flightService;
    private AdminController adminController;

    private User user;
    private Flight flight;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        flightService = mock(FlightService.class);
        adminController = new AdminController(authService, flightService);

        user = new User();
        user.setEmail("admin@example.com");
        user.setPassword("password");

        flight = new Flight();
        flight.setId("F1");
        flight.setAirline("TestFlight");
        flight.setFromPlace("CityA");
        flight.setToPlace("CityB");
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));
        flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        flight.setPrice(500);
        flight.setTotalSeats(100);
        flight.setAvailableSeats(100);
    }

    @Test
    void testAdminLogin() {
        when(authService.login(user.getEmail(), user.getPassword()))
                .thenReturn(Mono.just("SESSION123"));

        StepVerifier.create(adminController.adminLogin(user))
                .expectNextMatches(res -> res.contains("Login successful"))
                .verifyComplete();

        verify(authService, times(1)).login(user.getEmail(), user.getPassword());
    }

    @Test
    void testAddFlight() {
        when(flightService.addFlight(flight)).thenReturn(Mono.empty());

        StepVerifier.create(adminController.addFlight(flight))
                .expectNext("Flight added successfully")
                .verifyComplete();

        verify(flightService, times(1)).addFlight(flight);
    }

    @Test
    void testUpdateFlight() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("airline", "UpdatedAirline");
        updates.put("price", 600);

        Flight updatedFlight = new Flight();
        updatedFlight.setId("F1");
        updatedFlight.setAirline("UpdatedAirline");
        updatedFlight.setPrice(600);
        updatedFlight.setFromPlace("CityA");
        updatedFlight.setToPlace("CityB");
        updatedFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        updatedFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        updatedFlight.setTotalSeats(100);
        updatedFlight.setAvailableSeats(100);

        when(flightService.updateFlight("F1", updates)).thenReturn(Mono.just(updatedFlight));

        StepVerifier.create(adminController.update("F1", updates))
                .expectNextMatches(f -> f.getAirline().equals("UpdatedAirline") && f.getPrice() == 600)
                .verifyComplete();

        verify(flightService, times(1)).updateFlight("F1", updates);
    }

    @Test
    void testDeleteFlight() {
        when(flightService.deleteFlight("F1")).thenReturn(Mono.empty());

        StepVerifier.create(adminController.delete("F1"))
                .expectNext("Flight deleted successfully")
                .verifyComplete();

        verify(flightService, times(1)).deleteFlight("F1");
    }
}