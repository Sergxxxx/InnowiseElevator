package com.gmail.melnikau.concurrency;

import com.gmail.melnikau.concurrency.controller.Controller;
import com.gmail.melnikau.concurrency.factory.MyThreadFactory;
import com.gmail.melnikau.concurrency.model.Building;
import com.gmail.melnikau.concurrency.model.Elevator;
import com.gmail.melnikau.concurrency.model.Floor;
import com.gmail.melnikau.concurrency.model.Passenger;
import com.gmail.melnikau.concurrency.task.ElevatorMovementTask;
import com.gmail.melnikau.concurrency.task.PassengerTransportationTask;
import com.gmail.melnikau.concurrency.validator.EndTaskValidator;
import com.gmail.melnikau.concurrency.validator.InputValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gmail.melnikau.concurrency.constant.Constants.*;

public class ElevatorsEmulator {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String configFileName;

    public ElevatorsEmulator() {
        this.configFileName = DEFAULT_PROPERTIES_FIlE_NAME;
    }

    public void emulate(){

        Map<String, Integer> properties = InputValidator.getIntProperties(configFileName);

        int floorsNumber = properties.get(FLOORS_NUMBER);
        int elevatorCapacity = properties.get(ELEVATOR_CAPACITY);
        int passengersNumber = properties.get(PASSENGERS_NUMBER);
        int elevatorsNumber = properties.get(ELEVATORS_NUMBER);

        CountDownLatch countDownLatch = new CountDownLatch(elevatorsNumber);

        List<Floor> floors = IntStream.range(INITIAL_FLOOR, floorsNumber + INITIAL_FLOOR)
                .mapToObj(Floor::new)
                .collect(Collectors.toList());

        List<Passenger> passengers = IntStream.range(0, passengersNumber)
                .mapToObj(number -> new Passenger(floorsNumber))
                .peek(passenger -> floors.get(passenger.getSourceFloor() - 1)
                        .addPassengerToDispatchContainer(passenger))
                .collect(Collectors.toList());

        List<Elevator> elevators = IntStream.range(0, elevatorsNumber)
                .mapToObj(number -> new Elevator(floorsNumber,
                        elevatorCapacity, RANDOM_FLOOR_VALUE))
                .collect(Collectors.toList());

        Building building = new Building(floors);
        Controller controller = new Controller(building, elevators, passengersNumber);
        ThreadFactory passengersThreadFactory = MyThreadFactory.getThreadFactory(PASSENGER);
        ExecutorService passengersExecutorService = Executors.newFixedThreadPool(passengersNumber,
                passengersThreadFactory);

        for (Passenger passenger : passengers) {
            PassengerTransportationTask passengerTask = new PassengerTransportationTask(passenger, controller);
            passengersExecutorService.execute(passengerTask);
        }

        ThreadFactory elevatorsThreadFactory = MyThreadFactory.getThreadFactory(ELEVATOR);
        ExecutorService elevatorsExecutorService = Executors.newFixedThreadPool(elevatorsNumber, elevatorsThreadFactory);
        Lock lock = new ReentrantLock();

        for (Elevator elevator : elevators) {
            ElevatorMovementTask elevatorTask = new ElevatorMovementTask(elevator, controller, countDownLatch, lock);
            elevatorsExecutorService.execute(elevatorTask);
        }

        passengersExecutorService.shutdown();
        elevatorsExecutorService.shutdown();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.error(e);
            Thread.currentThread().interrupt();
        }

        EndTaskValidator.validateResult(floors, elevators, passengersNumber);
    }

}