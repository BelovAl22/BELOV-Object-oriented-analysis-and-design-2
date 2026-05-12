package com.race.api;

import com.race.model.RaceData;

public interface RacePlugin {
    String getName();
    // Теперь плагин получает всю гонку и время, прошедшее с прошлого тика
    void update(RaceData data, double deltaTime);
}