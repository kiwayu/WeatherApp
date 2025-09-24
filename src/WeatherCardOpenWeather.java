import org.json.simple.JSONObject;

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

/**
 * Weather Card Application with OpenWeatherMap Icons
 * Uses real weather icons from OpenWeatherMap API
 */
public class WeatherCardOpenWeather extends JFrame {
    
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
    private JLabel statusLabel;
    
    // City suggestions database
    private final List<String> WORLD_CITIES = Arrays.asList(
        "London", "New York", "Tokyo", "Paris", "Berlin", "Madrid", "Rome", "Amsterdam", "Vienna", "Prague",
        "Barcelona", "Munich", "Stockholm", "Copenhagen", "Oslo", "Helsinki", "Dublin", "Edinburgh", "Lisbon",
        "Athens", "Budapest", "Warsaw", "Zurich", "Geneva", "Brussels", "Luxembourg", "Monaco", "San Marino",
        "Moscow", "St. Petersburg", "Kiev", "Minsk", "Riga", "Vilnius", "Tallinn", "Reykjavik", "Istanbul",
        "Ankara", "Cairo", "Alexandria", "Casablanca", "Tunis", "Algiers", "Rabat", "Lagos", "Accra", "Nairobi",
        "Cape Town", "Johannesburg", "Durban", "Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata", "Beijing",
        "Shanghai", "Guangzhou", "Seoul", "Busan", "Bangkok", "Manila", "Jakarta", "Sydney", "Melbourne", "Brisbane",
        "Auckland", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego",
        "Dallas", "San Jose", "Austin", "Jacksonville", "Fort Worth", "Columbus", "Charlotte", "San Francisco",
        "Indianapolis", "Seattle", "Denver", "Washington", "Boston", "Nashville", "Detroit", "Portland", "Las Vegas",
        "Memphis", "Louisville", "Baltimore", "Milwaukee", "Albuquerque", "Tucson", "Fresno", "Sacramento",
        "Kansas City", "Mesa", "Atlanta", "Omaha", "Colorado Springs", "Raleigh", "Miami", "Oakland", "Minneapolis",
        "Tampa", "New Orleans", "Cleveland", "Toronto", "Montreal", "Calgary", "Ottawa", "Edmonton", "Vancouver"
    );
    
