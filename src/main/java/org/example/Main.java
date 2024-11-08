package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ParkingProcess parkingSystem = new ParkingProcess();
        List<Thread> carThreads = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("parking_input.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.replace(",", "").split(" ");
                    int gateId = Integer.parseInt(parts[1]);
                    int carId = Integer.parseInt(parts[3]);
                    int arrivalTime = Integer.parseInt(parts[5]);
                    int parkingDuration = Integer.parseInt(parts[7]);

                    Car car = new Car(carId, arrivalTime, parkingDuration, gateId, parkingSystem);
                    Thread carThread = new Thread(car);
                    carThreads.add(carThread);
                    carThread.start();

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
        for (Thread carThread : carThreads) {
            try {
                carThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Error waiting for car thread to complete: " + e.getMessage());
            }
        }
        System.out.println("\nSimulation Complete");
        System.out.println("Total Cars Served: " + parkingSystem.getTotalCarsServed());
        System.out.println("Current Cars in Parking: " + parkingSystem.getCurrentCarsParked());
        System.out.println("Details:");
        parkingSystem.getGateStats().forEach((gate, count) ->
                System.out.println("- Gate " + gate + " served " + count + " cars.")
        );
    }
}
