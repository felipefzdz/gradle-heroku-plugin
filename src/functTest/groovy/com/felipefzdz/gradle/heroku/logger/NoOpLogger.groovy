package com.felipefzdz.gradle.heroku.logger

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.slf4j.Marker

class NoOpLogger implements Logger {
    private final String name

    NoOpLogger(String name) {
        this.name = name
    }

    @Override
    String getName() {
         name
    }

    @Override
    boolean isTraceEnabled() {
        false
    }

    @Override
    void trace(String msg) {
    }

    @Override
    void trace(String format, Object arg) {
    }

    @Override
    void trace(String format, Object arg1, Object arg2) {
    }

    @Override
    void trace(String format, Object... arguments) {
    }

    @Override
    void trace(String msg, Throwable t) {
    }

    @Override
    boolean isTraceEnabled(Marker marker) {
         false
    }

    @Override
    void trace(Marker marker, String msg) {
    }

    @Override
    void trace(Marker marker, String format, Object arg) {
    }

    @Override
    void trace(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override
    void trace(Marker marker, String format, Object... argArray) {
    }

    @Override
    void trace(Marker marker, String msg, Throwable t) {
    }

    @Override
    boolean isDebugEnabled() {
         false
    }

    @Override
    void debug(String msg) {
    }

    @Override
    void debug(String format, Object arg) {
    }

    @Override
    void debug(String format, Object arg1, Object arg2) {
    }

    @Override
    boolean isLifecycleEnabled() {
         false
    }

    @Override
    void debug(String format, Object... arguments) {
    }

    @Override
    void lifecycle(String message) {
    }

    @Override
    void lifecycle(String message, Object... objects) {
    }

    @Override
    void lifecycle(String message, Throwable throwable) {
    }

    @Override
    void debug(String msg, Throwable t) {
    }

    @Override
    boolean isDebugEnabled(Marker marker) {
         false
    }

    @Override
    void debug(Marker marker, String msg) {
    }

    @Override
    void debug(Marker marker, String format, Object arg) {
    }

    @Override
    void debug(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override
    void debug(Marker marker, String format, Object... arguments) {
    }

    @Override
    void debug(Marker marker, String msg, Throwable t) {
    }

    @Override
    boolean isInfoEnabled() {
         false
    }

    @Override
    void info(String msg) {
    }

    @Override
    void info(String format, Object arg) {
    }

    @Override
    void info(String format, Object arg1, Object arg2) {
    }

    @Override
    void info(String format, Object... arguments) {
    }

    @Override
    boolean isQuietEnabled() {
         false
    }

    @Override
    void quiet(String message) {
    }

    @Override
    void quiet(String message, Object... objects) {
    }

    @Override
    void quiet(String message, Throwable throwable) {
    }

    @Override
    boolean isEnabled(LogLevel level) {
        false
    }

    @Override
    void log(LogLevel level, String message) {
    }

    @Override
    void log(LogLevel level, String message, Object... objects) {
    }

    @Override
    void log(LogLevel level, String message, Throwable throwable) {
    }

    @Override
    void info(String msg, Throwable t) {
    }

    @Override
    boolean isInfoEnabled(Marker marker) {
        false
    }

    @Override
    void info(Marker marker, String msg) {
    }

    @Override
    void info(Marker marker, String format, Object arg) {
    }

    @Override
    void info(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override
    void info(Marker marker, String format, Object... arguments) {
    }

    @Override
    void info(Marker marker, String msg, Throwable t) {
    }

    @Override
    boolean isWarnEnabled() {
        false
    }

    @Override
    void warn(String msg) {
    }

    @Override
    void warn(String format, Object arg) {
    }

    @Override
    void warn(String format, Object... arguments) {
    }

    @Override
    void warn(String format, Object arg1, Object arg2) {
    }

    @Override
    void warn(String msg, Throwable t) {
    }

    @Override
    boolean isWarnEnabled(Marker marker) {
        false
    }

    @Override
    void warn(Marker marker, String msg) {
    }

    @Override
    void warn(Marker marker, String format, Object arg) {
    }

    @Override
    void warn(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override
    void warn(Marker marker, String format, Object... arguments) {
    }

    @Override
    void warn(Marker marker, String msg, Throwable t) {
    }

    @Override
    boolean isErrorEnabled() {
        false
    }

    @Override
    void error(String msg) {
    }

    @Override
    void error(String format, Object arg) {
    }

    @Override
    void error(String format, Object arg1, Object arg2) {
    }

    @Override
    void error(String format, Object... arguments) {
    }

    @Override
    void error(String msg, Throwable t) {
    }

    @Override
    boolean isErrorEnabled(Marker marker) {
        false
    }

    @Override
    void error(Marker marker, String msg) {
    }

    @Override
    void error(Marker marker, String format, Object arg) {
    }

    @Override
    void error(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override
    void error(Marker marker, String format, Object... arguments) {
    }

    @Override
    void error(Marker marker, String msg, Throwable t) {
    }
}
