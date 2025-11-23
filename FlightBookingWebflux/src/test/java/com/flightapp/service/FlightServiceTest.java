package com.flightapp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flightapp.entity.Flight;
import com.flightapp.repository.FlightRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

	@Mock
	private FlightRepository flightRepository;

	@InjectMocks
	private FlightService flightService;

	private Flight flight;

	@BeforeEach
	void setup() {
		flight = new Flight();
		flight.setId("1");
		flight.setAirline("Indigo");
		flight.setFromPlace("BLR");
		flight.setToPlace("DEL");
		flight.setDepartureTime(LocalDateTime.now());
		flight.setArrivalTime(LocalDateTime.now().plusHours(2));
		flight.setPrice(5000);
		flight.setTotalSeats(120);
		flight.setAvailableSeats(100);
	}

	@Test
	void testAddFlight() {
		when(flightRepository.save(flight)).thenReturn(Mono.just(flight));

		StepVerifier.create(flightService.addFlight(flight)).expectNext(flight).verifyComplete();
	}

	@Test
	void testDeleteFlight_Success() {
		when(flightRepository.deleteById("1")).thenReturn(Mono.empty());

		StepVerifier.create(flightService.deleteFlight("1")).verifyComplete();
	}

	@Test
	void testGetAllFlights() {
		when(flightRepository.findAll()).thenReturn(Flux.just(flight));

		StepVerifier.create(flightService.getAllFlights()).expectNext(flight).verifyComplete();
	}

	@Test
	void testUpdateFlight_Success() {
		Map<String, Object> updates = new HashMap<>();
		updates.put("airline", "Air India");
		updates.put("price", 6000);

		when(flightRepository.findById("1")).thenReturn(Mono.just(flight));
		when(flightRepository.save(any(Flight.class))).thenReturn(Mono.just(flight));

		StepVerifier.create(flightService.updateFlight("1", updates))
				.expectNextMatches(updated -> updated.getAirline().equals("Air India") && updated.getPrice() == 6000)
				.verifyComplete();
	}

	@Test
	void testUpdateFlight_NotFound() {
		when(flightRepository.findById("2")).thenReturn(Mono.empty());

		StepVerifier.create(flightService.updateFlight("2", new HashMap<>()))
				.expectErrorMatches(e -> e.getMessage().contains("Flight not found")).verify();
	}

	@Test
	void testSearchFlightById_Success() {
		when(flightRepository.findById("1")).thenReturn(Mono.just(flight));

		StepVerifier.create(flightService.searchFlightById("1")).expectNext(flight).verifyComplete();
	}

	@Test
	void testSearchFlightById_NotFound() {
		when(flightRepository.findById("2")).thenReturn(Mono.empty());

		StepVerifier.create(flightService.searchFlightById("2"))
				.expectErrorMatches(e -> e.getMessage().contains("Flight not found")).verify();
	}

	@Test
	void testFindByFromPlaceAndToPlaceAndDepartureTimeBetween() {
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusHours(4);

		when(flightRepository.findByFromPlaceAndToPlaceAndDepartureTimeBetween("BLR", "DEL", start, end))
				.thenReturn(Flux.just(flight));

		StepVerifier.create(flightService.findByFromPlaceAndToPlaceAndDepartureTimeBetween("BLR", "DEL",
				start.toString(), end.toString())).expectNext(flight).verifyComplete();
	}

	@Test
	void testSearchFlightsByAirline() {
		when(flightRepository.findByFromPlaceAndToPlaceAndAirline("BLR", "DEL", "Indigo"))
				.thenReturn(Flux.just(flight));

		StepVerifier.create(flightService.searchFlightsByAirline("BLR", "DEL", "Indigo")).expectNext(flight)
				.verifyComplete();
	}
}