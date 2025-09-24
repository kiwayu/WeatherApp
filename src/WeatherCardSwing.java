import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
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

public class WeatherCardSwing extends JFrame {
    
    private JSONObject weatherData;
    private boolean isCelsius = true;
    
    // UI Components
    private JTextField searchField;
    private WeatherCardPanel cardPanel;
    private JList<String> suggestionList;
    private JScrollPane suggestionScrollPane;
    
    // Weather data
    private String currentCity = "London";
    private double currentTemperature = 17.0;
    private String currentCondition = "Clear";
    
    // City suggestions database
    private final List<String> WORLD_CITIES = Arrays.asList(
        "London", "New York", "Tokyo", "Paris", "Berlin", "Madrid", "Rome", "Amsterdam", "Vienna", "Prague",
        "Barcelona", "Munich", "Stockholm", "Copenhagen", "Oslo", "Helsinki", "Dublin", "Edinburgh", "Lisbon",
        "Athens", "Budapest", "Warsaw", "Zurich", "Geneva", "Brussels", "Luxembourg", "Monaco", "San Marino",
        "Moscow", "St. Petersburg", "Kiev", "Minsk", "Riga", "Vilnius", "Tallinn", "Reykjavik", "Istanbul",
        "Ankara", "Cairo", "Alexandria", "Casablanca", "Tunis", "Algiers", "Rabat", "Lagos", "Accra", "Nairobi",
        "Cape Town", "Johannesburg", "Durban", "Marrakech", "Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata",
        "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Lucknow", "Kanpur", "Nagpur", "Indore", "Bhopal", "Visakhapatnam",
        "Beijing", "Shanghai", "Guangzhou", "Shenzhen", "Chengdu", "Hangzhou", "Wuhan", "Xi'an", "Suzhou", "Tianjin",
        "Seoul", "Busan", "Incheon", "Daegu", "Daejeon", "Gwangju", "Ulsan", "Suwon", "Bangkok", "Ho Chi Minh City",
        "Hanoi", "Da Nang", "Can Tho", "Bien Hoa", "Hue", "Nha Trang", "Manila", "Quezon City", "Davao", "Cebu",
        "Zamboanga", "Antipolo", "Pasig", "Taguig", "Jakarta", "Surabaya", "Bandung", "Bekasi", "Medan", "Tangerang",
        "Depok", "Semarang", "Palembang", "Makassar", "Sydney", "Melbourne", "Brisbane", "Perth", "Adelaide", "Gold Coast",
        "Newcastle", "Canberra", "Central Coast", "Wollongong", "Auckland", "Wellington", "Christchurch", "Hamilton",
        "Tauranga", "Napier-Hastings", "Dunedin", "Palmerston North", "Los Angeles", "Chicago", "Houston", "Phoenix",
        "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose", "Austin", "Jacksonville", "Fort Worth",
        "Columbus", "Charlotte", "San Francisco", "Indianapolis", "Seattle", "Denver", "Washington", "Boston",
        "El Paso", "Nashville", "Detroit", "Oklahoma City", "Portland", "Las Vegas", "Memphis", "Louisville",
        "Baltimore", "Milwaukee", "Albuquerque", "Tucson", "Fresno", "Sacramento", "Kansas City", "Mesa", "Atlanta",
        "Omaha", "Colorado Springs", "Raleigh", "Long Beach", "Virginia Beach", "Miami", "Oakland", "Minneapolis",
        "Tampa", "Tulsa", "New Orleans", "Wichita", "Cleveland", "Arlington", "Toronto", "Montreal", "Calgary",
        "Ottawa", "Edmonton", "Mississauga", "Winnipeg", "Vancouver", "Brampton", "Hamilton", "Quebec City",
        "Surrey", "Laval", "Halifax", "London", "Markham", "Vaughan", "Gatineau", "Saskatoon", "Longueuil",
        "Burnaby", "Regina", "Richmond", "Richmond Hill", "Oakville", "Burlington", "Greater Sudbury", "Sherbrooke",
        "Oshawa", "Saguenay", "Lévis", "Barrie", "Abbotsford", "St. Catharines", "Coquitlam", "Trois-Rivières",
        "Guelph", "Cambridge", "Whitby", "Ajman", "Mexico City", "Guadalajara", "Monterrey", "Puebla", "Tijuana",
        "León", "Juárez", "Torreón", "Querétaro", "San Luis Potosí", "Mérida", "Mexicali", "Aguascalientes",
        "Cuernavaca", "Saltillo", "Hermosillo", "Culiacán", "Chihuahua", "Morelia", "Xalapa", "Tampico",
        "São Paulo", "Rio de Janeiro", "Brasília", "Salvador", "Fortaleza", "Belo Horizonte", "Manaus", "Curitiba",
        "Recife", "Goiânia", "Belém", "Porto Alegre", "Guarulhos", "Campinas", "São Luís", "São Gonçalo",
        "Maceió", "Duque de Caxias", "Natal", "Teresina", "Campo Grande", "Nova Iguaçu", "São Bernardo do Campo",
        "João Pessoa", "Santo André", "Osasco", "Jaboatão dos Guararapes", "São José dos Campos", "Ribeirão Preto"
    );

