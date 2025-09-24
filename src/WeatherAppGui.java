import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherAppGui extends JFrame {

    private JSONObject weatherData;
    private boolean isCelsius = true;
    private JLabel temperatureText;
    private JLabel weatherConditionDesc;
    private JLabel humidityText;
    private JLabel windspeedText;
    private JLabel weatherConditionImage;
    private JLabel locationText;
    private JLabel dateTimeText;
    private JTextField searchTextField;
    private JPanel forecastPanel;
    private JPanel cardPanel;
    private JLabel currentTimeLabel;
    private JLabel currentDateLabel;
    private JPanel hourlyForecastPanel;

    public WeatherAppGui(){
        // setup gui and add title
        super("Weather App");

        //configure gui to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set size of gui based on card dimensions (280x480 + padding)
        setSize(320, 580);

        //load our gui at the centre of the screen
        setLocationRelativeTo(null);

        // make layout manager null to manually position our components within the gui
        setLayout(null);

        // prevent any resized of our gui
        setResizable(false);

        // Set default background (will change based on weather)
        setCurrentWeatherBackground("partlyCloudy");

        addGuiComponents();
        updateDateTime();
        
        // Update date/time every minute
        Timer timer = new Timer(60000, e -> updateDateTime());
        timer.start();
    }

    private void addGuiComponents(){
        // Create main card panel with rounded corners effect
        cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        cardPanel.setLayout(null);
        cardPanel.setBounds(20, 20, 280, 480);
        cardPanel.setOpaque(false);
        add(cardPanel);

        // Search field at top
        searchTextField = new JTextField("Enter city name...");
        searchTextField.setBounds(20, 10, 200, 30);
        searchTextField.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));
        searchTextField.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 100), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        searchTextField.setBackground(new Color(255, 255, 255, 150));
        searchTextField.setForeground(new Color(80, 80, 80));
        searchTextField.addFocusListener(createSearchFieldFocusListener());
        searchTextField.addActionListener(this::searchWeather);
        cardPanel.add(searchTextField);

        // Search button
        JButton searchButton = new JButton("Search");
        searchButton.setBounds(230, 10, 60, 30);
        searchButton.setFont(new Font("Helvetica Neue", Font.PLAIN, 10));
        searchButton.setBackground(new Color(255, 255, 255, 150));
        searchButton.setBorder(new LineBorder(new Color(255, 255, 255, 100), 1, true));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(this::searchWeather);
        cardPanel.add(searchButton);

        // Date label (smaller, opacity 0.8)
        currentDateLabel = new JLabel("Monday, 27th April");
        currentDateLabel.setBounds(20, 24 + 8, 240, 20);
        currentDateLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        currentDateLabel.setForeground(new Color(255, 255, 255, 204)); // 0.8 opacity
        cardPanel.add(currentDateLabel);

        // Time label (large, thin font)
        currentTimeLabel = new JLabel("6:27am");
        currentTimeLabel.setBounds(20, 24 + 8 + 4, 240, 40);
        currentTimeLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 32));
        currentTimeLabel.setForeground(Color.WHITE);
        cardPanel.add(currentTimeLabel);

        // Location label
        locationText = new JLabel("Enter a city");
        locationText.setBounds(20, 24 + 8 + 4 + 32 + 4, 240, 20);
        locationText.setFont(new Font("Helvetica Neue", Font.PLAIN, 16));
        locationText.setForeground(new Color(255, 255, 255, 230)); // 0.9 opacity
        cardPanel.add(locationText);

        // Weather icon placeholder
        weatherConditionImage = new JLabel();
        weatherConditionImage.setBounds(100, 24 + 8 + 4 + 32 + 4 + 20 + 32, 80, 80);
        cardPanel.add(weatherConditionImage);

        // Temperature (very large, light font)
        temperatureText = new JLabel("--°");
        temperatureText.setBounds(20, 24 + 8 + 4 + 32 + 4 + 20 + 32 + 80 + 24, 240, 70);
        temperatureText.setFont(new Font("Helvetica Neue", Font.PLAIN, 64));
        temperatureText.setForeground(Color.WHITE);
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(temperatureText);

        // Temperature unit toggle
        JButton unitToggleButton = new JButton(isCelsius ? "°F" : "°C");
        unitToggleButton.setBounds(200, 24 + 8 + 4 + 32 + 4 + 20 + 32 + 80 + 24 + 10, 40, 25);
        unitToggleButton.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));
        unitToggleButton.setBackground(new Color(255, 255, 255, 100));
        unitToggleButton.setBorder(new LineBorder(new Color(255, 255, 255, 150), 1, true));
        unitToggleButton.setForeground(Color.WHITE);
        unitToggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        unitToggleButton.addActionListener(e -> {
            toggleTemperatureUnit();
            unitToggleButton.setText(isCelsius ? "°F" : "°C");
        });
        cardPanel.add(unitToggleButton);

        // Day label
        weatherConditionDesc = new JLabel("Today");
        weatherConditionDesc.setBounds(20, 24 + 8 + 4 + 32 + 4 + 20 + 32 + 80 + 24 + 70 + 16, 240, 25);
        weatherConditionDesc.setFont(new Font("Helvetica Neue", Font.PLAIN, 18));
        weatherConditionDesc.setForeground(new Color(255, 255, 255, 230)); // 0.9 opacity
        cardPanel.add(weatherConditionDesc);

        // Hourly forecast panel
        createHourlyForecastPanel();
    }

    private java.awt.event.FocusListener createSearchFieldFocusListener() {
        return new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchTextField.getText().equals("Enter city name...")) {
                    searchTextField.setText("");
                    searchTextField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchTextField.getText().isEmpty()) {
                    searchTextField.setText("Enter city name...");
                    searchTextField.setForeground(Color.GRAY);
                }
            }
        };
    }

    private void createHourlyForecastPanel() {
        hourlyForecastPanel = new JPanel();
        hourlyForecastPanel.setLayout(new GridLayout(1, 4, 16, 0));
        hourlyForecastPanel.setBounds(20, 24 + 8 + 4 + 32 + 4 + 20 + 32 + 80 + 24 + 70 + 16 + 25 + 20, 240, 60);
        hourlyForecastPanel.setOpaque(false);
        cardPanel.add(hourlyForecastPanel);
        
        // Initialize with empty hourly forecast
        updateHourlyForecast(null);
    }

    private void setCurrentWeatherBackground(String weatherCondition) {
        Color[] gradientColors = getGradientColors(weatherCondition);
        
        // Create a custom panel for gradient background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, gradientColors[0],
                    getWidth(), getHeight(), gradientColors[1]
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(null);
        
        // Update text colors based on weather condition
        Color textColor = getTextColor(weatherCondition);
        updateTextColors(textColor);
    }

    private Color[] getGradientColors(String weatherCondition) {
        switch(weatherCondition) {
            case "sunny":
            case "Clear":
                return new Color[]{new Color(255, 183, 94), new Color(237, 143, 3)};
            case "thunderstorm":
                return new Color[]{new Color(44, 83, 100), new Color(32, 58, 67)};
            case "rainy":
            case "Rain":
                return new Color[]{new Color(83, 105, 118), new Color(41, 46, 73)};
            case "cloudy":
            case "Cloudy":
                return new Color[]{new Color(189, 195, 199), new Color(139, 157, 195)};
            case "snowy":
            case "Snow":
                return new Color[]{new Color(230, 222, 221), new Color(197, 202, 233)};
            case "partlyCloudy":
            default:
                return new Color[]{new Color(78, 205, 196), new Color(68, 160, 141)};
        }
    }

    private Color getTextColor(String weatherCondition) {
        if ("snowy".equals(weatherCondition) || "Snow".equals(weatherCondition)) {
            return new Color(44, 62, 80); // Dark text for snowy
        }
        return Color.WHITE; // White text for all others
    }

    private void updateTextColors(Color textColor) {
        if (currentDateLabel != null) {
            currentDateLabel.setForeground(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 204));
        }
        if (currentTimeLabel != null) {
            currentTimeLabel.setForeground(textColor);
        }
        if (locationText != null) {
            locationText.setForeground(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 230));
        }
        if (temperatureText != null) {
            temperatureText.setForeground(textColor);
        }
        if (weatherConditionDesc != null) {
            weatherConditionDesc.setForeground(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 230));
        }
    }


    private void searchWeather(ActionEvent e) {
        String userInput = searchTextField.getText().trim();
        
        // Validate input
        if (userInput.isEmpty() || userInput.equals("Enter city name...")) {
            JOptionPane.showMessageDialog(this, "Please enter a city name.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Show loading state
            temperatureText.setText("Loading...");
            locationText.setText("Fetching data...");
            
            // Retrieve weather data
            weatherData = WeatherApp.getWeatherData(userInput);
            
            if (weatherData == null) {
                JOptionPane.showMessageDialog(this, "Could not find weather data for '" + userInput + "'. Please check the city name and try again.", "Location Not Found", JOptionPane.ERROR_MESSAGE);
                temperatureText.setText("--°");
                locationText.setText("Enter a city");
                return;
            }

            // Update location name
            locationText.setText(userInput);

            // Update weather condition and background
            String weatherCondition = (String) weatherData.get("weather_condition");
            
            // Update background based on weather condition
            setCurrentWeatherBackground(weatherCondition);
            
            // Re-add components to new content pane
            getContentPane().add(cardPanel);

            // Update weather icon
            updateWeatherIcon(weatherCondition);

            // Update temperature
            updateTemperatureDisplay();

            // Update day description
            weatherConditionDesc.setText("Today");

            // Update hourly forecast (simulate with current data)
            updateHourlyForecast(weatherData);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while fetching weather data. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void toggleTemperatureUnit() {
        isCelsius = !isCelsius;
        updateTemperatureDisplay();
        
        // Update hourly forecast with new temperature unit
        if (weatherData != null) {
            updateHourlyForecast(weatherData);
        }
    }

    private void updateWeatherIcon(String weatherCondition) {
        ImageIcon icon = null;
        switch(weatherCondition) {
            case "Clear":
                icon = loadImage("src/assets/clear.png");
                break;
            case "Cloudy":
                icon = loadImage("src/assets/cloudy.png");
                break;
            case "Rain":
                icon = loadImage("src/assets/rain.png");
                break;
            case "Snow":
                icon = loadImage("src/assets/snow.png");
                break;
            default:
                icon = loadImage("src/assets/cloudy.png");
                break;
        }
        
        if (icon != null) {
            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            weatherConditionImage.setIcon(new ImageIcon(img));
        }
    }

    private void updateHourlyForecast(JSONObject currentWeatherData) {
        hourlyForecastPanel.removeAll();
        
        if (currentWeatherData == null) {
            // Show empty hourly cards
            for (int i = 0; i < 4; i++) {
                JPanel hourCard = createHourlyCard("--", "--°", "cloudy.png");
                hourlyForecastPanel.add(hourCard);
            }
        } else {
            try {
                // Simulate hourly data based on current weather
                double currentTemp = (double) currentWeatherData.get("temperature");
                String condition = (String) currentWeatherData.get("weather_condition");
                
                String[] times = {"Now", "+3h", "+6h", "+9h"};
                double[] temps = {currentTemp, currentTemp + 2, currentTemp - 1, currentTemp - 3};
                
                for (int i = 0; i < 4; i++) {
                    String displayTemp;
                    if (isCelsius) {
                        displayTemp = String.format("%.0f°", temps[i]);
                    } else {
                        double fahrenheit = (temps[i] * 9/5) + 32;
                        displayTemp = String.format("%.0f°", fahrenheit);
                    }
                    
                    JPanel hourCard = createHourlyCard(times[i], displayTemp, getIconFileName(condition));
                    hourlyForecastPanel.add(hourCard);
                }
            } catch (Exception e) {
                // Show empty cards on error
                for (int i = 0; i < 4; i++) {
                    JPanel hourCard = createHourlyCard("--", "--°", "cloudy.png");
                    hourlyForecastPanel.add(hourCard);
                }
            }
        }
        
        hourlyForecastPanel.revalidate();
        hourlyForecastPanel.repaint();
    }

    private JPanel createHourlyCard(String time, String temp, String iconFileName) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setOpaque(false);
        
        // Time label (small, opacity 0.7)
        JLabel timeLabel = new JLabel(time);
        timeLabel.setBounds(0, 0, 50, 15);
        timeLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(255, 255, 255, 179)); // 0.7 opacity
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(timeLabel);
        
        // Weather icon (small)
        ImageIcon icon = loadImage("src/assets/" + iconFileName);
        if (icon != null) {
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        }
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBounds(15, 20, 20, 20);
        card.add(iconLabel);
        
        // Temperature
        JLabel tempLabel = new JLabel(temp);
        tempLabel.setBounds(0, 45, 50, 15);
        tempLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        tempLabel.setForeground(Color.WHITE);
        tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(tempLabel);
        
        return card;
    }

    private void updateTemperatureDisplay() {
        if (weatherData != null) {
            double temperature = (double) weatherData.get("temperature");
            if (isCelsius) {
                temperatureText.setText(String.format("%.0f°", temperature));
            } else {
                double fahrenheit = (temperature * 9/5) + 32;
                temperatureText.setText(String.format("%.0f°", fahrenheit));
            }
        }
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        
        // Update date (format like "Monday, 27th April")
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, d");
        String day = now.format(dateFormatter);
        String month = now.format(DateTimeFormatter.ofPattern("MMMM"));
        int dayOfMonth = now.getDayOfMonth();
        String dayWithSuffix = dayOfMonth + getDayOfMonthSuffix(dayOfMonth);
        
        currentDateLabel.setText(day.replace(String.valueOf(dayOfMonth), dayWithSuffix) + " " + month);
        
        // Update time (format like "6:27am")
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
        currentTimeLabel.setText(now.format(timeFormatter).toLowerCase());
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


    private String getIconFileName(String condition) {
        switch(condition) {
            case "Clear": return "clear.png";
            case "Rain": return "rain.png";
            case "Snow": return "snow.png";
            case "Cloudy":
            default: return "cloudy.png";
        }
    }

    private String convertWeatherCode(long weathercode) {
        if(weathercode == 0L) {
            return "Clear";
        } else if(weathercode <= 3L && weathercode > 0L) {
            return "Cloudy";
        } else if((weathercode >= 51L && weathercode <= 67L )
                    || (weathercode >= 80L && weathercode <= 99L)) {
            return "Rain";
        } else if(weathercode >= 71L && weathercode <= 77L) {
            return "Snow";
        }
        return "Cloudy";
    }

    private ImageIcon loadImage(String resourcePath){
        try{
            // read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // returns an image icon so that our component can render it
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}
