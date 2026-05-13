package com.race.model;

import java.util.ArrayList;
import java.util.List;

public class RaceData {
    private final List<CarState> cars = new ArrayList<>();
    private boolean rainy = false;
    private int totalLaps = 15; // Установим длину гонки по умолчанию 15 кругов

    public List<CarState> getCars() { return cars; }
    public boolean isRainy() { return rainy; }
    public void setRainy(boolean rainy) { this.rainy = rainy; }

    public int getTotalLaps() { return totalLaps; }
    public void setTotalLaps(int totalLaps) { this.totalLaps = totalLaps; }
}