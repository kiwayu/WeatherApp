import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Complete Weather Card Application with API Integration
 * Includes search functionality and auto-suggestions
 */
public class WeatherCardWithAPI extends JFrame {
    
    // EXACT specifications
    private static final int CARD_WIDTH = 280;
    private static final int CARD_HEIGHT = 480;
    private static final int CORNER_RADIUS = 20;
    private static final int PADDING_TOP = 24;
    private static final int PADDING_HORIZONTAL = 20;
    private static final int PADDING_BOTTOM = 20;
    private static final int WINDOW_MARGIN = 30;
    
    // Enhanced window dimensions for search functionality
    private static final int SEARCH_HEIGHT = 45;
    private static final int SEARCH_GAP = 15;
    
    // Colors - EXACT specifications
    private static final Color GRADIENT_START = new Color(255, 183, 94); // #FFB75E
    private static final Color GRADIENT_END = new Color(237, 143, 3);    // #ED8F03
    private static final Color PRIMARY_TEXT = Color.WHITE;
    private static final Color SECONDARY_TEXT = new Color(255, 255, 255, 204);
    private static final Color TERTIARY_TEXT = new Color(255, 255, 255, 230);
    private static final Color HOURLY_TEXT = new Color(255, 255, 255, 179);
    private static final Color DIVIDER_COLOR = new Color(255, 255, 255, 77);
    
    private WeatherData currentWeatherData;
    private WeatherCardPanel cardPanel;
    private JTextField searchField;
    private JList<String> suggestionList;
    private JScrollPane suggestionScrollPane;
    
    // City suggestions database
    // Enhanced search with caching for better performance
    private final Map<String, List<LocationSuggestion>> searchCache = new ConcurrentHashMap<>();
    private Timer suggestionTimer;
    
    // Location suggestion with country information
    private static class LocationSuggestion {
        final String name;
        final String country;
        final String displayText;
        
