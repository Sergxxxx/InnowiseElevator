package com.gmail.melnikau.concurrency.constant;

import com.gmail.melnikau.concurrency.enums.MovementDirection;

import java.util.List;
import java.util.Map;

public final class Constants {

    public static final String DEFAULT_PROPERTIES_FIlE_NAME = "config.properties";

    public static final String FLOORS_NUMBER = "floorsNumber";
    public static final String ELEVATOR_CAPACITY = "elevatorCapacity";
    public static final String PASSENGERS_NUMBER = "passengersNumber";
    public static final String ELEVATORS_NUMBER = "elevatorsNumber";

    public static final String PASSENGER = "Passenger";
    public static final String ELEVATOR = "Elevator";

    public static final int INITIAL_SUM = 0;
    public static final int INITIAL_ID = 1;
    public static final int INITIAL_FLOOR = 1;

    public static final int RANDOM_FLOOR_VALUE = 0;
    public static final int DEFAULT_FLOORS_NUMBER = 2;
    public static final int DEFAULT_ELEVATOR_CAPACITY = 1;
    public static final int DEFAULT_PASSENGERS_NUMBER = 1;
    public static final int DEFAULT_ELEVATORS_NUMBER = 1;

    public static final List<String> REQUIRED_PARAMETERS = List.of(FLOORS_NUMBER,
            ELEVATOR_CAPACITY, PASSENGERS_NUMBER, ELEVATORS_NUMBER);

    public static final Map<String, Integer> DEFAULT_PARAMETERS_MAP = Map
                .of(FLOORS_NUMBER, DEFAULT_FLOORS_NUMBER,
                    ELEVATOR_CAPACITY, DEFAULT_ELEVATOR_CAPACITY,
                    PASSENGERS_NUMBER, DEFAULT_PASSENGERS_NUMBER,
                    ELEVATORS_NUMBER, DEFAULT_ELEVATORS_NUMBER);

    public static final Map<Integer, MovementDirection> DIRECTION_MAP = Map
            .of(0, MovementDirection.DOWN, 1, MovementDirection.UP);

    public static final String STARTING_TRANSPORTATION = "Starting transportation by elevator %s";
    public static final String COMPLETION_TRANSPORTATION = "Completion transportation by elevator %s";
    public static final String MOVING_ELEVATOR = "Moving elevator %s from %s floor to %s floor";
    public static final String BOARDING_OF_PASSENGER = "Boarding passenger %s on floor %s in elevator %s";
    public static final String DEBOARDING_OF_PASSENGER = "Deboarding passenger %s on floor %s from elevator %s";
    public static final String REG_EXP_FOR_NON_NEGATIVE_INTEGER_NUMBERS = "^\\d+$";
}