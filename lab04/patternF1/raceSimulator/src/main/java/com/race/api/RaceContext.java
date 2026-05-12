package com.race.api;

import javafx.beans.property.*;

public class RaceContext {
    public DoubleProperty speed = new SimpleDoubleProperty(0.0);
    public DoubleProperty fuel = new SimpleDoubleProperty(100.0);
    public DoubleProperty tireWear = new SimpleDoubleProperty(0.0);
    public DoubleProperty engineTemp = new SimpleDoubleProperty(80.0); // Новое
    public DoubleProperty progress = new SimpleDoubleProperty(0.0);    // Прогресс круга (0.0 - 1.0)
    public StringProperty weather = new SimpleStringProperty("SUNNY");
    public IntegerProperty lap = new SimpleIntegerProperty(1);
    public StringProperty lastLog = new SimpleStringProperty("SYSTEMS READY");

    public final String teamName;
    public final String driverName;

    public RaceContext(String teamName, String driverName) {
        this.teamName = teamName;
        this.driverName = driverName;
    }
}