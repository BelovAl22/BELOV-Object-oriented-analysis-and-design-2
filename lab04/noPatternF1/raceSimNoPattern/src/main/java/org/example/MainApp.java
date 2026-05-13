package com.race;

import com.race.core.RaceEngine;
import com.race.model.*;
import com.race.ui.Dashboard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        // 1. Инициализируем БД
        com.race.db.DatabaseManager.init();

        // 2. Создаем данные гонки
        RaceData data = new RaceData();
        data.setTotalLaps(12); // Установим 12 кругов для интереса

        // 3. Добавляем пилотов (Имя, Агрессивность, Умение беречь шины)
        CarState player = new CarState("YOU (Verstappen)", 0.8, 0.9);
        player.isPlayer = true;
        data.getCars().add(player);

        data.getCars().add(new CarState("Hamilton (Pro)", 0.7, 0.95));
        data.getCars().add(new CarState("Leclerc (Fast)", 0.9, 0.6));
        data.getCars().add(new CarState("Norris (Stable)", 0.6, 0.85));
        data.getCars().add(new CarState("Alonso (Legend)", 0.7, 0.99));

        // 4. Создаем движок МОНОЛИТНЫЙ (без PluginManager)
        RaceEngine engine = new RaceEngine(data);

        // 5. Создаем интерфейс
        Dashboard dashboard = new Dashboard(data);

        // 6. Настраиваем действие кнопки Старт
        dashboard.setStartAction(() -> {
            engine.start(() -> dashboard.update(data));
        });

        // 7. Показываем окно
        stage.setScene(new Scene(dashboard, 1100, 700));
        stage.setTitle("F1 Racing Simulator - Monolithic Edition");
        stage.show();
    }
}