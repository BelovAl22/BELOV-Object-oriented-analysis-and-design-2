package com.race.model;

public enum TireType {
    SOFT(0.05, 40.0),   // Быстро изнашивается (+0.05 за тик), очень быстрая
    MEDIUM(0.03, 20.0),
    HARD(0.015, 0.0),   // Медленная, но почти не изнашивается
    WET(0.02, -50.0);   // Только для дождя

    public final double wearRate;
    public final double speedBonus;

    TireType(double wearRate, double speedBonus) {
        this.wearRate = wearRate;
        this.speedBonus = speedBonus;
    }
}