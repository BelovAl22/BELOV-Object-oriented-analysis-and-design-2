package com.race.model;

import javafx.beans.property.*;

public class CarState {
    public final String driverName;
    public final double aggression;
    public final double tireManagement;

    public DoubleProperty speed = new SimpleDoubleProperty(0);
    public DoubleProperty fuel = new SimpleDoubleProperty(100);
    public DoubleProperty tireWear = new SimpleDoubleProperty(0);
    public DoubleProperty engineTemp = new SimpleDoubleProperty(80);
    public DoubleProperty totalDistance = new SimpleDoubleProperty(0);
    public IntegerProperty lap = new SimpleIntegerProperty(1);
    public DoubleProperty lapProgress = new SimpleDoubleProperty(0.0);

    // НОВОЕ: Отставание от лидера (строка для красивого вывода в таблицу)
    public StringProperty gapToLeader = new SimpleStringProperty("0.0 m");

    public ObjectProperty<TireType> tires = new SimpleObjectProperty<>(TireType.MEDIUM);
    public ObjectProperty<Pace> pace = new SimpleObjectProperty<>(Pace.MEDIUM);
    public ObjectProperty<TireType> nextTireType = new SimpleObjectProperty<>(TireType.MEDIUM);

    public boolean isPlayer = false;
    public boolean isPitStopScheduled = false;
    public int pitStopTicksLeft = 0;
    public StringProperty lastLog = new SimpleStringProperty("Ready");

    public CarState(String name, double aggression, double tireMgmt) {
        this.driverName = name;
        this.aggression = aggression;
        this.tireManagement = tireMgmt;
    }
}