package com.simulation.restaurant.domain.interfaces;

public interface Observer {
    void update(String event, Object data);
}