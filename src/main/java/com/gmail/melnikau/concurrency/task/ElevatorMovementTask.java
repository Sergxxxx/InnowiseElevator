package com.gmail.melnikau.concurrency.task;

import com.gmail.melnikau.concurrency.constant.Constants;
import com.gmail.melnikau.concurrency.controller.Controller;
import com.gmail.melnikau.concurrency.model.Elevator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

public class ElevatorMovementTask implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Elevator elevator;
    private final Controller controller;
    private final CountDownLatch countDownLatch;
    private final Lock lock;

    public ElevatorMovementTask(Elevator elevator, Controller controller,
                                CountDownLatch countDownLatch, Lock lock) {
        this.elevator = elevator;
        this.controller = controller;
        this.countDownLatch = countDownLatch;
        this.lock = lock;
    }

    public void run() {
        LOGGER.info(String.format(Constants.STARTING_TRANSPORTATION, elevator.getPersonalId()));

        while (controller.doTask(elevator)) {
            lock.lock();
            try {
                controller.sitPassengers(elevator);
                controller.move(elevator);
                controller.releasePassengers(elevator);
            } finally {
                lock.unlock();
            }
        }

        LOGGER.info(String.format(Constants.COMPLETION_TRANSPORTATION, elevator.getPersonalId()));
        countDownLatch.countDown();
    }

}