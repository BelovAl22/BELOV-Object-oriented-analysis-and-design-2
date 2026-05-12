package com.race.plugins;

import com.race.api.RacePlugin;
import com.race.model.*;
import java.util.Random;

public class WeatherPlugin implements RacePlugin {
    private Random random = new Random();
    public String getName() { return "Weather Engine"; }

    @Override
    public void update(RaceData data, double dt) {
        // 0.5% шанс смены погоды каждый тик
        if (random.nextDouble() < 0.005) {
            boolean isNowRainy = !data.isRainy();
            data.setRainy(isNowRainy);

            // Оповещаем всех пилотов в лог
            for (CarState car : data.getCars()) {
                car.lastLog.set(isNowRainy ? "IT'S RAINING! Wet tires needed!" : "Track is DRYING.");
            }
        }
    }
}