package com.fnkcode.sla;

import java.util.function.Predicate;

public class SlaRetryPredicate implements Predicate<Throwable> {

    private final Class<? extends Throwable>[] retryTypes;

    @SafeVarargs
    public SlaRetryPredicate(Class<? extends Throwable>... retryTypes) {
        this.retryTypes = retryTypes;
    }

    @Override
    public boolean test(Throwable throwable) {
        boolean isRetriableType = false;
        for (Class<? extends Throwable> type : retryTypes) {
            if (type.isAssignableFrom(throwable.getClass())) {
                isRetriableType = true;
                break;
            }
        }

        if (!isRetriableType) {
            return false;
        }

        //time budget left?
        return SlaContext.hasTimeLeft();
    }
}