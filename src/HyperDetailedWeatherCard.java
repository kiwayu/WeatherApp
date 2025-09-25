import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class HyperDetailedWeatherCard extends Region {
    
    // EXACT dimensions as specified
    private static final double CARD_WIDTH = 280;
    private static final double CARD_HEIGHT = 480;
    private static final double CORNER_RADIUS = 20;
    private static final double CARD_PADDING = 20;
    
    // Typography elements
    private Text dateText;
    private Text timeText;
    private Text locationText;
    private HyperDetailedWeatherIcon weatherIcon;
    private Text temperatureText;
    private Text dayText;
    private Line dividerLine;
    private HBox hourlyForecast;
    
    public HyperDetailedWeatherCard() {
        initializeCard();
        createLayout();
    }
    
    private void initializeCard() {
        // Set exact dimensions
        setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        setMinSize(CARD_WIDTH, CARD_HEIGHT);
        
        // Create gradient background with EXACT colors
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#FFB75E")),
            new Stop(1, Color.web("#ED8F03"))
        );
        
        // Apply background with rounded corners
        setBackground(new Background(new BackgroundFill(
            gradient, 
            new CornerRadii(CORNER_RADIUS), 
            Insets.EMPTY
        )));
        
        // Add drop shadow effect with EXACT specifications
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(16);
        dropShadow.setSpread(0.5);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(8);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        setEffect(dropShadow);
    }
    
    private void createLayout() {
        // Create all text elements with EXACT positioning and typography
        createDateText();
        createTimeText();
        createLocationText();
        createWeatherIcon();
        createTemperatureText();
        createDayText();
        createDividerLine();
        createHourlyForecast();
        
        // Add all elements to the card
        getChildren().addAll(
            dateText, timeText, locationText, weatherIcon,
            temperatureText, dayText, dividerLine, hourlyForecast
        );
    }
    
    private void createDateText() {
        dateText = new Text("Monday, 27th april");
        dateText.setFont(Font.font("System", FontWeight.NORMAL, 14));
        dateText.setFill(Color.rgb(255, 255, 255, 0.8));
        
        // Position: 24px from top, centered horizontally
        dateText.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            dateText.setLayoutX((CARD_WIDTH - newBounds.getWidth()) / 2);
            dateText.setLayoutY(24 + newBounds.getHeight());
        });
    }
    
    private void createTimeText() {
        timeText = new Text("9:43am");
        timeText.setFont(Font.font("System", FontWeight.THIN, 48));
        timeText.setFill(Color.WHITE);
        
        // Position: 40px from top (16px below date)
        timeText.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            timeText.setLayoutX((CARD_WIDTH - newBounds.getWidth()) / 2);
            timeText.setLayoutY(40 + newBounds.getHeight());
        });
    }
    
    private void createLocationText() {
        locationText = new Text("London");
        locationText.setFont(Font.font("System", FontWeight.NORMAL, 16));
        locationText.setFill(Color.rgb(255, 255, 255, 0.9));
        
        // Position: 92px from top (4px below time)
        locationText.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            locationText.setLayoutX((CARD_WIDTH - newBounds.getWidth()) / 2);
            locationText.setLayoutY(92 + newBounds.getHeight());
        });
    }
    
    private void createWeatherIcon() {
        weatherIcon = new HyperDetailedWeatherIcon();
        
        // Position: 140px from top, centered horizontally
        weatherIcon.setLayoutX((CARD_WIDTH - 80) / 2); // 80px icon width
        weatherIcon.setLayoutY(140);
    }
    
    private void createTemperatureText() {
        temperatureText = new Text("17°");
        temperatureText.setFont(Font.font("System", FontWeight.THIN, 72));
        temperatureText.setFill(Color.WHITE);
        
        // Position: 240px from top (20px below icon)
        temperatureText.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            temperatureText.setLayoutX((CARD_WIDTH - newBounds.getWidth()) / 2);
            temperatureText.setLayoutY(240 + newBounds.getHeight());
        });
    }
    
    private void createDayText() {
        dayText = new Text("Monday");
        dayText.setFont(Font.font("System", FontWeight.NORMAL, 18));
        dayText.setFill(Color.rgb(255, 255, 255, 0.9));
        
        // Position: 320px from top (20px below temperature)
        dayText.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            dayText.setLayoutX((CARD_WIDTH - newBounds.getWidth()) / 2);
            dayText.setLayoutY(320 + newBounds.getHeight());
        });
    }
    
    private void createDividerLine() {
        dividerLine = new Line();
        dividerLine.setStartX(CARD_PADDING);
        dividerLine.setEndX(CARD_WIDTH - CARD_PADDING);
        dividerLine.setStartY(360);
        dividerLine.setEndY(360);
        dividerLine.setStroke(Color.rgb(255, 255, 255, 0.3));
        dividerLine.setStrokeWidth(1);
    }
    
    private void createHourlyForecast() {
        hourlyForecast = new HBox();
        hourlyForecast.setAlignment(Pos.CENTER);
        hourlyForecast.setSpacing(15); // Evenly space the 4 mini cards
        hourlyForecast.setPrefWidth(CARD_WIDTH - (CARD_PADDING * 2));
        
        // Position: 380px from top
        hourlyForecast.setLayoutX(CARD_PADDING);
        hourlyForecast.setLayoutY(380);
        
        // Create 4 mini weather cards (will be populated later)
        for (int i = 0; i < 4; i++) {
            VBox miniCard = createMiniWeatherCard("", "");
            hourlyForecast.getChildren().add(miniCard);
        }
    }
    
    private VBox createMiniWeatherCard(String time, String temp) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(8);
        card.setPrefWidth(50);
        
        // Time text
        Text timeText = new Text(time);
        timeText.setFont(Font.font("System", FontWeight.NORMAL, 12));
        timeText.setFill(Color.rgb(255, 255, 255, 0.7));
        
        // Mini weather icon (simplified circle for now)
        Region miniIcon = new Region();
        miniIcon.setPrefSize(20, 20);
        miniIcon.setMaxSize(20, 20);
        miniIcon.setBackground(new Background(new BackgroundFill(
            Color.TRANSPARENT, 
            new CornerRadii(10), 
            Insets.EMPTY
        )));
        miniIcon.setBorder(new Border(new BorderStroke(
            Color.WHITE, 
            BorderStrokeStyle.SOLID, 
            new CornerRadii(10), 
            new BorderWidths(2)
        )));
        
        // Temperature text
        Text tempText = new Text(temp);
        tempText.setFont(Font.font("System", FontWeight.NORMAL, 14));
        tempText.setFill(Color.WHITE);
        
        card.getChildren().addAll(timeText, miniIcon, tempText);
        return card;
    }
    
    public void updateWeatherData(WeatherDataModel data) {
        if (data.getDate() != null) dateText.setText(data.getDate());
        if (data.getTime() != null) timeText.setText(data.getTime());
        if (data.getLocation() != null) locationText.setText(data.getLocation());
        if (data.getTemperature() != 0) temperatureText.setText(data.getTemperature() + "°");
        if (data.getDay() != null) dayText.setText(data.getDay());
        
        // Update hourly forecast
        String[][] hourlyData = data.getHourlyForecast();
        if (hourlyData != null && hourlyData.length == 4) {
            for (int i = 0; i < 4; i++) {
                VBox miniCard = (VBox) hourlyForecast.getChildren().get(i);
                
                Text timeText = (Text) miniCard.getChildren().get(0);
                Text tempText = (Text) miniCard.getChildren().get(2);
                
                timeText.setText(hourlyData[i][0]);
                tempText.setText(hourlyData[i][1]);
            }
        }
    }
}