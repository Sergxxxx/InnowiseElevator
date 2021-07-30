package com.gmail.melnikau.concurrency.controller;

import com.gmail.melnikau.concurrency.constant.Constants;
import com.gmail.melnikau.concurrency.enums.MovementDirection;
import com.gmail.melnikau.concurrency.model.Building;
import com.gmail.melnikau.concurrency.model.Elevator;
import com.gmail.melnikau.concurrency.model.Floor;
import com.gmail.melnikau.concurrency.model.Passenger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Building building;
    private final List<Elevator> elevators;
    private final Map<Passenger, Elevator> passengerElevatorMap = new HashMap<>();
    private final Lock lock;
    private final List<Condition> floorPassengersConditionsList;
    private final List<Condition> elevatorPassengersConditionsList;
    private final Condition elevatorCondition;
    private int passengerCount;
    private Elevator activeElevator;

    public Controller(Building building, List<Elevator> elevators, int passengerCount) {
        this.building = building;
        this.elevators = new ArrayList<>(elevators);
        this.lock = new ReentrantLock();
        this.floorPassengersConditionsList = getConditions();
        this.elevatorPassengersConditionsList = getConditions();
        this.elevatorCondition = lock.newCondition();
        this.passengerCount = passengerCount;
    }

    private List<Condition> getConditions() {
        return IntStream
                .range(0, building.getBuildingHeight())
                .mapToObj(x -> lock.newCondition())
                .collect(Collectors.toList());
    }

    public void sitPassenger(Passenger passenger) {

        try {
            lock.lock();
            while (!canElevatorAcceptAPassenger(activeElevator, passenger)) {
                floorPassengersConditionsList.get(passenger.getSourceFloor() - 1).await();
            }

            Floor currentFloor = getCurrentFloor(activeElevator);

            currentFloor.getDispatchContainer().remove(passenger);
            passengerCount--;

            List<Passenger> elevatorContainer = activeElevator.getContainer();

            elevatorContainer.add(passenger);
            passengerElevatorMap.put(passenger, activeElevator);

            LOGGER.info(String.format(Constants.BOARDING_OF_PASSENGER, passenger.getPersonalId(),
                    currentFloor.getFloorNumber(), activeElevator.getPersonalId()));

            elevatorCondition.signal();
        } catch (InterruptedException e) {
            LOGGER.error(e);
        } finally {
            lock.unlock();
        }
    }

    public void releasePassenger(Passenger passenger) {

        try {
            lock.lock();
            Elevator elevator = passengerElevatorMap.get(passenger);

            while (elevator.getCurrentFloorNumber() != passenger.getDestinationFloor()) {
                elevatorPassengersConditionsList.get(passenger.getDestinationFloor() - 1).await();
            }

            Floor currentFloor = getCurrentFloor(elevator);

            currentFloor.addPassengerToArrivalContainer(passenger);

            List<Passenger> elevatorContainer = elevator.getContainer();

            elevatorContainer.remove(passenger);

            LOGGER.info(String.format(Constants.DEBOARDING_OF_PASSENGER, passenger.getPersonalId(),
                    currentFloor.getFloorNumber(), elevator.getPersonalId()));

            elevatorCondition.signal();
        } catch (InterruptedException e) {
            LOGGER.error(e);
        } finally {
            lock.unlock();
        }
    }

    public void sitPassengers(Elevator elevator) {

        try {
            lock.lock();
            activeElevator = elevator;

            floorPassengersConditionsList.get(elevator.getCurrentFloorNumber() - 1).signalAll();

            while (elevator.hasFreeSpace() && getCurrentFloor(elevator)
                    .getDispatchContainer()
                    .stream()
                    .anyMatch(passenger -> passenger.getMovementDirection()
                            == elevator.getMovementDirection())) {
                elevatorCondition.await();
            }

            activeElevator = null;
        } catch (InterruptedException e) {
            LOGGER.error(e);
        } finally {
            lock.unlock();
        }
    }

    private Floor getCurrentFloor(Elevator elevator) {
        return building.getFloors().get(elevator.getCurrentFloorNumber() - 1);
    }

    public void move(Elevator elevator) {
        int currentFloor = elevator.getCurrentFloorNumber();
        int old_floor = currentFloor;
        MovementDirection currentDirection = elevator.getMovementDirection();

        if (currentDirection == MovementDirection.UP) {
            elevator.setCurrentFloorNumber(++currentFloor);
        } else {
            elevator.setCurrentFloorNumber(--currentFloor);
        }

        LOGGER.info(String.format(Constants.MOVING_ELEVATOR, elevator.getPersonalId(),
                old_floor, currentFloor));

        if (currentFloor == Constants.INITIAL_FLOOR) {
            elevator.setMovementDirection(MovementDirection.UP);
        }

        if (currentFloor == building.getBuildingHeight()) {
            elevator.setMovementDirection(MovementDirection.DOWN);
        }
    }

    public void releasePassengers(Elevator elevator) {

        try {
            lock.lock();
            elevatorPassengersConditionsList.get(elevator.getCurrentFloorNumber() - 1)
                    .signalAll();

            while (elevator
                    .getContainer()
                    .stream()
                    .anyMatch(passenger -> passenger.getDestinationFloor()
                            == elevator.getCurrentFloorNumber())) {
                elevatorCondition.await();
            }
        } catch (InterruptedException e) {
            LOGGER.error(e);
        } finally {
            lock.unlock();
        }
    }

    public boolean doTask(Elevator elevator) {

        try {
            lock.lock();
            if (elevator.getContainer().size() > 0
                    || passengerCount > getActiveElevatorsFreeCapacity() - elevator.getCapacity()) {

                return true;
            } else {
                elevators.remove(elevator);

                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean canElevatorAcceptAPassenger(Elevator elevator, Passenger passenger) {
        return elevator != null
                && elevator.hasFreeSpace()
                && passenger.getMovementDirection() == elevator.getMovementDirection()
                && elevator.getCurrentFloorNumber()==passenger.getSourceFloor();
    }

    private int getActiveElevatorsFreeCapacity() {
        return elevators
                .stream()
                .map(elevator -> elevator.getCapacity() - elevator.getContainer().size())
                .reduce(0, Integer::sum);
    }

}