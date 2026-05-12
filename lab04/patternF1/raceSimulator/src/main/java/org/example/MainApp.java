package com.race;

import com.race.core.*;
import com.race.model.*;
import com.race.ui.Dashboard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        com.race.db.DatabaseManager.init();
        RaceData data = new RaceData();
        data.setTotalLaps(3); // Гонка на 10 кругов


        // ИГРОК
        CarState player = new CarState("YOU (Verstappen)", 0.8, 0.9);
        player.isPlayer = true;
        data.getCars().add(player);

        // ТОП-ПИЛОТЫ (Быстрые, но агрессивные)
        data.getCars().add(new CarState("Hamilton (Fast)", 0.9, 0.85));
        data.getCars().add(new CarState("Leclerc (Risky)", 0.95, 0.6));

        // СРЕДНЯКИ
        data.getCars().add(new CarState("Norris (Stable)", 0.7, 0.9));
        data.getCars().add(new CarState("Alonso (Tactical)", 0.6, 0.99));

        // АУТСАЙДЕРЫ (Медленные)
        data.getCars().add(new CarState("Sargeant (Slow)", 0.4, 0.5));
        data.getCars().add(new CarState("Magnussen (Back)", 0.5, 0.6));

        PluginManager pm = new PluginManager();
        pm.loadPlugins();

        RaceEngine engine = new RaceEngine(data, pm.getPlugins());
        Dashboard dashboard = new Dashboard(data);

        dashboard.setStartAction(() -> engine.start(() -> dashboard.update(data)));

        stage.setScene(new Scene(dashboard, 1100, 700));
        stage.setTitle("F1 Plugin Simulation - Tactical Terminal");
        stage.show();
    }
}