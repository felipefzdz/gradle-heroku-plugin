package com.felipefzdz.gradle.heroku.utils

import org.gradle.internal.exceptions.Contextual

import java.time.Duration
import java.time.Instant

import static java.time.Instant.now

abstract class AsyncUtil {
    static <T> T waitFor(Duration timeout, Duration interval, String description, Boolean skipWaits, Closure<T> closure) {
        Duration actualInterval = skipWaits ? Duration.ZERO : interval

        Instant stopAt = now() + timeout
        Throwable error = null

        while (now() < stopAt) {
            try {
                return closure.call()
            } catch (Exception e) {
                error = e
            } catch (AssertionError e) {
                error = e
            }

            if (now() + actualInterval < stopAt) {
                Thread.sleep(actualInterval.toMillis())
            } else {
                break
            }
        }

        throw new TimeoutException("'$description' did not succeed after $timeout.seconds", error)
    }

    @Contextual
    static class TimeoutException extends RuntimeException {
        TimeoutException(String var1, Throwable var2) {
            super(var1, var2)
        }
    }
}
