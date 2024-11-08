package org.example;

class Car implements Runnable {
    private final int carId;
    private final int arrivalTime;
    private final int parkingDuration;
    private final int gateId;
    private final ParkingProcess parkingSystem;

    public Car(int carId, int arrivalTime, int parkingDuration, int gateId, ParkingProcess parkingSystem) {
        this.carId = carId;
        this.arrivalTime = arrivalTime;
        this.parkingDuration = parkingDuration;
        this.gateId = gateId;
        this.parkingSystem = parkingSystem;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(arrivalTime * 1000L);
            System.out.println("Car " + carId + " from Gate " + gateId + " arrived at time " + arrivalTime);
            synchronized (parkingSystem) {
                // not a bust wait because parkingSystem.wait(); make thread ideal
                while (ParkingProcess.getParkingLot().availablePermits() == 0) {
                    System.out.println("Car " + carId + " from Gate " + gateId + " waiting for a spot.");
                    parkingSystem.wait();
                }
                ParkingProcess.getParkingLot().acquire();
                parkingSystem.carEntered(gateId);
                System.out.println("Car " + carId + " from Gate " + gateId + " parked. (Parking Status: "
                        + parkingSystem.getCurrentCarsParked() + " spots occupied)");
            }
            Thread.sleep(parkingDuration * 1000L);
            synchronized (parkingSystem) {
                parkingSystem.carExited();
                ParkingProcess.getParkingLot().release();
                System.out.println("Car " + carId + " from Gate " + gateId + " left after " + parkingDuration + " units of time. (Parking Status: "
                        + parkingSystem.getCurrentCarsParked() + " spots occupied)");
                parkingSystem.notifyAll();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Car " + carId + " from Gate " + gateId + " was interrupted.");
        }
    }
}
