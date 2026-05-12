package com.race.model;

public enum Pace {
    SLOW(0.8, 0.5, -20.0),    // Экономия: мало греет, мало износит, медленно
    MEDIUM(1.0, 1.0, 0.0),    // Баланс
    PUSH(1.5, 1.8, 30.0);     // Агрессия: сильно греет, быстро износит, быстро едет

    public final double wearMod;
    public final double heatFactor;
    public final double speedBonus;

    Pace(double wearMod, double heatFactor, double speedBonus) {
        this.wearMod = wearMod;
        this.heatFactor = heatFactor;
        this.speedBonus = speedBonus;
    }
}