package com.gmail.melnikau.concurrency.model;

import com.gmail.melnikau.concurrency.constant.Constants;
import com.gmail.melnikau.concurrency.enums.MovementDirection;
import com.gmail.melnikau.concurrency.enums.TransportationState;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Passenger {

    private static int id = Constants.INITIAL_ID;
    private final int personalId;
    private final int sourceFloor;
    private final int destinationFloor;
    private final MovementDirection movementDirection;
    private TransportationState transportationState;

    public Passenger(int floorsNumber) {
        List<Integer> randomFloors = getRandomFloors(floorsNumber);
        this.personalId = id++;
        this.sourceFloor = randomFloors.get(0);
        this.destinationFloor = randomFloors.get(randomFloors.size() - 1);
        this.transportationState = TransportationState.NOT_STARTED;
        this.movementDirection = destinationFloor > sourceFloor ? MovementDirection.UP : MovementDirection.DOWN;
    }

    private List<Integer> getRandomFloors(int floorsNumber) {
        List<Integer> randomFloorsList = IntStream
                .range(Constants.INITIAL_FLOOR, floorsNumber + Constants.INITIAL_FLOOR)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(randomFloorsList);

        return randomFloorsList;
    }

    public void setTransportationState(TransportationState transportationState) {
        this.transportationState = transportationState;
    }

    public int getPersonalId() {
        return personalId;
    }

    public int getSourceFloor() {
        return sourceFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public TransportationState getTransportationState() {
        return transportationState;
    }

    public MovementDirection getMovementDirection() {
        return movementDirection;
    }

}