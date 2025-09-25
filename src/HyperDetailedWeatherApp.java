import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HyperDetailedWeatherApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Create main container with dark purple gradient background
        StackPane root = new StackPane();
        root.setPadding(new Insets(10));
        
        // Background gradient for the scene
        LinearGradient sceneGradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#8360c3")),
            new Stop(1, Color.web("#2ebf91"))
        );
        root.setBackground(new Background(new BackgroundFill(sceneGradient, null, null)));
        
        // Create the weather card
        HyperDetailedWeatherCard weatherCard = new HyperDetailedWeatherCard();
        root.getChildren().add(weatherCard);
        
        // Create scene with exact dimensions (300x500 with 10px margin around 280x480 card)
        Scene scene = new Scene(root, 300, 500);
        
        // Configure stage
        primaryStage.setTitle("Hyper-Detailed Weather Card");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.DECORATED); // Keep decorations for now
        primaryStage.show();
        
        // Initialize weather data
        WeatherDataModel weatherData = new WeatherDataModel();
        weatherData.setDate("Monday, 27th april");
        weatherData.setTime("9:43am");
        weatherData.setLocation("London");
        weatherData.setTemperature(17);
        weatherData.setWeatherType("sunny");
        weatherData.setDay("Monday");
        
        // Set hourly forecast data
        weatherData.setHourlyForecast(new String[][]{
            {"10am", "15°"},
            {"11am", "16°"},
            {"12pm", "18°"},
            {"1pm", "19°"}
        });
        
        weatherCard.updateWeatherData(weatherData);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
