import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherCardFX extends Application {
    
    private WeatherApp weatherAPI;
    private JSONObject weatherData;
    private boolean isCelsius = true;
    
    // UI Components
    private Text dateText;
    private Text timeText;
    private Text locationText;
    private Group weatherIcon;
    private Text temperatureText;
    private Text dayText;
    private HBox hourlyForecast;
    private TextField searchField;
    private Pane cardContainer;

    @Override
    public void start(Stage primaryStage) {
        weatherAPI = new WeatherApp();
        
        // Create main container
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #f0f0f0;");
        
        // Create search field
        createSearchField();
        
        // Create weather card
        createWeatherCard();
        
        // Position elements
        searchField.setLayoutX(20);
        searchField.setLayoutY(20);
        
        cardContainer.setLayoutX(20);
        cardContainer.setLayoutY(70);
        
        root.getChildren().addAll(searchField, cardContainer);
        
        // Update initial time
        updateDateTime();
        
        Scene scene = new Scene(root, 320, 570);
        primaryStage.setTitle("Weather Card");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Update time every minute
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.minutes(1), e -> updateDateTime())
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void createSearchField() {
        searchField = new TextField("Enter city name...");
        searchField.setPrefWidth(280);
        searchField.setPrefHeight(35);
        searchField.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #ddd; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8;"
        );
        
        searchField.setOnAction(e -> searchWeather());
        
        searchField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused && searchField.getText().equals("Enter city name...")) {
                searchField.clear();
            } else if (!isNowFocused && searchField.getText().isEmpty()) {
                searchField.setText("Enter city name...");
            }
        });
    }
    
    private void createWeatherCard() {
        // Create card container with exact specifications
        cardContainer = new Pane();
        cardContainer.setPrefSize(280, 480);
        
        // Create rounded rectangle background
        Rectangle cardBg = new Rectangle(280, 480);
        cardBg.setArcWidth(20);
        cardBg.setArcHeight(20);
        
        // Create gradient background (sunny gradient as default)
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#FFB75E")),
            new Stop(1, Color.web("#ED8F03"))
        );
        cardBg.setFill(gradient);
        
        // Add drop shadow
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(16);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(8);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        cardBg.setEffect(dropShadow);
        
        cardContainer.getChildren().add(cardBg);
        
        // Create typography elements with EXACT positioning
        createTypographyElements();
        
        // Create weather icon
        createWeatherIcon();
        
        // Create hourly forecast
        createHourlyForecast();
    }
    
    private void createTypographyElements() {
        // Date Text - Position: 24px from top
        dateText = new Text("Monday, 27th april");
        dateText.setFont(Font.font("System", 14));
        dateText.setFill(Color.rgb(255, 255, 255, 0.8));
        dateText.setStyle("-fx-letter-spacing: 0.3px;");
        dateText.setLayoutX(140 - dateText.getBoundsInLocal().getWidth() / 2); // Center horizontally
        dateText.setLayoutY(24 + 14); // 24px from top + font size for baseline
        
        // Time Text - Position: 40px from top
        timeText = new Text("9:43am");
        timeText.setFont(Font.font("System", FontWeight.THIN, 48));
        timeText.setFill(Color.WHITE);
        timeText.setStyle("-fx-letter-spacing: -1px;");
        timeText.setLayoutX(140 - timeText.getBoundsInLocal().getWidth() / 2);
        timeText.setLayoutY(40 + 48);
        
        // Location Text - Position: 92px from top
        locationText = new Text("London");
        locationText.setFont(Font.font("System", 16));
        locationText.setFill(Color.rgb(255, 255, 255, 0.9));
        locationText.setStyle("-fx-letter-spacing: 0.5px;");
        locationText.setLayoutX(140 - locationText.getBoundsInLocal().getWidth() / 2);
        locationText.setLayoutY(92 + 16);
        
        // Temperature Text - Position: 240px from top
        temperatureText = new Text("17°");
        temperatureText.setFont(Font.font("System", FontWeight.THIN, 72));
        temperatureText.setFill(Color.WHITE);
        temperatureText.setStyle("-fx-letter-spacing: -2px;");
        temperatureText.setLayoutX(140 - temperatureText.getBoundsInLocal().getWidth() / 2);
        temperatureText.setLayoutY(240 + 72);
        
        // Day Text - Position: 320px from top
        dayText = new Text("Monday");
        dayText.setFont(Font.font("System", 18));
        dayText.setFill(Color.rgb(255, 255, 255, 0.9));
        dayText.setLayoutX(140 - dayText.getBoundsInLocal().getWidth() / 2);
        dayText.setLayoutY(320 + 18);
        
        // Divider Line - Position: 360px from top
        Line divider = new Line();
        divider.setStartX(20);
        divider.setStartY(360);
        divider.setEndX(260);
        divider.setEndY(360);
        divider.setStroke(Color.rgb(255, 255, 255, 0.3));
        divider.setStrokeWidth(1);
        
        cardContainer.getChildren().addAll(dateText, timeText, locationText, temperatureText, dayText, divider);
    }
    
    private void createWeatherIcon() {
        // Create sun icon with exact specifications
        weatherIcon = new Group();
        
        // Central circle
        Circle center = new Circle(40, 40, 12);
        center.setFill(Color.TRANSPARENT);
        center.setStroke(Color.WHITE);
        center.setStrokeWidth(2);
        
        // 12 radiating lines (sun rays)
        Group rays = new Group();
        for (int i = 0; i < 12; i++) {
            double angle = i * 30; // 360/12 = 30 degrees between rays
            double radians = Math.toRadians(angle);
            
            double startX = 40 + Math.cos(radians) * 20;
            double startY = 40 + Math.sin(radians) * 20;
            double endX = 40 + Math.cos(radians) * 32;
            double endY = 40 + Math.sin(radians) * 32;
            
            Line ray = new Line(startX, startY, endX, endY);
            ray.setStroke(Color.WHITE);
            ray.setStrokeWidth(2);
            rays.getChildren().add(ray);
        }
        
        weatherIcon.getChildren().addAll(center, rays);
        weatherIcon.setLayoutX(100); // Center horizontally (140 - 40)
        weatherIcon.setLayoutY(140); // 140px from top
        
        cardContainer.getChildren().add(weatherIcon);
    }
    
    private void createHourlyForecast() {
        hourlyForecast = new HBox();
        hourlyForecast.setSpacing(15); // Space between mini cards
        hourlyForecast.setAlignment(Pos.CENTER);
        hourlyForecast.setLayoutX(20);
        hourlyForecast.setLayoutY(380);
        hourlyForecast.setPrefWidth(240);
        
        // Create 4 mini weather cards
        String[] times = {"9am", "12pm", "3pm", "6pm"};
        String[] temps = {"15°", "17°", "19°", "16°"};
        
        for (int i = 0; i < 4; i++) {
            VBox miniCard = createMiniWeatherCard(times[i], temps[i]);
            hourlyForecast.getChildren().add(miniCard);
        }
        
        cardContainer.getChildren().add(hourlyForecast);
    }
    
    private VBox createMiniWeatherCard(String time, String temp) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(8);
        card.setPrefWidth(50);
        
        // Time text
        Text timeText = new Text(time);
        timeText.setFont(Font.font("System", 12));
        timeText.setFill(Color.rgb(255, 255, 255, 0.7));
        
        // Mini weather icon (simplified sun)
        Circle miniIcon = new Circle(10);
        miniIcon.setFill(Color.TRANSPARENT);
        miniIcon.setStroke(Color.WHITE);
        miniIcon.setStrokeWidth(1.5);
        
        // Temperature text
        Text tempText = new Text(temp);
        tempText.setFont(Font.font("System", FontWeight.NORMAL, 14));
        tempText.setFill(Color.WHITE);
        
        card.getChildren().addAll(timeText, miniIcon, tempText);
        return card;
    }
    
    private void searchWeather() {
        String cityName = searchField.getText().trim();
        if (cityName.isEmpty() || cityName.equals("Enter city name...")) {
            return;
        }
        
        try {
            weatherData = WeatherApp.getWeatherData(cityName);
            if (weatherData != null) {
                updateWeatherDisplay(cityName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateWeatherDisplay(String cityName) {
        if (weatherData == null) return;
        
        // Update location
        locationText.setText(cityName);
        centerText(locationText, 140);
        
        // Update temperature
        double temperature = (double) weatherData.get("temperature");
        String tempDisplay = String.format("%.0f°", temperature);
        temperatureText.setText(tempDisplay);
        centerText(temperatureText, 140);
        
        // Update weather condition and background
        String weatherCondition = (String) weatherData.get("weather_condition");
        updateBackgroundForWeather(weatherCondition);
        updateWeatherIconForCondition(weatherCondition);
        
        // Update hourly forecast with realistic variations
        updateHourlyForecastData(temperature);
    }
    
    private void updateBackgroundForWeather(String condition) {
        Rectangle cardBg = (Rectangle) cardContainer.getChildren().get(0);
        LinearGradient gradient;
        
        switch (condition) {
            case "Clear":
                gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#FFB75E")), new Stop(1, Color.web("#ED8F03")));
                break;
            case "Cloudy":
                gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#BDC3C7")), new Stop(1, Color.web("#8B9DC3")));
                break;
            case "Rain":
                gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#536976")), new Stop(1, Color.web("#292E49")));
                break;
            case "Snow":
                gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#E6DEDD")), new Stop(1, Color.web("#C5CAE9")));
                break;
            default:
                gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#4ECDC4")), new Stop(1, Color.web("#44A08D")));
                break;
        }
        
        cardBg.setFill(gradient);
    }
    
    private void updateWeatherIconForCondition(String condition) {
        cardContainer.getChildren().remove(weatherIcon);
        
        switch (condition) {
            case "Clear":
                createSunIcon();
                break;
            case "Cloudy":
                createCloudIcon();
                break;
            case "Rain":
                createRainIcon();
                break;
            case "Snow":
                createSnowIcon();
                break;
            default:
                createSunIcon();
                break;
        }
        
        cardContainer.getChildren().add(weatherIcon);
    }
    
    private void createSunIcon() {
        weatherIcon = new Group();
        
        Circle center = new Circle(40, 40, 12);
        center.setFill(Color.TRANSPARENT);
        center.setStroke(Color.WHITE);
        center.setStrokeWidth(2);
        
        Group rays = new Group();
        for (int i = 0; i < 12; i++) {
            double angle = i * 30;
            double radians = Math.toRadians(angle);
            
            double startX = 40 + Math.cos(radians) * 20;
            double startY = 40 + Math.sin(radians) * 20;
            double endX = 40 + Math.cos(radians) * 32;
            double endY = 40 + Math.sin(radians) * 32;
            
            Line ray = new Line(startX, startY, endX, endY);
            ray.setStroke(Color.WHITE);
            ray.setStrokeWidth(2);
            rays.getChildren().add(ray);
        }
        
        weatherIcon.getChildren().addAll(center, rays);
        weatherIcon.setLayoutX(100);
        weatherIcon.setLayoutY(140);
    }
    
    private void createCloudIcon() {
        weatherIcon = new Group();
        
        // Cloud shape with circles
        Circle cloud1 = new Circle(30, 45, 15);
        Circle cloud2 = new Circle(45, 40, 18);
        Circle cloud3 = new Circle(60, 45, 12);
        
        cloud1.setFill(Color.TRANSPARENT);
        cloud1.setStroke(Color.WHITE);
        cloud1.setStrokeWidth(2);
        
        cloud2.setFill(Color.TRANSPARENT);
        cloud2.setStroke(Color.WHITE);
        cloud2.setStrokeWidth(2);
        
        cloud3.setFill(Color.TRANSPARENT);
        cloud3.setStroke(Color.WHITE);
        cloud3.setStrokeWidth(2);
        
        weatherIcon.getChildren().addAll(cloud1, cloud2, cloud3);
        weatherIcon.setLayoutX(95);
        weatherIcon.setLayoutY(140);
    }
    
    private void createRainIcon() {
        weatherIcon = new Group();
        
        // Cloud
        Circle cloud1 = new Circle(30, 35, 12);
        Circle cloud2 = new Circle(45, 30, 15);
        Circle cloud3 = new Circle(60, 35, 10);
        
        cloud1.setFill(Color.TRANSPARENT);
        cloud1.setStroke(Color.WHITE);
        cloud1.setStrokeWidth(2);
        
        cloud2.setFill(Color.TRANSPARENT);
        cloud2.setStroke(Color.WHITE);
        cloud2.setStrokeWidth(2);
        
        cloud3.setFill(Color.TRANSPARENT);
        cloud3.setStroke(Color.WHITE);
        cloud3.setStrokeWidth(2);
        
        // Rain drops
        Line drop1 = new Line(35, 50, 35, 65);
        Line drop2 = new Line(45, 52, 45, 67);
        Line drop3 = new Line(55, 50, 55, 65);
        
        drop1.setStroke(Color.WHITE);
        drop1.setStrokeWidth(2);
        drop2.setStroke(Color.WHITE);
        drop2.setStrokeWidth(2);
        drop3.setStroke(Color.WHITE);
        drop3.setStrokeWidth(2);
        
        weatherIcon.getChildren().addAll(cloud1, cloud2, cloud3, drop1, drop2, drop3);
        weatherIcon.setLayoutX(95);
        weatherIcon.setLayoutY(140);
    }
    
    private void createSnowIcon() {
        weatherIcon = new Group();
        
        // Cloud
        Circle cloud1 = new Circle(30, 35, 12);
        Circle cloud2 = new Circle(45, 30, 15);
        Circle cloud3 = new Circle(60, 35, 10);
        
        cloud1.setFill(Color.TRANSPARENT);
        cloud1.setStroke(Color.WHITE);
        cloud1.setStrokeWidth(2);
        
        cloud2.setFill(Color.TRANSPARENT);
        cloud2.setStroke(Color.WHITE);
        cloud2.setStrokeWidth(2);
        
        cloud3.setFill(Color.TRANSPARENT);
        cloud3.setStroke(Color.WHITE);
        cloud3.setStrokeWidth(2);
        
        // Snowflakes
        Circle flake1 = new Circle(35, 55, 2);
        Circle flake2 = new Circle(45, 60, 2);
        Circle flake3 = new Circle(55, 55, 2);
        
        flake1.setFill(Color.WHITE);
        flake2.setFill(Color.WHITE);
        flake3.setFill(Color.WHITE);
        
        weatherIcon.getChildren().addAll(cloud1, cloud2, cloud3, flake1, flake2, flake3);
        weatherIcon.setLayoutX(95);
        weatherIcon.setLayoutY(140);
    }
    
    private void updateHourlyForecastData(double currentTemp) {
        String[] times = {"Now", "+3h", "+6h", "+9h"};
        double[] temps = {currentTemp, currentTemp + 2, currentTemp - 1, currentTemp - 3};
        
        hourlyForecast.getChildren().clear();
        
        for (int i = 0; i < 4; i++) {
            String tempDisplay = String.format("%.0f°", temps[i]);
            VBox miniCard = createMiniWeatherCard(times[i], tempDisplay);
            hourlyForecast.getChildren().add(miniCard);
        }
    }
    
    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        
        // Update date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, d");
        String day = now.format(dateFormatter);
        String month = now.format(DateTimeFormatter.ofPattern("MMMM")).toLowerCase();
        int dayOfMonth = now.getDayOfMonth();
        String dayWithSuffix = dayOfMonth + getDayOfMonthSuffix(dayOfMonth);
        
        String dateString = day.replace(String.valueOf(dayOfMonth), dayWithSuffix) + " " + month;
        dateText.setText(dateString);
        centerText(dateText, 140);
        
        // Update time
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
        timeText.setText(now.format(timeFormatter).toLowerCase());
        centerText(timeText, 140);
        
        // Update day
        String dayName = now.format(DateTimeFormatter.ofPattern("EEEE"));
        dayText.setText(dayName);
        centerText(dayText, 140);
    }
    
    private String getDayOfMonthSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
    
    private void centerText(Text text, double centerX) {
        text.setLayoutX(centerX - text.getBoundsInLocal().getWidth() / 2);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
