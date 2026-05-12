package com.race.plugins;

import com.race.api.RacePlugin;
import com.race.model.*;

public class EngineHeatPlugin implements RacePlugin {
    public String getName() { return "Thermal System"; }

    @Override
    public void update(RaceData data, double dt) {
        for (CarState car : data.getCars()) {
            if (car.pitStopTicksLeft > 0) {
                // Охлаждение на пит-стопе до базовой температуры
                double current = car.engineTemp.get();
                car.engineTemp.set(Math.max(75, current - 1.5));
                continue;
            }

            // Рассчитываем "целевую" температуру на основе темпа (Pace)
            // SLOW -> ~80°C, MEDIUM -> ~95°C, PUSH -> ~115°C
            double targetTemp = 75 + (car.pace.get().heatFactor * 30) + (car.aggression * 5);

            // Если идет дождь, целевая температура падает на 15 градусов (охлаждение)
            if (data.isRainy()) targetTemp -= 15;

            // Плавный нагрев или остывание: температура меняется в сторону целевой
            double current = car.engineTemp.get();
            double delta = (targetTemp - current) * 0.05; // Коэффициент инерции двигателя

            car.engineTemp.set(current + delta);

            // Лог критических событий
            if (current > 120) {
                car.lastLog.set("ENGINE OVERHEAT! Critical Temp!");
            }
        }
    }
}