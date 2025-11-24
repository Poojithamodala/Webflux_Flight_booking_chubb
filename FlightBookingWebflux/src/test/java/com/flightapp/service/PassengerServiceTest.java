package com.flightapp.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flightapp.entity.Passenger;
import com.flightapp.repository.PassengerRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class PassengerServiceTest {

	private PassengerRepository passengerRepository;
	private PassengerService passengerService;

	private Passenger passenger1;
	private Passenger passenger2;

	@BeforeEach
	void setUp() {
		passengerRepository = mock(PassengerRepository.class);
		passengerService = new PassengerService(passengerRepository);

		passenger1 = new Passenger();
		passenger1.setId("P1");
		passenger1.setName("Srikanth");
		passenger1.setTicketId("T1");

		passenger2 = new Passenger();
		passenger2.setId("P2");
		passenger2.setName("Pooja");
		passenger2.setTicketId("T1");
	}

	@Test
	void testGetAllPassengers() {
		when(passengerRepository.findAll()).thenReturn(Flux.just(passenger1, passenger2));

		StepVerifier.create(passengerService.getAllPassengers()).expectNext(passenger1).expectNext(passenger2)
				.verifyComplete();

		verify(passengerRepository, times(1)).findAll();
	}
}