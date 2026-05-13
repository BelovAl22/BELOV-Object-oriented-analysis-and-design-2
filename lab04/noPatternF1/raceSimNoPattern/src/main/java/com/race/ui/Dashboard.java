package com.race.ui;

import com.race.model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*; // Это закроет все вопросы по Properties
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.layout.*;

public class Dashboard extends BorderPane {
    private TableView<CarState> table;
    private CarState player;
    private Button startBtn;
    private Label weatherLabel;
    private Label lapCounter; // Новое поле класса

    public Dashboard(RaceData data) {
        // Находим игрока или берем первого
        this.player = data.getCars().stream()
                .filter(c -> c.isPlayer)
                .findFirst()
                .orElse(data.getCars().get(0));

        setupStyles();
        setupHeader();
        setupTable(data); // Передаем данные для инициализации списка
        setupBottomPanel();
    }

    private void setupStyles() {
        this.setStyle("-fx-background-color: #0a0a0a; -fx-padding: 15;");
    }

    private void setupHeader() {
        HBox header = new HBox(40);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 15, 0));

        Label title = new Label("F1 STRATEGY TERMINAL");
        title.setStyle("-fx-text-fill: #00ffcc; -fx-font-size: 24; -fx-font-weight: bold;");

        // НОВОЕ: Счетчик кругов
        lapCounter = new Label("LAP: 1 / 15");
        lapCounter.setStyle("-fx-text-fill: yellow; -fx-font-size: 20; -fx-font-family: 'Courier New';");

        weatherLabel = new Label("TRACK STATUS: DRY");
        weatherLabel.setStyle("-fx-text-fill: white; -fx-background-color: #333; -fx-padding: 8 20;");

        header.getChildren().addAll(title, lapCounter, weatherLabel);
        setTop(header);
    }

    private void setupTable(RaceData data) {
        table = new TableView<>(FXCollections.observableArrayList(data.getCars()));
        table.setStyle("-fx-base: #111; -fx-control-inner-background: #111;");
        table.setFixedCellSize(35);

        TableColumn<CarState, Integer> posCol = new TableColumn<>("POS");
        posCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cell.getValue()) + 1));
        posCol.setPrefWidth(45);

        TableColumn<CarState, String> nameCol = new TableColumn<>("DRIVER");
        nameCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().driverName));
        nameCol.setPrefWidth(130);

        // НОВОЕ: GAP (Отставание)
        TableColumn<CarState, String> gapCol = new TableColumn<>("GAP");
        gapCol.setCellValueFactory(f -> f.getValue().gapToLeader);
        gapCol.setPrefWidth(90);

        // НОВОЕ: SPEED (Текущая скорость)
        TableColumn<CarState, String> spdCol = new TableColumn<>("SPEED");
        spdCol.setCellValueFactory(f -> Bindings.format("%.0f", f.getValue().speed));
        spdCol.setPrefWidth(60);

        TableColumn<CarState, Number> lapCol = new TableColumn<>("LAP");
        lapCol.setCellValueFactory(f -> f.getValue().lap);
        lapCol.setPrefWidth(50);

        TableColumn<CarState, Double> trackCol = new TableColumn<>("PROGRESS");
        trackCol.setCellValueFactory(f -> f.getValue().lapProgress.asObject());
        trackCol.setCellFactory(ProgressBarTableCell.forTableColumn());
        trackCol.setPrefWidth(120);

        TableColumn<CarState, String> tempCol = new TableColumn<>("TEMP");
        tempCol.setCellValueFactory(f -> Bindings.format("%.0f°C", f.getValue().engineTemp));

        TableColumn<CarState, String> fuelCol = new TableColumn<>("FUEL");
        fuelCol.setCellValueFactory(f -> Bindings.format("%.1f%%", f.getValue().fuel));

        TableColumn<CarState, String> logCol = new TableColumn<>("RADIO");
        logCol.setCellValueFactory(f -> f.getValue().lastLog);
        logCol.setPrefWidth(150);

        table.getColumns().addAll(posCol, nameCol, gapCol, spdCol, lapCol, trackCol, tempCol, fuelCol, logCol);
        setCenter(table);
    }

    private void setupBottomPanel() {
        VBox bottom = new VBox(15);
        bottom.setPadding(new Insets(15, 0, 0, 0));

        // Панель управления игрока
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 15; -fx-border-color: #00ffcc; -fx-border-width: 1 0 0 0;");

        this.startBtn = new Button("GO RACE!");
        startBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");

        // Выбор темпа
        ComboBox<Pace> paceBox = new ComboBox<>(FXCollections.observableArrayList(Pace.values()));
        paceBox.setValue(Pace.MEDIUM);
        paceBox.setOnAction(e -> player.pace.set(paceBox.getValue()));

        // Выбор шин для пит-стопа
        ComboBox<TireType> nextTireBox = new ComboBox<>(FXCollections.observableArrayList(TireType.values()));
        nextTireBox.setValue(TireType.MEDIUM);
        nextTireBox.setOnAction(e -> player.nextTireType.set(nextTireBox.getValue()));

        Button pitBtn = new Button("CONFIRM PIT STRATEGY");
        pitBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        pitBtn.setOnAction(e -> {
            player.isPitStopScheduled = true;
            player.lastLog.set("STRATEGY: BOX FOR " + player.nextTireType.get());
        });

        Label pLabel = new Label("PLAYER CONTROL:");
        pLabel.setStyle("-fx-text-fill: #777; -fx-font-weight: bold;");

        controls.getChildren().addAll(startBtn, pLabel, new Label("PACE:"), paceBox, new Label("NEXT TIRE:"), nextTireBox, pitBtn);
        bottom.getChildren().addAll(controls);
        setBottom(bottom);
    }

    public void setStartAction(Runnable r) {
        startBtn.setOnAction(e -> {
            r.run();
            startBtn.setDisable(true);
            startBtn.setText("LIVE");
        });
    }

    public void update(RaceData data) {
        // Находим лидера для отображения текущего круга
        int currentRaceLap = data.getCars().stream()
                .mapToInt(c -> c.lap.get())
                .max().orElse(1);

        // Ограничиваем отображение, чтобы не было "16 / 15" на финише
        int displayLap = Math.min(currentRaceLap, data.getTotalLaps());
        lapCounter.setText(String.format("LAP: %d / %d", displayLap, data.getTotalLaps()));

        table.getItems().sort((c1, c2) -> Double.compare(c2.totalDistance.get(), c1.totalDistance.get()));
        table.refresh();

        if (data.isRainy()) {
            weatherLabel.setText("STATUS: !!! RAINING !!!");
            weatherLabel.setStyle("-fx-text-fill: white; -fx-background-color: #2980b9; -fx-padding: 8 20;");
        } else {
            weatherLabel.setText("STATUS: TRACK DRY");
            weatherLabel.setStyle("-fx-text-fill: white; -fx-background-color: #333; -fx-padding: 8 20;");
        }
    }
}