package com.pensionat.customer.controller;

import com.pensionat.customer.dto.CreateCustomerRequest;
import com.pensionat.customer.dto.CustomerResponse;
import com.pensionat.customer.dto.UpdateCustomerRequest;
import com.pensionat.customer.model.CustomerEntity;
import com.pensionat.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers()
                .stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable Long id) {
        CustomerEntity customerEntity = customerService.getCustomerById(id);
        return CustomerResponse.from(customerEntity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerEntity customerEntity = customerService.createCustomer(request);
        return CustomerResponse.from(customerEntity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        CustomerEntity customerEntity = customerService.updateCustomer(id, request);
        return CustomerResponse.from(customerEntity);
    }
}