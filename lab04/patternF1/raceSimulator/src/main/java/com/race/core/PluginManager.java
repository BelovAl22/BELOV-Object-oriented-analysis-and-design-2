package com.race.core;

// Импорты из стандартной библиотеки Java
import java.io.InputStream;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

// Импорт нашего собственного интерфейса
import com.race.api.RacePlugin;

public class PluginManager {
    // Список для хранения загруженных плагинов
    private final List<RacePlugin> loadedPlugins = new ArrayList<>();

    public void loadPlugins() {
        // Пытаемся открыть файл конфигурации из папки resources
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("plugins.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.err.println("CRITICAL: plugins.properties NOT FOUND in resources!");
                return;
            }

            prop.load(input);
            String pluginsStr = prop.getProperty("active.plugins");

            if (pluginsStr == null || pluginsStr.isEmpty()) {
                System.err.println("No plugins defined in properties file.");
                return;
            }

            // Разделяем строку по запятым на массив имен классов
            String[] classNames = pluginsStr.split(",");
            for (String className : classNames) {
                try {
                    String cleanName = className.trim(); // Убираем лишние пробелы
                    System.out.println("Attempting to load plugin: " + cleanName);

                    // Рефлексия: загружаем класс по его строковому имени
                    Class<?> clazz = Class.forName(cleanName);

                    // Создаем экземпляр плагина
                    RacePlugin plugin = (RacePlugin) clazz.getDeclaredConstructor().newInstance();

                    // Добавляем в наш список
                    loadedPlugins.add(plugin);
                    System.out.println("Successfully loaded: " + plugin.getName());
                } catch (Exception e) {
                    System.err.println("Failed to load plugin class: " + className);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading plugins.properties:");
            e.printStackTrace();
        }
    }

    public List<RacePlugin> getPlugins() {
        return loadedPlugins;
    }
}