    public WeatherCardOpenWeather() {
        setTitle("Weather Card - OpenWeatherMap Icons");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Preload common weather icons
        OpenWeatherIcon.preloadIcons();
        
        // Initialize with sample data
        initializeSampleData();
        
        // Calculate total window dimensions
        int totalWidth = CARD_WIDTH + (WINDOW_MARGIN * 2);
        int totalHeight = CARD_HEIGHT + SEARCH_HEIGHT + SEARCH_GAP + (WINDOW_MARGIN * 2) + 30; // Extra space for status
        
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
        
        // Create status label
        createStatusLabel();
        
        // Add components to main panel
        mainPanel.add(searchField);
        mainPanel.add(cardPanel);
        mainPanel.add(suggestionScrollPane);
        mainPanel.add(statusLabel);
        
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
        searchField = new JTextField("Enter city name...");
        searchField.setBounds(WINDOW_MARGIN, WINDOW_MARGIN, CARD_WIDTH, SEARCH_HEIGHT);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setForeground(new Color(100, 100, 100));
        searchField.setBackground(new Color(255, 255, 255, 230));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 2, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        
        // Add document listener for real-time suggestions
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Enter city name...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(50, 50, 50));
                }
                updateSuggestions();
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
        suggestionList.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        suggestionList.setSelectionBackground(new Color(100, 149, 237, 100));
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
        
        suggestionScrollPane = new JScrollPane(suggestionList);
        suggestionScrollPane.setBounds(WINDOW_MARGIN, WINDOW_MARGIN + SEARCH_HEIGHT + 5, CARD_WIDTH, 120);
        suggestionScrollPane.setVisible(false);
        suggestionScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
    }
    
    private void createStatusLabel() {
        statusLabel = new JLabel("Ready - Using OpenWeatherMap Icons");
        statusLabel.setBounds(WINDOW_MARGIN, WINDOW_MARGIN + SEARCH_HEIGHT + SEARCH_GAP + CARD_HEIGHT + 5, CARD_WIDTH, 20);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void updateSuggestions() {
        String input = searchField.getText().trim();
        if (input.isEmpty() || input.equals("Enter city name...")) {
            suggestionScrollPane.setVisible(false);
            return;
        }
        
        List<String> matches = WORLD_CITIES.stream()
            .filter(city -> city.toLowerCase().startsWith(input.toLowerCase()))
            .limit(6)
            .collect(Collectors.toList());
        
        if (matches.isEmpty()) {
            suggestionScrollPane.setVisible(false);
        } else {
            suggestionList.setListData(matches.toArray(new String[0]));
            suggestionScrollPane.setVisible(true);
        }
    }
    
    private void searchWeather() {
        String cityName = searchField.getText().trim();
        if (cityName.isEmpty() || cityName.equals("Enter city name...")) {
            return;
        }
        
        statusLabel.setText("Fetching weather data...");
        
        // Use SwingWorker to fetch data in background
        SwingWorker<JSONObject, Void> worker = new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() throws Exception {
                return WeatherApp.getWeatherData(cityName);
            }
            
            @Override
            protected void done() {
                try {
                    JSONObject weatherData = get();
                    if (weatherData != null) {
                        updateWeatherData(cityName, weatherData);
                        statusLabel.setText("Weather data updated - Icons from OpenWeatherMap");
                    } else {
                        statusLabel.setText("City not found - Please try another city");
                        JOptionPane.showMessageDialog(WeatherCardOpenWeather.this, 
                            "Could not find weather data for '" + cityName + "'.", 
                            "Location Not Found", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error fetching weather data");
                    JOptionPane.showMessageDialog(WeatherCardOpenWeather.this, 
                        "An error occurred while fetching weather data.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
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
     * Weather Card Panel with OpenWeatherMap Icons
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
            drawWeatherIcon(g2d);  // Now uses OpenWeatherMap icons
            drawTemperature(g2d);
            drawDayText(g2d);
            drawDividerLine(g2d);
            drawHourlyForecast(g2d);  // Now uses OpenWeatherMap mini icons
            
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
            // Position: 140px from top, size: 80px × 80px
            int iconCenterX = CARD_WIDTH / 2;
            int iconCenterY = 140 + 40;
            int iconSize = 80;
            
            String weatherType = currentWeatherData.getWeatherType();
            if (weatherType == null) weatherType = "Clear";
            
            // Determine if it's day or night based on current time
            LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            boolean isDayTime = hour >= 6 && hour < 18;
            
            // Get OpenWeatherMap icon
            ImageIcon weatherIcon = OpenWeatherIcon.getWeatherIcon(weatherType, iconSize, isDayTime);
            
            if (weatherIcon != null) {
                // Draw the OpenWeatherMap icon
                int iconX = iconCenterX - iconSize / 2;
                int iconY = iconCenterY - iconSize / 2;
                weatherIcon.paintIcon(this, g2d, iconX, iconY);
            } else {
                // Fallback to custom icon if OpenWeatherMap icon fails
                WeatherIcon.drawSunIcon(g2d, iconCenterX, iconCenterY, iconSize, PRIMARY_TEXT);
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
            
            // OpenWeatherMap Mini Icon
            ImageIcon miniIcon = OpenWeatherIcon.getMiniWeatherIcon(forecast.getWeatherType(), 20);
            if (miniIcon != null) {
                int iconX = x + (width - 20) / 2;
                int iconY = y + 25;
                miniIcon.paintIcon(cardPanel, g2d, iconX, iconY);
            } else {
                // Fallback to custom mini icon
                int iconCenterX = x + width / 2;
                int iconCenterY = y + 35;
                WeatherIcon.drawMiniIcon(g2d, iconCenterX, iconCenterY, 20, forecast.getWeatherType(), PRIMARY_TEXT);
            }
            
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
            new WeatherCardOpenWeather();
        });
    }
}
