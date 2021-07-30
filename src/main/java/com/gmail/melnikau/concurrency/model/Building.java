package com.gmail.melnikau.concurrency.model;

import java.util.List;

public class Building {

    private final List<Floor> floors;

    public Building(List<Floor> floors) {
        this.floors = floors;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public int getBuildingHeight() {
        return floors.size();
    }
}