package org.example;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import java.util.Map;

class ParkingProcess {
    private static final int SPOTS = 4;
    private static final Semaphore parkingLot = new Semaphore(SPOTS, true);
    private int currentParked = 0;
    private int totalServed = 0;
    private final Map<Integer, AtomicInteger> gateStats = new HashMap<>();

    public ParkingProcess() {
        gateStats.put(1, new AtomicInteger(0));
        gateStats.put(2, new AtomicInteger(0));
        gateStats.put(3, new AtomicInteger(0));
    }

    public synchronized void carEntered(int gateId) {
        currentParked++;
        totalServed++;
        gateStats.get(gateId).incrementAndGet();
    }

    public synchronized void carExited() {
        currentParked--;
    }

    public synchronized int getCurrentCarsParked() {
        return currentParked;
    }

    public int getTotalCarsServed() {
        return totalServed;
    }

    public Map<Integer, AtomicInteger> getGateStats() {
        return gateStats;
    }

    public static Semaphore getParkingLot() {
        return parkingLot;
    }
}