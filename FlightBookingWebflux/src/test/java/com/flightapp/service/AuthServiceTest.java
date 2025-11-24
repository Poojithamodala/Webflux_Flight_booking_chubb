package com.flightapp.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.flightapp.entity.User;
import com.flightapp.repository.UserRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("pooja@gmail.com");
        user.setPassword("password123");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(authService.register(user))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.just(user));

        StepVerifier.create(authService.login("pooja@gmail.com", "password123"))
                .assertNext(sessionId -> assertNotNull(sessionId))
                .verifyComplete();
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByEmail("pooja@gmail.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(authService.login("pooja@gmail.com", "password123"))
                .expectErrorMatches(e ->
                        e instanceof RuntimeException &&
                        e.getMessage().equals("User not found"))
                .verify();
    }

    @Test
    void testLogin_InvalidPassword() {
        User wrongUser = new User();
        wrongUser.setEmail("pooja@gmail.com");
        wrongUser.setPassword("pass123");

        when(userRepository.findByEmail("pooja@gmail.com"))
                .thenReturn(Mono.just(wrongUser));

        StepVerifier.create(authService.login("pooja@gmail.com", "password123"))
                .expectErrorMatches(e ->
                        e instanceof RuntimeException &&
                        e.getMessage().equals("Invalid password"))
                .verify();
    }
    
    @Test
    void testGetLoggedInUser_Success() {

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.just(user));

        String sessionId = authService.login("pooja@gmail.com", "password123")
                .block();  

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.just(user));

        StepVerifier.create(authService.getloggedInUser(sessionId))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void testGetLoggedInUser_InvalidSession() {

        StepVerifier.create(authService.getloggedInUser("bad-session-id"))
                .expectErrorMatches(e ->
                        e instanceof RuntimeException &&
                        e.getMessage().equals("Invalid session ID"))
                .verify();
    }
    
    @Test
    void testRegister_GeneratesId_WhenIdIsNull() {
        User user = new User();
        user.setId(null);
        user.setEmail("pooja@gmail.com");
        user.setPassword("pass");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(authService.register(user))
                .assertNext(savedUser -> {
                    assert savedUser.getId() != null;       
                    assert !savedUser.getId().isBlank();    
                })
                .verifyComplete();
    }

    @Test
    void testRegister_GeneratesId_WhenIdIsBlank() {
        User user = new User();
        user.setId(""); 
        user.setEmail("pooja@gmail.com");
        user.setPassword("pass");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(authService.register(user))
                .assertNext(savedUser -> {
                    assert savedUser.getId() != null;
                    assert !savedUser.getId().isBlank();
                })
                .verifyComplete();
    }
}