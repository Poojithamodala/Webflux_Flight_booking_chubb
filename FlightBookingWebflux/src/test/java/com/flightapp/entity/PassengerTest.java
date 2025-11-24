package com.flightapp.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class PassengerTest {

	private static Validator validator;

	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testNameValidation() {
		Passenger passenger = new Passenger();

		passenger.setName(null);
		passenger.setGender("Male");
		passenger.setAge(25);
		passenger.setSeatNumber("A1");
		Set<ConstraintViolation<Passenger>> violations = validator.validate(passenger);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));

		passenger.setName("");
		violations = validator.validate(passenger);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));

		passenger.setName("Poojitha");
		violations = validator.validate(passenger);
		assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
	}

	@Test
	void testGenderValidation() {
		Passenger passenger = new Passenger();
		passenger.setName("Srikanth");
		passenger.setAge(25);
		passenger.setSeatNumber("A1");

		passenger.setGender(null);
		Set<ConstraintViolation<Passenger>> violations = validator.validate(passenger);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("gender")));

		passenger.setGender("");
		violations = validator.validate(passenger);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("gender")));

		passenger.setGender("Male");
		violations = validator.validate(passenger);
		assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("gender")));
	}

	@Test
	void testSeatNumberValidation() {
		Passenger passenger = new Passenger();
		passenger.setName("Poojitha");
		passenger.setGender("Female");
		passenger.setAge(25);

		passenger.setSeatNumber(null);
		Set<ConstraintViolation<Passenger>> violations = validator.validate(passenger);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("seatNumber")));

		passenger.setSeatNumber("");
		violations = validator.validate(passenger);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("seatNumber")));

		passenger.setSeatNumber("A1");
		violations = validator.validate(passenger);
		assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("seatNumber")));
	}

	@Test
	void testOptionalFields() {
		Passenger passenger = new Passenger();
		passenger.setName("Poojitha");
		passenger.setGender("Female");
		passenger.setAge(25);
		passenger.setSeatNumber("A1");

		passenger.setMealPreference(null);
		Set<ConstraintViolation<Passenger>> violations = validator.validate(passenger);
		assertTrue(violations.isEmpty());

		passenger.setMealPreference("Veg");
		violations = validator.validate(passenger);
		assertTrue(violations.isEmpty());

		passenger.setTicketId(null);
		violations = validator.validate(passenger);
		assertTrue(violations.isEmpty());

		passenger.setTicketId("TICKET123");
		violations = validator.validate(passenger);
		assertTrue(violations.isEmpty());
	}

	@Test
	void veryLargeAge() {
		Passenger passenger = new Passenger();
		passenger.setAge(25);
		passenger.setMealPreference("Non-Veg");
		passenger.setTicketId("TICKET123");

		passenger.setAge(Integer.MAX_VALUE);
		passenger.setName("Srikanth");
		passenger.setGender("Male");
		passenger.setSeatNumber("A1");
		Set<ConstraintViolation<Passenger>> violations = validator.validate(passenger);
		assertTrue(violations.isEmpty(), "Very large age should pass if no max limit");
	}

}
