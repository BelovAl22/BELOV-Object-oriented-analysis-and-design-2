package com.race.plugins;

import com.race.api.RacePlugin;
import com.race.model.*;

public class TireWearPlugin implements RacePlugin {
    public String getName() { return "Tire Wear System"; }

    @Override
    public void update(RaceData data, double dt) {
        for (CarState car : data.getCars()) {
            if (car.pitStopTicksLeft > 0) continue;

            // Износ = База_Шины * Множитель_Темпа * Коэффициент_Пилота
            double wearBase = car.tires.get().wearRate;
            double paceMod = car.pace.get().wearMod;
            double driverStat = 1.2 - car.tireManagement;

            double tickWear = wearBase * paceMod * driverStat;
            double newWear = Math.min(100, car.tireWear.get() + tickWear);

            car.tireWear.set(newWear);

            if (newWear > 80 && car.isPlayer) {
                car.lastLog.set("TIRES CRITICAL: Schedule pit-stop!");
            }
        }
    }
}