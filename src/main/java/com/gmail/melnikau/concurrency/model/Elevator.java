package com.gmail.melnikau.concurrency.model;

import com.gmail.melnikau.concurrency.constant.Constants;
import com.gmail.melnikau.concurrency.enums.MovementDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Elevator {

    private static int id = Constants.INITIAL_ID;
    private final int capacity;
    private final List<Passenger> container;
    private final int personalId;
    private int currentFloor;
    private MovementDirection movementDirection;

    public Elevator(int floorsNumber, int capacity, int currentFloor) {
        this.personalId = id++;
        this.currentFloor = generateCurrentFloor(floorsNumber, currentFloor);
        this.movementDirection = getRandomMovementDirection(floorsNumber);
        this.capacity = capacity;
        this.container = new ArrayList<>();
    }

    private int generateCurrentFloor(int floorsNumber, int currentFloor) {
        if (currentFloor > 0) {
            return currentFloor % floorsNumber;
        } else {
            return (int) (Math.ceil(Math.random() * floorsNumber));
        }
    }

    private MovementDirection getRandomMovementDirection(int floorsNumber) {
        MovementDirection[] directions = IntStream
                .range(0, floorsNumber)
                .mapToObj(floor -> Constants.DIRECTION_MAP
                        .get((int) Math.floor(Math.random() * Constants.DIRECTION_MAP.size())))
                .toArray(MovementDirection[]::new);

        directions[0] = MovementDirection.UP;
        directions[directions.length - 1] = MovementDirection.DOWN;

        return directions[currentFloor - 1];
    }

    public boolean hasFreeSpace() {
        return getContainer().size() < getCapacity();
    }

    public void setCurrentFloorNumber(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void setMovementDirection(MovementDirection movementDirection) {
        this.movementDirection = movementDirection;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Passenger> getContainer() {
        return container;
    }

    public int getCurrentFloorNumber() {
        return currentFloor;
    }

    public int getPersonalId() {
        return personalId;
    }

    public MovementDirection getMovementDirection() {
        return movementDirection;
    }

}