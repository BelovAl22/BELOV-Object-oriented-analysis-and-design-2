package com.race.core;

import com.race.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.Random;

public class RaceEngine {
    private final RaceData raceData;
    private final double trackLength = 2500.0;
    private Timeline timeline;
    private Random random = new Random();

    public RaceEngine(RaceData data) {
        this.raceData = data;
    }

    public void start(Runnable updateUI) {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            processWeather(); // Логика погоды теперь здесь
            processPhysics(); // Логика всех систем теперь здесь
            updateUI.run();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // ЛОГИКА ПОГОДЫ (раньше была в WeatherPlugin)
    private void processWeather() {
        if (random.nextDouble() < 0.005) {
            raceData.setRainy(!raceData.isRainy());
            String msg = raceData.isRainy() ? "IT'S RAINING!" : "TRACK IS DRYING";
            raceData.getCars().forEach(c -> c.lastLog.set(msg));
        }
    }

    private void processPhysics() {
        CarState leader = raceData.getCars().stream()
                .max((c1, c2) -> Double.compare(c1.totalDistance.get(), c2.totalDistance.get()))
                .get();

        if (leader.lap.get() > raceData.getTotalLaps()) {
            stopRace(leader);
            return;
        }

        for (CarState car : raceData.getCars()) {
            updateCarState(car, leader.totalDistance.get());
        }
    }

    private void updateCarState(CarState car, double leaderDist) {
        // 1. Расчет GAP
        if (car.totalDistance.get() >= leaderDist) car.gapToLeader.set("LEADER");
        else car.gapToLeader.set(String.format("+%.1f m", leaderDist - car.totalDistance.get()));

        // 2. Логика Пит-стопа
        if (car.pitStopTicksLeft > 0) {
            car.speed.set(0);
            car.pitStopTicksLeft--;
            // Охлаждение и заправка встроены прямо сюда
            car.engineTemp.set(Math.max(75, car.engineTemp.get() - 2.0));
            if (car.pitStopTicksLeft == 0) finalizePitStop(car);
            return;
        }

        // 3. ЛОГИКА НАГРЕВА (раньше была в EngineHeatPlugin)
        double targetTemp = 75 + (car.pace.get().heatFactor * 30);
        if (raceData.isRainy()) targetTemp -= 15;
        car.engineTemp.set(car.engineTemp.get() + (targetTemp - car.engineTemp.get()) * 0.05);

        // 4. ЛОГИКА ИЗНОСА ШИН (раньше была в TireWearPlugin)
        double wear = car.tires.get().wearRate * car.pace.get().wearMod * (1.2 - car.tireManagement);
        car.tireWear.set(Math.min(100, car.tireWear.get() + wear));

        // 5. ЛОГИКА ТОПЛИВА (раньше была в FuelStrategyPlugin)
        double cons = 0.1 * (car.pace.get() == Pace.PUSH ? 1.5 : (car.pace.get() == Pace.SLOW ? 0.7 : 1.0));
        car.fuel.set(Math.max(0, car.fuel.get() - cons));

        // 6. РАСЧЕТ СКОРОСТИ
        double v = (car.fuel.get() <= 0) ? 95.0 : 280 + car.tires.get().speedBonus + car.pace.get().speedBonus;
        if (car.tireWear.get() > 75) v -= 70;
        if (car.engineTemp.get() > 118) v -= 50;
        if (raceData.isRainy() && car.tires.get() != TireType.WET) v -= 140;

        car.speed.set(v + random.nextInt(15));

        // 7. ДВИЖЕНИЕ
        car.totalDistance.set(car.totalDistance.get() + car.speed.get() * 0.05);
        car.lapProgress.set((car.totalDistance.get() % trackLength) / trackLength);

        // 8. СМЕНА КРУГА И AI
        int curLap = (int) (car.totalDistance.get() / trackLength) + 1;
        if (curLap > car.lap.get()) {
            car.lap.set(curLap);
            if (car.isPitStopScheduled) car.pitStopTicksLeft = 40;
        }

        if (!car.isPlayer && (car.fuel.get() < 8 || car.tireWear.get() > 85) && !car.isPitStopScheduled) {
            car.isPitStopScheduled = true;
            car.nextTireType.set(raceData.isRainy() ? TireType.WET : TireType.MEDIUM);
        }
    }

    private void finalizePitStop(CarState car) {
        car.fuel.set(100);
        car.tireWear.set(0);
        car.tires.set(car.nextTireType.get());
        car.isPitStopScheduled = false;
        car.lastLog.set("OUT ON " + car.tires.get());
    }

    private void stopRace(CarState winner) {
        if (timeline != null) timeline.stop();
        // Вызов метода save из DatabaseManager
        com.race.db.DatabaseManager.save(winner.driverName, "WINNER", raceData.getTotalLaps());
        raceData.getCars().forEach(c -> {
            c.speed.set(0);
            c.lastLog.set("FINISHED");
        });
    }
}
