package com.pensionat.customer.client;

import com.pensionat.customer.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

@Component
public class BookingClient {

    private final RestClient restClient;

    public BookingClient(@Value("${booking.service.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public boolean customerHasActiveBookings(Long customerId) {
        try {
            Boolean result = restClient.get()
                    .uri("/api/bookings/customer/{id}/has-active", customerId)
                    .retrieve()
                    .body(Boolean.class);

            return Boolean.TRUE.equals(result);

        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException(
                    "Could not reach booking service. Please try again later."
            );
        } catch (RestClientResponseException e) {
            throw new ServiceUnavailableException(
                    "Booking service returned an error. Please try again later."
            );
        }
    }
}