    public WeatherCardSwing() {
        setTitle("Weather Card - Enhanced");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Calculate exact content dimensions for even visual spacing
        int margin = 30;
        int cardWidth = 280;
        int cardHeight = 480;
        int searchHeight = 45;
        int gapBetweenSearchAndCard = 15;
        
        // Content area dimensions (what we want to be evenly spaced)
        int contentWidth = (margin * 2) + cardWidth; // 340px
        int contentHeight = (margin * 2) + searchHeight + gapBetweenSearchAndCard + cardHeight; // 600px
        
        // Create main panel with visible background to show even spacing
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(contentWidth, contentHeight));
        mainPanel.setBackground(new Color(240, 240, 240)); // Light background to visualize spacing
        setContentPane(mainPanel);
        
        // Create a layered pane for component ordering
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, contentWidth, contentHeight);
        layeredPane.setOpaque(false);
        mainPanel.add(layeredPane);
        
        // Store reference for adding components
        this.getContentPane().add(layeredPane);
        ((JPanel)getContentPane()).remove(layeredPane); // Clean up
        
        createWeatherCard();
        createSearchField(); 
        createSuggestionList();
        
        // Add components to layered pane
        layeredPane.add(cardPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(searchField, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(suggestionScrollPane, JLayeredPane.MODAL_LAYER);
        
        // Clean up content pane and set layered pane
        setContentPane(layeredPane);
        
        // Use pack() to size window to exact content dimensions
        pack();
        setLocationRelativeTo(null);
        
        // Start timer for time updates
        Timer timer = new Timer(60000, e -> cardPanel.updateDateTime());
        timer.start();
        
        setVisible(true);
    }
    
    private void createSearchField() {
        searchField = new JTextField("Enter city name...");
        searchField.setBounds(30, 30, 280, 45); // Better positioning and size
        
        // Use a more modern font and styling
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // More modern font
        searchField.setForeground(new Color(100, 100, 100));
        searchField.setBackground(new Color(255, 255, 255, 230)); // Semi-transparent white
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
                // Hide suggestions when focus is lost
                Timer timer = new Timer(150, evt -> suggestionScrollPane.setVisible(false));
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        searchField.addActionListener(e -> {
            searchWeather();
            suggestionScrollPane.setVisible(false);
        });
        
        // Component will be added in main constructor
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
        suggestionScrollPane.setBounds(30, 80, 280, 120);
        suggestionScrollPane.setVisible(false);
        suggestionScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        
        // Component will be added in main constructor
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
    
    private void createWeatherCard() {
        cardPanel = new WeatherCardPanel();
        // EXACT equal margins calculation:
        // Left margin: 30px
        // Top: 30px (margin) + 45px (search) + 15px (gap) = 90px
        // Width: 280px (window width - 2 * margin)
        // Height: 480px (original card height)
        // This ensures: Bottom margin = 600 - 90 - 480 = 30px (EXACTLY equal to sides!)
        cardPanel.setBounds(30, 90, 280, 480);
        // Component will be added in main constructor
    }
    
    private void searchWeather() {
        String cityName = searchField.getText().trim();
        if (cityName.isEmpty() || cityName.equals("Enter city name...")) {
            return;
        }
        
        try {
            weatherData = WeatherApp.getWeatherData(cityName);
            if (weatherData != null) {
                currentCity = cityName;
                currentTemperature = (double) weatherData.get("temperature");
                currentCondition = (String) weatherData.get("weather_condition");
                cardPanel.updateWeatherData();
                cardPanel.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private class WeatherCardPanel extends JPanel {
        
        public WeatherCardPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(280, 480));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Enable anti-aliasing for smooth graphics
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Draw card background with gradient and shadow
            drawCardBackground(g2d);
            
            // Draw all text elements with exact positioning
            drawDateText(g2d);
            drawTimeText(g2d);
            drawLocationText(g2d);
            drawWeatherIcon(g2d);
            drawTemperatureText(g2d);
            drawDayText(g2d);
            drawDividerLine(g2d);
            drawHourlyForecast(g2d);
            
            g2d.dispose();
        }
        
        private void drawCardBackground(Graphics2D g2d) {
            // Create drop shadow first
            g2d.setColor(new Color(0, 0, 0, 38)); // rgba(0,0,0,0.15)
            RoundRectangle2D shadow = new RoundRectangle2D.Double(4, 12, 280, 480, 20, 20);
            g2d.fill(shadow);
            
            // Create gradient based on weather condition
            GradientPaint gradient = getWeatherGradient();
            g2d.setPaint(gradient);
            
            // Draw rounded rectangle card
            RoundRectangle2D card = new RoundRectangle2D.Double(0, 0, 280, 480, 20, 20);
            g2d.fill(card);
        }
        
        private GradientPaint getWeatherGradient() {
            Color startColor, endColor;
            
            switch (currentCondition) {
                case "Clear":
                    startColor = new Color(255, 183, 94);  // #FFB75E
                    endColor = new Color(237, 143, 3);     // #ED8F03
                    break;
                case "Cloudy":
                    startColor = new Color(189, 195, 199); // #BDC3C7
                    endColor = new Color(139, 157, 195);   // #8B9DC3
                    break;
                case "Rain":
                    startColor = new Color(83, 105, 118);  // #536976
                    endColor = new Color(41, 46, 73);      // #292E49
                    break;
                case "Snow":
                    startColor = new Color(230, 222, 221); // #E6DEDD
                    endColor = new Color(197, 202, 233);   // #C5CAE9
                    break;
                default:
                    startColor = new Color(78, 205, 196);  // #4ECDC4
                    endColor = new Color(68, 160, 141);    // #44A08D
                    break;
            }
            
            // 135° gradient: from top-left to bottom-right
            return new GradientPaint(0, 0, startColor, 280, 480, endColor);
        }
        
        private void drawDateText(Graphics2D g2d) {
            // Position: 24px from top, centered horizontally
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, d");
            String day = now.format(dateFormatter);
            String month = now.format(DateTimeFormatter.ofPattern("MMMM")).toLowerCase();
            int dayOfMonth = now.getDayOfMonth();
            String dayWithSuffix = dayOfMonth + getDayOfMonthSuffix(dayOfMonth);
            String dateString = day.replace(String.valueOf(dayOfMonth), dayWithSuffix) + " " + month;
            
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Modern font
            g2d.setColor(new Color(255, 255, 255, 204)); // rgba(255,255,255,0.8)
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(dateString);
            int x = (280 - textWidth) / 2; // Center horizontally
            int y = 24 + fm.getAscent(); // 24px from top + font ascent
            
            g2d.drawString(dateString, x, y);
        }
        
        private void drawTimeText(Graphics2D g2d) {
            // Position: 40px from top
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
            String timeString = now.format(timeFormatter).toLowerCase();
            
            g2d.setFont(new Font("Segoe UI Light", Font.PLAIN, 48)); // Light weight modern font
            g2d.setColor(Color.WHITE);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(timeString);
            int x = (280 - textWidth) / 2;
            int y = 40 + fm.getAscent();
            
            g2d.drawString(timeString, x, y);
        }
        
        private void drawLocationText(Graphics2D g2d) {
            // Position: 92px from top
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            g2d.setColor(new Color(255, 255, 255, 230)); // rgba(255,255,255,0.9)
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(currentCity);
            int x = (280 - textWidth) / 2;
            int y = 92 + fm.getAscent();
            
            g2d.drawString(currentCity, x, y);
        }
        
        private void drawWeatherIcon(Graphics2D g2d) {
            // Position: 140px from top, size: 80x80px
            int centerX = 140;
            int centerY = 140 + 40; // 140px from top + half icon size
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2.0f));
            
            switch (currentCondition) {
                case "Clear":
                    drawSunIcon(g2d, centerX, centerY);
                    break;
                case "Cloudy":
                    drawCloudIcon(g2d, centerX, centerY);
                    break;
                case "Rain":
                    drawRainIcon(g2d, centerX, centerY);
                    break;
                case "Snow":
                    drawSnowIcon(g2d, centerX, centerY);
                    break;
                default:
                    drawSunIcon(g2d, centerX, centerY);
                    break;
            }
        }
        
        private void drawSunIcon(Graphics2D g2d, int centerX, int centerY) {
            // Central circle
            g2d.drawOval(centerX - 12, centerY - 12, 24, 24);
            
            // 12 radiating lines (sun rays)
            for (int i = 0; i < 12; i++) {
                double angle = i * 30; // 360/12 = 30 degrees
                double radians = Math.toRadians(angle);
                
                int startX = (int) (centerX + Math.cos(radians) * 20);
                int startY = (int) (centerY + Math.sin(radians) * 20);
                int endX = (int) (centerX + Math.cos(radians) * 32);
                int endY = (int) (centerY + Math.sin(radians) * 32);
                
                g2d.drawLine(startX, startY, endX, endY);
            }
        }
        
        private void drawCloudIcon(Graphics2D g2d, int centerX, int centerY) {
            // Cloud shape with overlapping circles
            g2d.drawOval(centerX - 30, centerY - 5, 30, 30);  // Left circle
            g2d.drawOval(centerX - 15, centerY - 15, 36, 36); // Center circle
            g2d.drawOval(centerX + 10, centerY - 5, 24, 24);  // Right circle
        }
        
        private void drawRainIcon(Graphics2D g2d, int centerX, int centerY) {
            // Draw cloud first
            drawCloudIcon(g2d, centerX, centerY - 10);
            
            // Draw rain drops
            g2d.drawLine(centerX - 15, centerY + 20, centerX - 15, centerY + 35);
            g2d.drawLine(centerX, centerY + 25, centerX, centerY + 40);
            g2d.drawLine(centerX + 15, centerY + 20, centerX + 15, centerY + 35);
        }
        
        private void drawSnowIcon(Graphics2D g2d, int centerX, int centerY) {
            // Draw cloud first
            drawCloudIcon(g2d, centerX, centerY - 10);
            
            // Draw snowflakes as small filled circles
            g2d.fillOval(centerX - 15, centerY + 20, 4, 4);
            g2d.fillOval(centerX, centerY + 25, 4, 4);
            g2d.fillOval(centerX + 15, centerY + 20, 4, 4);
            g2d.fillOval(centerX - 8, centerY + 30, 4, 4);
            g2d.fillOval(centerX + 8, centerY + 30, 4, 4);
        }
        
        private void drawTemperatureText(Graphics2D g2d) {
            // Position: 240px from top
            String tempText = String.format("%.0f°", currentTemperature);
            
            g2d.setFont(new Font("Segoe UI Light", Font.PLAIN, 72)); // Ultra thin effect
            g2d.setColor(Color.WHITE);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(tempText);
            int x = (280 - textWidth) / 2;
            int y = 240 + fm.getAscent();
            
            g2d.drawString(tempText, x, y);
        }
        
        private void drawDayText(Graphics2D g2d) {
            // Position: 320px from top
            LocalDateTime now = LocalDateTime.now();
            String dayString = now.format(DateTimeFormatter.ofPattern("EEEE"));
            
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            g2d.setColor(new Color(255, 255, 255, 230)); // rgba(255,255,255,0.9)
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(dayString);
            int x = (280 - textWidth) / 2;
            int y = 320 + fm.getAscent();
            
            g2d.drawString(dayString, x, y);
        }
        
        private void drawDividerLine(Graphics2D g2d) {
            // Position: 360px from top, width: 240px (full width minus padding)
            g2d.setColor(new Color(255, 255, 255, 77)); // rgba(255,255,255,0.3)
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawLine(20, 360, 260, 360);
        }
        
        private void drawHourlyForecast(Graphics2D g2d) {
            // Position: 380px from top
            String[] times = {"Now", "+3h", "+6h", "+9h"};
            double[] temps = {currentTemperature, currentTemperature + 2, currentTemperature - 1, currentTemperature - 3};
            
            int startX = 20;
            int y = 380; // Back to original position
            int cardWidth = 60;
            
            for (int i = 0; i < 4; i++) {
                int cardX = startX + (i * cardWidth);
                drawMiniWeatherCard(g2d, cardX, y, times[i], String.format("%.0f°", temps[i]));
            }
        }
        
        private void drawMiniWeatherCard(Graphics2D g2d, int x, int y, String time, String temp) {
            // Time text (12px font, rgba(255,255,255,0.7))
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2d.setColor(new Color(255, 255, 255, 179)); // rgba(255,255,255,0.7)
            
            FontMetrics timeFm = g2d.getFontMetrics();
            int timeWidth = timeFm.stringWidth(time);
            g2d.drawString(time, x + (50 - timeWidth) / 2, y + 15);
            
            // Mini weather icon (20px size)
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.5f));
            int iconCenterX = x + 25;
            int iconCenterY = y + 35;
            g2d.drawOval(iconCenterX - 10, iconCenterY - 10, 20, 20);
            
            // Temperature text (14px font, #FFFFFF)
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2d.setColor(Color.WHITE);
            
            FontMetrics tempFm = g2d.getFontMetrics();
            int tempWidth = tempFm.stringWidth(temp);
            g2d.drawString(temp, x + (50 - tempWidth) / 2, y + 70);
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
        
        public void updateWeatherData() {
            repaint();
        }
        
        public void updateDateTime() {
            repaint();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherCardSwing());
    }
}
