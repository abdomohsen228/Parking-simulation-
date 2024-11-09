package org.example;

class Car implements Runnable {
    private final int carId;
    private final int arrivalTime;
    private final int parkingDuration;
    private final int gateId;
    private final ParkingProcess parkingSystem;
    private int waitingDuration=0;

    public Car(int carId, int arrivalTime, int parkingDuration, int gateId, ParkingProcess parkingSystem) {
        this.carId = carId;
        this.arrivalTime = arrivalTime;
        this.parkingDuration = parkingDuration;
        this.gateId = gateId;
        this.parkingSystem = parkingSystem;
        waitingDuration=0;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(arrivalTime * 1000L); // duration of staying
            System.out.println("Car " + carId + " from Gate " + gateId + " arrived at time " + arrivalTime); // car status
            synchronized (parkingSystem) {
                // not a bust wait because parkingSystem.wait(); make thread ideal
                while (ParkingProcess.getParkingLot().availablePermits() == 0) {  // if no place available
                    System.out.println("Car " + carId + " from Gate " + gateId + " waiting for a spot.");
                    waitingDuration++;
                    parkingSystem.wait();
                }
                // ParkingProcess.getParkingLot().acquire(); // if there is an empty place
                parkingSystem.carEntered(gateId);
                System.out.println("Car " + carId + " from Gate " + gateId + " parked after waiting for " + waitingDuration +" units of time. (Parking Status: "
                        + parkingSystem.getCurrentCarsParked() + " spots occupied)");
            }
            Thread.sleep(parkingDuration * 1000L);
            // case when cars exit
            synchronized (parkingSystem) {
                parkingSystem.carExited();
                // ParkingProcess.getParkingLot().release(); // emptying place in the semaphore
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
