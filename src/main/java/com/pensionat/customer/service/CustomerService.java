package com.pensionat.customer.service;

import com.pensionat.customer.dto.CreateCustomerRequest;
import com.pensionat.customer.dto.LoginRequest;
import com.pensionat.customer.dto.LoginResponse;
import com.pensionat.customer.dto.UpdateCustomerRequest;
import com.pensionat.customer.exception.BadRequestException;
import com.pensionat.customer.exception.NotFoundException;
import com.pensionat.customer.model.CustomerEntity;
import com.pensionat.customer.repository.CustomerRepository;
import com.pensionat.customer.client.BookingClient;
import com.pensionat.customer.exception.ConflictException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookingClient bookingClient;

    public CustomerService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            BookingClient bookingClient
    ) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookingClient = bookingClient;
    }

    public List<CustomerEntity> getAllCustomers() {
        return customerRepository.findAll();
    }

    public CustomerEntity createCustomer(CreateCustomerRequest request) {
        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setHashedPassword(
                passwordEncoder.encode(request.hashedPassword())
        );
        customer.setPhoneNumber(request.phone());

        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Customer not found");
        }

        if (bookingClient.customerHasActiveBookings(id)) {
            throw new ConflictException("Customer cannot be deleted due to active booking(s)");
        }

        customerRepository.deleteById(id);
    }

    public CustomerEntity updateCustomer(
            Long id,
            UpdateCustomerRequest request
    ) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Customer not found")
                );

        if (customerRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new BadRequestException("Email is already in use");
        }

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());

        if (request.hashedPassword() != null
                && !request.hashedPassword().isBlank()) {
            customer.setHashedPassword(
                    passwordEncoder.encode(request.hashedPassword())
            );
        }

        customer.setPhoneNumber(request.phone());

        return customerRepository.save(customer);
    }

    public LoginResponse login(LoginRequest request) {
        CustomerEntity customer = customerRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new NotFoundException("Customer not found")
                );

        if (!passwordEncoder.matches(
                request.password(),
                customer.getHashedPassword()
        )) {
            throw new BadRequestException("Invalid password");
        }

        return new LoginResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber()
        );
    }
}