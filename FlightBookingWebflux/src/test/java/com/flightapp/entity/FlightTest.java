package com.flightapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class FlightTest {

	private static Validator validator;

	@BeforeAll
	static void setupValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	private Flight createValidFlight() {
		Flight flight = new Flight();
		flight.setAirline("GoAir");
		flight.setFromPlace("Hyderabad");
		flight.setToPlace("Bangalore");
		flight.setDepartureTime(LocalDateTime.of(2025, 1, 20, 9, 30));
		flight.setArrivalTime(LocalDateTime.of(2025, 1, 20, 11, 0));
		flight.setPrice(3500);
		flight.setTotalSeats(180);
		flight.setAvailableSeats(180);
		return flight;
	}

	@Test
	void testValidFlight() {
		Flight flight = createValidFlight();
		Set<ConstraintViolation<Flight>> violations = validator.validate(flight);
		assertTrue(violations.isEmpty(), "Valid flight should have no violations");
	}

	@Test
	void testAirlineValidation() {
		Flight flight = createValidFlight();
		flight.setAirline("");
		Set<ConstraintViolation<Flight>> violations = validator.validate(flight);
		assertEquals(1, violations.size());
	}

	@Test
	void testFromPlaceValidation() {
		Flight flight = createValidFlight();
		flight.setFromPlace(null);
		Set<ConstraintViolation<Flight>> violations = validator.validate(flight);
		assertEquals(1, violations.size());
	}

	@Test
	void testToPlaceValidation() {
		Flight flight = createValidFlight();
		flight.setToPlace("");
		Set<ConstraintViolation<Flight>> violations = validator.validate(flight);
		assertEquals(1, violations.size());
	}

	@Test
	void testDepartureTimeValidation() {
		Flight flight = createValidFlight();
		flight.setDepartureTime(null);
		Set<ConstraintViolation<Flight>> violations = validator.validate(flight);
		assertEquals(1, violations.size());
	}

	@Test
	void testArrivalTimeValidation() {
		Flight flight = createValidFlight();
		flight.setArrivalTime(null);
		Set<ConstraintViolation<Flight>> violations = validator.validate(flight);
		assertEquals(1, violations.size());
	}

	@Test
	void testPriceValidation() {
		Flight flight = createValidFlight();
		flight.setPrice(0);
		Set<ConstraintViolation<Flight>> violations = validator.validate(flight);
		assertEquals(1, violations.size());

		flight.setPrice(-10);
		violations = validator.validate(flight);
		assertEquals(1, violations.size());
	}

	@Test
	void testTotalSeatsValidation() {
		Flight flight = createValidFlight();
		flight.setTotalSeats(0);
		Set<ConstraintViolation<Flight>> violations = validator.validate(flight);
		assertEquals(1, violations.size());

		flight.setTotalSeats(-5);
		violations = validator.validate(flight);
		assertEquals(1, violations.size());
	}

	@Test
	void testAvailableSeatsValidation() {
		Flight flight = createValidFlight();
		flight.setAvailableSeats(-1);
		Set<ConstraintViolation<Flight>> violations = validator.validate(flight);
		assertEquals(1, violations.size());
	}

	@Test
	void testGettersAndSetters() {
		Flight flight = createValidFlight();
		assertEquals("GoAir", flight.getAirline());
		assertEquals("Hyderabad", flight.getFromPlace());
		assertEquals("Bangalore", flight.getToPlace());
		assertEquals(LocalDateTime.of(2025, 1, 20, 9, 30), flight.getDepartureTime());
		assertEquals(LocalDateTime.of(2025, 1, 20, 11, 0), flight.getArrivalTime());
		assertEquals(3500, flight.getPrice());
		assertEquals(180, flight.getTotalSeats());
		assertEquals(180, flight.getAvailableSeats());
	}

	@Test
	void testDepartureAfterArrival() {
		Flight flight = createValidFlight();
		flight.setDepartureTime(LocalDateTime.of(2025, 1, 20, 12, 0));
		flight.setArrivalTime(LocalDateTime.of(2025, 1, 20, 11, 0));

		assertTrue(flight.getDepartureTime().isAfter(flight.getArrivalTime()),
				"Departure time is after arrival time, which is invalid");
	}

	@Test
	void testAvailableSeatsExceedTotalSeats() {
		Flight flight = createValidFlight();
		flight.setTotalSeats(100);
		flight.setAvailableSeats(120);

		assertTrue(flight.getAvailableSeats() > flight.getTotalSeats(),
				"Available seats exceed total seats, which is invalid");
	}

}
