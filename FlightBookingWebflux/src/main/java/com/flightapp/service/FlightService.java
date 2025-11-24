package com.flightapp.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.flightapp.entity.Flight;
import com.flightapp.repository.FlightRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FlightService {

	private final FlightRepository flightRepository;

	public FlightService(FlightRepository flightRepository) {
		this.flightRepository = flightRepository;
	}

	public Mono<Flight> addFlight(Flight flight) {
		return flightRepository.save(flight);
	}

	public Mono<Void> deleteFlight(String flightId) {
		return flightRepository.deleteById(flightId);
	}

	public Mono<Flight> updateFlight(String id, Map<String, Object> updates) {
		return flightRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("Flight not found")))
				.flatMap(flight -> {
					updates.forEach((key, value) -> {
						switch (key) {
						case "airline" -> flight.setAirline((String) value);
						case "fromPlace" -> flight.setFromPlace((String) value);
						case "toPlace" -> flight.setToPlace((String) value);
						case "departureTime" -> flight.setDepartureTime(LocalDateTime.parse((String) value));
						case "arrivalTime" -> flight.setArrivalTime(LocalDateTime.parse((String) value));
						case "price" -> flight.setPrice(((Number) value).intValue());
						case "totalSeats" -> flight.setTotalSeats(((Number) value).intValue());
						case "availableSeats" -> flight.setAvailableSeats(((Number) value).intValue());
						default -> {
							// Ignore this empty field
						}
						}
					});
					return flightRepository.save(flight);
				});
	}

	public Flux<Flight> getAllFlights() {
		return flightRepository.findAll();
	}

	public Mono<Flight> searchFlightById(String flightId) {
		return flightRepository.findById(flightId).switchIfEmpty(Mono.error(new RuntimeException("Flight not found")));
	}

	public Flux<Flight> findByFromPlaceAndToPlaceAndDepartureTimeBetween(String fromPlace, String toPlace,
			String departureTimeStr, String arrivalTimeStr) {

		LocalDateTime start = LocalDateTime.parse(departureTimeStr);
		LocalDateTime end = LocalDateTime.parse(arrivalTimeStr);

		return flightRepository.findByFromPlaceAndToPlaceAndDepartureTimeBetween(fromPlace, toPlace, start, end);
	}

	public Flux<Flight> searchFlightsByAirline(String fromPlace, String toPlace, String airline) {
		return flightRepository.findByFromPlaceAndToPlaceAndAirline(fromPlace, toPlace, airline);
	}
}