        LocationSuggestion(String name, String country) {
            this.name = name;
            this.country = country;
            this.displayText = name + (country != null && !country.isEmpty() ? ", " + country : "");
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
    
    public WeatherCardWithAPI() {
        setTitle("Weather Card - Complete Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Initialize with sample data
        initializeSampleData();
        
        // Calculate total window dimensions
        int totalWidth = CARD_WIDTH + (WINDOW_MARGIN * 2);
        int totalHeight = CARD_HEIGHT + SEARCH_HEIGHT + SEARCH_GAP + (WINDOW_MARGIN * 2);
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dark purple gradient background
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(131, 96, 195),
                    getWidth(), getHeight(), new Color(46, 191, 145)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(totalWidth, totalHeight));
        
        // Create search field
        createSearchField();
        
        // Create weather card
        cardPanel = new WeatherCardPanel();
        cardPanel.setBounds(WINDOW_MARGIN, WINDOW_MARGIN + SEARCH_HEIGHT + SEARCH_GAP, CARD_WIDTH, CARD_HEIGHT);
        
        // Create suggestion list
        createSuggestionList();
        
        // Add components to main panel
        mainPanel.add(searchField);
        mainPanel.add(cardPanel);
        mainPanel.add(suggestionScrollPane);
        
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        
        // Update time every minute
        Timer timer = new Timer(60000, e -> {
            updateCurrentTime();
            cardPanel.repaint();
        });
        timer.start();
        
        setVisible(true);
    }
    
    private void createSearchField() {
        searchField = new JTextField("Enter city name...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint rounded background
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Paint border
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        searchField.setBounds(WINDOW_MARGIN, WINDOW_MARGIN, CARD_WIDTH, SEARCH_HEIGHT);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setForeground(new Color(100, 100, 100));
        searchField.setBackground(new Color(255, 255, 255, 230));
        searchField.setOpaque(false);
        searchField.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        
        // Add document listener for real-time suggestions with debouncing
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { debounceUpdateSuggestions(); }
            @Override
            public void removeUpdate(DocumentEvent e) { debounceUpdateSuggestions(); }
            @Override
            public void changedUpdate(DocumentEvent e) { debounceUpdateSuggestions(); }
        });
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Enter city name...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(50, 50, 50));
                }
                debounceUpdateSuggestions();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Enter city name...");
                    searchField.setForeground(new Color(100, 100, 100));
                }
                Timer timer = new Timer(150, evt -> suggestionScrollPane.setVisible(false));
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        searchField.addActionListener(e -> {
            searchWeather();
            suggestionScrollPane.setVisible(false);
        });
    }
    
    private void createSuggestionList() {
        suggestionList = new JList<>();
        suggestionList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        suggestionList.setBackground(new Color(255, 255, 255, 240));
        suggestionList.setForeground(new Color(60, 60, 60));
        suggestionList.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        suggestionList.setSelectionBackground(new Color(GRADIENT_START.getRed(), GRADIENT_START.getGreen(), GRADIENT_START.getBlue(), 100));
        suggestionList.setSelectionForeground(Color.WHITE);
        suggestionList.setVisibleRowCount(6);
        
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String selectedCity = suggestionList.getSelectedValue();
                    if (selectedCity != null) {
                        searchField.setText(selectedCity);
                        suggestionScrollPane.setVisible(false);
                        searchWeather();
                    }
                }
            }
        });
        
        suggestionScrollPane = new JScrollPane(suggestionList) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint rounded background
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        suggestionScrollPane.setBounds(WINDOW_MARGIN, WINDOW_MARGIN + SEARCH_HEIGHT + 5, CARD_WIDTH, 120);
        suggestionScrollPane.setVisible(false);
        suggestionScrollPane.setOpaque(false);
        suggestionScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200, 150), 1, true),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        suggestionScrollPane.setBackground(new Color(255, 255, 255, 250));
        suggestionScrollPane.getViewport().setOpaque(false);
    }
    
    private void debounceUpdateSuggestions() {
        if (suggestionTimer != null) {
            suggestionTimer.stop();
        }
        suggestionTimer = new Timer(300, e -> updateSuggestions());
        suggestionTimer.setRepeats(false);
        suggestionTimer.start();
    }
    
    private void updateSuggestions() {
        String input = searchField.getText().trim();
        if (input.isEmpty() || input.equals("Enter city name...") || input.length() < 2) {
            suggestionScrollPane.setVisible(false);
            return;
        }
        
        // Check cache first
        String cacheKey = input.toLowerCase();
        if (searchCache.containsKey(cacheKey)) {
            List<LocationSuggestion> cachedResults = searchCache.get(cacheKey);
            displaySuggestions(cachedResults);
            return;
        }
        
        // Fetch suggestions from geocoding API asynchronously
        CompletableFuture.supplyAsync(() -> fetchLocationSuggestions(input))
            .thenAccept(suggestions -> {
                SwingUtilities.invokeLater(() -> {
                    if (suggestions != null && !suggestions.isEmpty()) {
                        searchCache.put(cacheKey, suggestions);
                        displaySuggestions(suggestions);
                    } else {
                        suggestionScrollPane.setVisible(false);
                    }
                });
            });
    }
    
    private List<LocationSuggestion> fetchLocationSuggestions(String input) {
        try {
            JSONArray locationData = WeatherApp.getLocationData(input);
            if (locationData == null) return new ArrayList<>();
            
            List<LocationSuggestion> suggestions = new ArrayList<>();
            for (int i = 0; i < Math.min(locationData.size(), 6); i++) {
                JSONObject location = (JSONObject) locationData.get(i);
                String name = (String) location.get("name");
                String country = (String) location.get("country");
                suggestions.add(new LocationSuggestion(name, country));
            }
            return suggestions;
        } catch (Exception e) {
            System.err.println("Error fetching location suggestions: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void displaySuggestions(List<LocationSuggestion> suggestions) {
        if (suggestions.isEmpty()) {
            suggestionScrollPane.setVisible(false);
        } else {
            String[] suggestionArray = suggestions.stream()
                .map(LocationSuggestion::toString)
                .toArray(String[]::new);
            suggestionList.setListData(suggestionArray);
            suggestionScrollPane.setVisible(true);
        }
    }
    
    private void searchWeather() {
        String cityName = searchField.getText().trim();
        if (cityName.isEmpty() || cityName.equals("Enter city name...")) {
            return;
        }
        
        try {
            JSONObject weatherData = WeatherApp.getWeatherData(cityName);
            if (weatherData != null) {
                updateWeatherData(cityName, weatherData);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Could not find weather data for '" + cityName + "'.", 
                    "Location Not Found", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred while fetching weather data.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateWeatherData(String cityName, JSONObject apiData) {
        currentWeatherData.setLocation(cityName);
        currentWeatherData.setTemperature((Double) apiData.get("temperature"));
        currentWeatherData.setWeatherType((String) apiData.get("weather_condition"));
        
        // Generate sample hourly data based on current weather
        double baseTemp = currentWeatherData.getTemperature();
        String condition = currentWeatherData.getWeatherType();
        
        currentWeatherData.getHourlyData().clear();
        currentWeatherData.addHourlyForecast(new WeatherData.HourlyForecast("Now", baseTemp, condition));
        currentWeatherData.addHourlyForecast(new WeatherData.HourlyForecast("+3h", baseTemp + 2, condition));
        currentWeatherData.addHourlyForecast(new WeatherData.HourlyForecast("+6h", baseTemp - 1, condition));
        currentWeatherData.addHourlyForecast(new WeatherData.HourlyForecast("+9h", baseTemp - 3, condition));
        
        updateCurrentTime();
        cardPanel.repaint();
    }
    
    private void initializeSampleData() {
        currentWeatherData = new WeatherData();
        updateCurrentTime();
        currentWeatherData.setLocation("London");
        currentWeatherData.setTemperature(17);
        currentWeatherData.setWeatherType("Clear");
        
        // Add hourly forecast data
        currentWeatherData.addHourlyForecast(new WeatherData.HourlyForecast("9am", 15, "Clear"));
        currentWeatherData.addHourlyForecast(new WeatherData.HourlyForecast("12pm", 17, "Clear"));
        currentWeatherData.addHourlyForecast(new WeatherData.HourlyForecast("3pm", 19, "Clear"));
        currentWeatherData.addHourlyForecast(new WeatherData.HourlyForecast("6pm", 16, "Cloudy"));
    }
    
    private void updateCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        
        // Format date: "Monday, 27th april"
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE, d");
        String day = now.format(dayFormatter);
        String month = now.format(DateTimeFormatter.ofPattern("MMMM")).toLowerCase();
        int dayOfMonth = now.getDayOfMonth();
        String dayWithSuffix = dayOfMonth + getDayOfMonthSuffix(dayOfMonth);
        String formattedDate = day.replace(String.valueOf(dayOfMonth), dayWithSuffix) + " " + month;
        
        // Format time: "9:43am"
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
        String formattedTime = now.format(timeFormatter).toLowerCase();
        
        currentWeatherData.setDate(formattedDate);
        currentWeatherData.setTime(formattedTime);
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
    
    /**
     * Reuse the exact same WeatherCardPanel from HyperDetailedWeatherCard
     */
    private class WeatherCardPanel extends JPanel {
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Enable high-quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            // Update gradient colors based on weather condition
            Color[] gradientColors = getWeatherGradientColors(currentWeatherData.getWeatherType());
            
            // Draw drop shadow
            drawDropShadow(g2d);
            
            // Draw card background with weather-specific gradient
            drawCardBackground(g2d, gradientColors[0], gradientColors[1]);
            
            // Draw all content with EXACT positioning
            drawDateText(g2d);
            drawTimeText(g2d);
            drawLocationText(g2d);
            drawWeatherIcon(g2d);
            drawTemperature(g2d);
            drawDayText(g2d);
            drawDividerLine(g2d);
            drawHourlyForecast(g2d);
            
            g2d.dispose();
        }
        
        private Color[] getWeatherGradientColors(String weatherType) {
            if (weatherType == null) weatherType = "Clear";
            
            switch (weatherType.toLowerCase()) {
                case "clear":
                case "sunny":
                    return new Color[]{new Color(255, 183, 94), new Color(237, 143, 3)}; // Orange
                case "cloudy":
                    return new Color[]{new Color(189, 195, 199), new Color(139, 157, 195)}; // Blue-gray
                case "rain":
                    return new Color[]{new Color(83, 105, 118), new Color(41, 46, 73)}; // Dark blue
                case "snow":
                    return new Color[]{new Color(230, 222, 221), new Color(197, 202, 233)}; // Light purple
                default:
                    return new Color[]{new Color(78, 205, 196), new Color(68, 160, 141)}; // Teal
            }
        }
        
        private void drawDropShadow(Graphics2D g2d) {
            g2d.setColor(new Color(0, 0, 0, 38));
            for (int i = 1; i <= 8; i++) {
                float alpha = 0.15f / 8 * (9 - i);
                g2d.setColor(new Color(0, 0, 0, (int)(alpha * 255)));
                RoundRectangle2D shadow = new RoundRectangle2D.Double(
                    i, 8 + i, CARD_WIDTH - (i * 2), CARD_HEIGHT - (i * 2), 
                    CORNER_RADIUS, CORNER_RADIUS
                );
                g2d.fill(shadow);
            }
        }
        
        private void drawCardBackground(Graphics2D g2d, Color startColor, Color endColor) {
            GradientPaint gradient = new GradientPaint(0, 0, startColor, CARD_WIDTH, CARD_HEIGHT, endColor);
            g2d.setPaint(gradient);
            
            RoundRectangle2D card = new RoundRectangle2D.Double(
                0, 0, CARD_WIDTH, CARD_HEIGHT, CORNER_RADIUS, CORNER_RADIUS
            );
            g2d.fill(card);
        }
        
        // All the drawing methods remain exactly the same as HyperDetailedWeatherCard
        private void drawDateText(Graphics2D g2d) {
            if (currentWeatherData.getDate() == null) return;
            
            Font font = new Font("Segoe UI", Font.PLAIN, 14);
            g2d.setFont(font);
            g2d.setColor(SECONDARY_TEXT);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(currentWeatherData.getDate());
            int x = (CARD_WIDTH - textWidth) / 2;
            int y = PADDING_TOP + fm.getAscent();
            
            g2d.drawString(currentWeatherData.getDate(), x, y);
        }
        
        private void drawTimeText(Graphics2D g2d) {
            if (currentWeatherData.getTime() == null) return;
            
            Font font = new Font("Segoe UI Light", Font.PLAIN, 48);
            g2d.setFont(font);
            g2d.setColor(PRIMARY_TEXT);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(currentWeatherData.getTime());
            int x = (CARD_WIDTH - textWidth) / 2;
            int y = 40 + fm.getAscent();
            
            g2d.drawString(currentWeatherData.getTime(), x, y);
        }
        
        private void drawLocationText(Graphics2D g2d) {
            if (currentWeatherData.getLocation() == null) return;
            
            Font font = new Font("Segoe UI", Font.PLAIN, 16);
            g2d.setFont(font);
            g2d.setColor(TERTIARY_TEXT);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(currentWeatherData.getLocation());
            int x = (CARD_WIDTH - textWidth) / 2;
            int y = 92 + fm.getAscent();
            
            g2d.drawString(currentWeatherData.getLocation(), x, y);
        }
        
        private void drawWeatherIcon(Graphics2D g2d) {
            int iconCenterX = CARD_WIDTH / 2;
            int iconCenterY = 140 + 40;
            int iconSize = 80;
            
            String weatherType = currentWeatherData.getWeatherType();
            if (weatherType == null) weatherType = "Clear";
            
            switch (weatherType.toLowerCase()) {
                case "clear":
                case "sunny":
                    WeatherIcon.drawSunIcon(g2d, iconCenterX, iconCenterY, iconSize, PRIMARY_TEXT);
                    break;
                case "cloudy":
                    WeatherIcon.drawCloudIcon(g2d, iconCenterX, iconCenterY, iconSize, PRIMARY_TEXT);
                    break;
                case "rain":
                    WeatherIcon.drawRainIcon(g2d, iconCenterX, iconCenterY, iconSize, PRIMARY_TEXT);
                    break;
                case "snow":
                    WeatherIcon.drawSnowIcon(g2d, iconCenterX, iconCenterY, iconSize, PRIMARY_TEXT);
                    break;
                default:
                    WeatherIcon.drawSunIcon(g2d, iconCenterX, iconCenterY, iconSize, PRIMARY_TEXT);
                    break;
            }
        }
        
        private void drawTemperature(Graphics2D g2d) {
            String tempText = (int)currentWeatherData.getTemperature() + "°";
            
            Font font = new Font("Segoe UI Light", Font.PLAIN, 72);
            g2d.setFont(font);
            g2d.setColor(PRIMARY_TEXT);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(tempText);
            int x = (CARD_WIDTH - textWidth) / 2;
            int y = 240 + fm.getAscent();
            
            g2d.drawString(tempText, x, y);
        }
        
        private void drawDayText(Graphics2D g2d) {
            String dayText = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE"));
            
            Font font = new Font("Segoe UI", Font.PLAIN, 18);
            g2d.setFont(font);
            g2d.setColor(TERTIARY_TEXT);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(dayText);
            int x = (CARD_WIDTH - textWidth) / 2;
            int y = 320 + fm.getAscent();
            
            g2d.drawString(dayText, x, y);
        }
        
        private void drawDividerLine(Graphics2D g2d) {
            g2d.setColor(DIVIDER_COLOR);
            g2d.setStroke(new BasicStroke(1.0f));
            
            int lineY = 360;
            int lineStartX = PADDING_HORIZONTAL;
            int lineEndX = CARD_WIDTH - PADDING_HORIZONTAL;
            
            g2d.drawLine(lineStartX, lineY, lineEndX, lineY);
        }
        
        private void drawHourlyForecast(Graphics2D g2d) {
            List<WeatherData.HourlyForecast> hourlyData = currentWeatherData.getHourlyData();
            if (hourlyData.isEmpty()) return;
            
            int forecastY = 380;
            int cardWidth = 50;
            int totalWidth = CARD_WIDTH - (PADDING_HORIZONTAL * 2);
            int spacing = (totalWidth - (cardWidth * 4)) / 3;
            
            for (int i = 0; i < Math.min(4, hourlyData.size()); i++) {
                WeatherData.HourlyForecast forecast = hourlyData.get(i);
                int cardX = PADDING_HORIZONTAL + (i * (cardWidth + spacing));
                
                drawMiniWeatherCard(g2d, cardX, forecastY, cardWidth, forecast);
            }
        }
        
        private void drawMiniWeatherCard(Graphics2D g2d, int x, int y, int width, WeatherData.HourlyForecast forecast) {
            // Time
            Font timeFont = new Font("Segoe UI", Font.PLAIN, 12);
            g2d.setFont(timeFont);
            g2d.setColor(HOURLY_TEXT);
            
            FontMetrics timeFm = g2d.getFontMetrics();
            int timeWidth = timeFm.stringWidth(forecast.getTime());
            int timeX = x + (width - timeWidth) / 2;
            g2d.drawString(forecast.getTime(), timeX, y + 15);
            
            // Icon
            int iconCenterX = x + width / 2;
            int iconCenterY = y + 35;
            WeatherIcon.drawMiniIcon(g2d, iconCenterX, iconCenterY, 20, forecast.getWeatherType(), PRIMARY_TEXT);
            
            // Temperature
            Font tempFont = new Font("Segoe UI", Font.PLAIN, 14);
            g2d.setFont(tempFont);
            g2d.setColor(PRIMARY_TEXT);
            
            String tempText = (int)forecast.getTemperature() + "°";
            FontMetrics tempFm = g2d.getFontMetrics();
            int tempWidth = tempFm.stringWidth(tempText);
            int tempX = x + (width - tempWidth) / 2;
            g2d.drawString(tempText, tempX, y + 70);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WeatherCardWithAPI();
        });
    }
}
