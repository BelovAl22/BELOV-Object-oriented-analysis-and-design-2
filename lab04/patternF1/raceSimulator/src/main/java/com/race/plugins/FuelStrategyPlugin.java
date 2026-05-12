package com.race.plugins;

import com.race.api.RacePlugin;
import com.race.model.*;

public class FuelStrategyPlugin implements RacePlugin {
    public String getName() { return "Fuel Management"; }

    @Override
    public void update(RaceData data, double dt) {
        for (CarState car : data.getCars()) {
            if (car.pitStopTicksLeft > 0) continue;

            // Базовый расход за тик (условно 0.1%)
            double consumption = 0.1;

            // На темпе PUSH расход выше на 50%, на SLOW ниже на 30%
            if (car.pace.get() == Pace.PUSH) consumption *= 1.5;
            if (car.pace.get() == Pace.SLOW) consumption *= 0.7;

            double fuelRemaining = car.fuel.get() - consumption;
            car.fuel.set(Math.max(0, fuelRemaining));

            if (fuelRemaining < 10) {
                car.lastLog.set("LOW FUEL WARNING!");
            }
        }
    }
}