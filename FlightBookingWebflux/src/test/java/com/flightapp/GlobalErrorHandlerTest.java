package com.flightapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class GlobalErrorHandlerTest {

	private GlobalErrorHandler errorHandler;

	@BeforeEach
	void setup() {
		errorHandler = new GlobalErrorHandler();
	}

	@Test
	void testHandleValidationExceptions_singleFieldError() {
		BindingResult bindingResult = Mockito.mock(BindingResult.class);
		FieldError fieldError = new FieldError("user", "name", "Name cannot be blank");
		Mockito.when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

		WebExchangeBindException ex = new WebExchangeBindException(null, bindingResult);

		Mono<Map<String, String>> result = errorHandler.handleValidationExceptions(ex);

		StepVerifier.create(result).assertNext(map -> {
			assertEquals(1, map.size());
			assertEquals("Name cannot be blank", map.get("name"));
		}).verifyComplete();
	}

	@Test
	void testHandleValidationExceptions_multipleFieldErrors() {
		BindingResult bindingResult = Mockito.mock(BindingResult.class);
		FieldError error1 = new FieldError("user", "name", "Name cannot be blank");
		FieldError error2 = new FieldError("user", "email", "Email cannot be blank");
		Mockito.when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

		WebExchangeBindException ex = new WebExchangeBindException(null, bindingResult);

		Mono<Map<String, String>> result = errorHandler.handleValidationExceptions(ex);

		StepVerifier.create(result).assertNext(map -> {
			assertEquals(2, map.size());
			assertEquals("Name cannot be blank", map.get("name"));
			assertEquals("Email cannot be blank", map.get("email"));
		}).verifyComplete();
	}

	@Test
	void testHandleValidationExceptions_noFieldErrors() {
		BindingResult bindingResult = Mockito.mock(BindingResult.class);
		Mockito.when(bindingResult.getFieldErrors()).thenReturn(List.of());

		WebExchangeBindException ex = new WebExchangeBindException(null, bindingResult);

		StepVerifier.create(errorHandler.handleValidationExceptions(ex)).assertNext(map -> assertEquals(0, map.size()))
				.verifyComplete();
	}

	@ExceptionHandler(DuplicateKeyException.class)
	Mono<Map<String, String>> handleDuplicateKeyException(DuplicateKeyException ex) {

		Map<String, String> errors = new HashMap<>();
		String message = ex.getMessage();

		if (message != null && message.contains("email")) {
			errors.put("email", "Email already exists");
		} else {
			errors.put("error", "Duplicate key error");
		}

		return Mono.just(errors);
	}

	@Test
	void testHandleDuplicateKeyException_otherDuplicate() {
		DuplicateKeyException ex = new DuplicateKeyException("Duplicate key error: username already exists");

		StepVerifier.create(errorHandler.handleDuplicateKeyException(ex)).assertNext(map -> {
			assertEquals(1, map.size());
			assertEquals("Duplicate key error", map.get("error"));
		}).verifyComplete();
	}

	@Test
	@ExceptionHandler(DuplicateKeyException.class)
	Mono<Map<String, String>> handleDuplicateKeyException1(DuplicateKeyException ex) {
		Map<String, String> errors = new HashMap<>();
		String message = ex.getMessage();

		if ("email".equalsIgnoreCase(message)) {
			errors.put("email", "Email already exists");
		} else if (message != null && message.contains("email")) {
			errors.put("email", "Email already exists");
		} else {
			errors.put("error", "Duplicate key error");
		}

		return Mono.just(errors);
	}

	@Test
	void testHandleGeneralException_basic() {
		Exception ex = new Exception("Some error");

		StepVerifier.create(errorHandler.handleGeneralException(ex)).assertNext(map -> {
			assertEquals(1, map.size());
			assertEquals("Internal server error", map.get("error"));
		}).verifyComplete();
	}
}
