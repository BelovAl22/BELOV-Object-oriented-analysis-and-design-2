package com.race.core;

import com.race.api.RacePlugin;
import com.race.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.List;

public class RaceEngine {
    private final RaceData raceData;
    private final List<RacePlugin> plugins;
    private final double trackLength = 2500.0;
    private Timeline timeline;

    public RaceEngine(RaceData data, List<RacePlugin> plugins) {
        this.raceData = data;
        this.plugins = plugins;
    }

    public void start(Runnable updateUI) {
        // Создаем таймер: 10 тиков в секунду (Duration.millis(100))
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            processTick();
            updateUI.run();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void processTick() {
        // 1. Обновляем все плагины
        plugins.forEach(p -> p.update(raceData, 0.1));

        // 2. Находим лидера гонки
        CarState leader = raceData.getCars().stream()
                .max((c1, c2) -> Double.compare(c1.totalDistance.get(), c2.totalDistance.get()))
                .orElse(raceData.getCars().get(0));

        double leaderDist = leader.totalDistance.get();

        // 3. ПРОВЕРКА ФИНИША: если лидер завершил все круги
        // ПРОВЕРКА ФИНИША
        if (leader.lap.get() > raceData.getTotalLaps()) {
            if (timeline != null) timeline.stop();

            // ВЫЗЫВАЕМ СОХРАНЕНИЕ В БД
            com.race.db.DatabaseManager.saveResult(leader.driverName, raceData.getTotalLaps());

            raceData.getCars().forEach(c -> {
                c.speed.set(0);
                c.lastLog.set("FINISHED");
            });
            return;
        }

        // 4. Обновляем состояние каждой машины
        for (CarState car : raceData.getCars()) {

            // Расчет отставания (GAP)
            if (car == leader) {
                car.gapToLeader.set("LEADER");
            } else {
                double gap = leaderDist - car.totalDistance.get();
                car.gapToLeader.set(String.format("+%.1f m", gap));
            }

            // Логика заезда в боксы (Пит-стоп в процессе)
            if (car.pitStopTicksLeft > 0) {
                car.speed.set(0);
                car.pitStopTicksLeft--;
                if (car.pitStopTicksLeft == 0) finalizePitStop(car);
                continue;
            }

            // РАСЧЕТ СКОРОСТИ
            double v;
            if (car.fuel.get() <= 0) {
                // УСКОРЕННЫЙ LIMP MODE: ~95 км/ч, чтобы дотянуть до боксов
                v = 95.0 + (Math.random() * 5);
                car.lastLog.set("LIMPING (NO FUEL)");
                car.isPitStopScheduled = true; // Принудительно планируем пит-стоп
            } else {
                // Обычная логика скорости
                v = 280 + car.tires.get().speedBonus + car.pace.get().speedBonus;
                if (car.tireWear.get() > 75) v -= 70; // Штраф за износ
                if (car.engineTemp.get() > 118) v -= 50; // Штраф за перегрев
                if (raceData.isRainy() && car.tires.get() != TireType.WET) v -= 140; // Штраф за дождь
                v += (Math.random() * 15); // Случайные колебания
            }

            car.speed.set(v);

            // Движение: Дистанция = Скорость * Время (тик)
            double dist = car.speed.get() * 0.05;
            car.totalDistance.set(car.totalDistance.get() + dist);

            // Прогресс по текущему кругу для полоски ProgressBar
            car.lapProgress.set((car.totalDistance.get() % trackLength) / trackLength);

            // Счетчик кругов
            int currentLap = (int)(car.totalDistance.get() / trackLength) + 1;
            if (currentLap > car.lap.get()) {
                car.lap.set(currentLap);
                // Если был запланирован пит-стоп, машина останавливается
                if (car.isPitStopScheduled) {
                    car.pitStopTicksLeft = 40; // Остановка на 4 секунды
                    car.lastLog.set("BOX BOX BOX");
                }
            }

            // AI Decision Making: когда ехать в боксы
            // AI Decision Making: когда ехать в боксы (если топлива < 7% или шины убиты > 85%)
            if (!car.isPlayer && (car.fuel.get() < 25 || car.tireWear.get() > 85) && !car.isPitStopScheduled) {
                car.isPitStopScheduled = true;

                if (raceData.isRainy()) {
                    // Если дождь - выбора нет, только дождевые
                    car.nextTireType.set(TireType.WET);
                } else {
                    // Если сухо - выбираем случайно между SOFT, MEDIUM и HARD
                    TireType[] dryOptions = {TireType.SOFT, TireType.MEDIUM, TireType.HARD};
                    int randomIndex = (int)(Math.random() * dryOptions.length);
                    TireType selectedTire = dryOptions[randomIndex];

                    car.nextTireType.set(selectedTire);
                    car.lastLog.set("STRATEGY: BOX FOR " + selectedTire);
                }
            }
        }
    }

    private void finalizePitStop(CarState car) {
        car.fuel.set(100);
        car.tireWear.set(0);
        car.tires.set(car.nextTireType.get()); // Ставим шины, выбранные заранее
        car.isPitStopScheduled = false;
        car.lastLog.set("OUT ON " + car.tires.get());
    }
}