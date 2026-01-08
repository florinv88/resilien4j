package com.fnkcode.circuitbraker;

import com.fnkcode.exceptions.NotFoundException;

import java.util.function.Predicate;

public class CourierExceptionsForResillience4jPredicate implements Predicate<Throwable> {
    @Override
    public boolean test(Throwable t) {
        if (t instanceof NotFoundException ce) {
            // ONLY record (trip circuit) if it's COMMUNICATION (technical)
            // REJECTED (business) returns false, so the circuit stays CLOSED
            return ce.getReason().equals("COMMUNICATION");
        }

        if (t instanceof org.springframework.web.client.ResourceAccessException) {
            return true;
        }

        return t instanceof java.io.IOException ||
                t instanceof org.springframework.web.client.HttpServerErrorException;
    }
}
