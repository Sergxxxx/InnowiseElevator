package com.gmail.melnikau.concurrency.model;

import java.util.ArrayList;
import java.util.List;

public class Floor {

    private final List<Passenger> dispatchContainer = new ArrayList<>();
    private final List<Passenger> arrivalContainer = new ArrayList<>();
    private final int floor;

    public Floor(int floor) {
        this.floor = floor;
    }

    public void addPassengerToDispatchContainer(Passenger passenger) {
        dispatchContainer.add(passenger);
    }

    public void addPassengerToArrivalContainer(Passenger passenger) {
        arrivalContainer.add(passenger);
    }

    public List<Passenger> getDispatchContainer() {
        return dispatchContainer;
    }

    public List<Passenger> getArrivalContainer() {
        return arrivalContainer;
    }

    public int getFloorNumber() {
        return floor;
    }
}
