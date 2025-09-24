import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Hyper-Detailed Weather Card with EXACT visual specifications
 * Dimensions: 280px width × 480px height
 * All positioning and typography exactly as specified
 */
public class HyperDetailedWeatherCard extends JFrame {
    
    // EXACT specifications
    private static final int CARD_WIDTH = 280;
    private static final int CARD_HEIGHT = 480;
    private static final int CORNER_RADIUS = 20;
    private static final int PADDING_TOP = 24;
    private static final int PADDING_HORIZONTAL = 20;
    private static final int PADDING_BOTTOM = 20;
    private static final int WINDOW_MARGIN = 10;
    
    // Colors - EXACT specifications
    private static final Color GRADIENT_START = new Color(255, 183, 94); // #FFB75E
    private static final Color GRADIENT_END = new Color(237, 143, 3);    // #ED8F03
    private static final Color PRIMARY_TEXT = Color.WHITE;              // #FFFFFF
    private static final Color SECONDARY_TEXT = new Color(255, 255, 255, 204); // rgba(255,255,255,0.8)
    private static final Color TERTIARY_TEXT = new Color(255, 255, 255, 230);  // rgba(255,255,255,0.9)
    private static final Color HOURLY_TEXT = new Color(255, 255, 255, 179);    // rgba(255,255,255,0.7)
    private static final Color DIVIDER_COLOR = new Color(255, 255, 255, 77);   // rgba(255,255,255,0.3)
    
    private WeatherData currentWeatherData;
    private WeatherCardPanel cardPanel;
    
    public HyperDetailedWeatherCard() {
        setTitle("Hyper-Detailed Weather Card");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Initialize with sample data
        initializeSampleData();
        
        // Create main panel with exact dimensions
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(
            CARD_WIDTH + (WINDOW_MARGIN * 2), 
            CARD_HEIGHT + (WINDOW_MARGIN * 2)
        ));
        mainPanel.setBackground(new Color(131, 96, 195)); // Dark purple background
        
        // Create the weather card
        cardPanel = new WeatherCardPanel();
        cardPanel.setBounds(WINDOW_MARGIN, WINDOW_MARGIN, CARD_WIDTH, CARD_HEIGHT);
        mainPanel.add(cardPanel);
        
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
        
        // Format day: "Monday"
        String dayOfWeek = now.format(DateTimeFormatter.ofPattern("EEEE"));
        
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
     * Custom panel that renders the weather card with EXACT specifications
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
            
            // Draw drop shadow (0px 8px 32px rgba(0,0,0,0.15))
            drawDropShadow(g2d);
            
            // Draw card background with gradient
            drawCardBackground(g2d);
            
            // Draw all content with EXACT positioning
            drawDateText(g2d);      // 24px from top
            drawTimeText(g2d);      // 40px from top
            drawLocationText(g2d);  // 92px from top
            drawWeatherIcon(g2d);   // 140px from top
            drawTemperature(g2d);   // 240px from top
            drawDayText(g2d);       // 320px from top
            drawDividerLine(g2d);   // 360px from top
            drawHourlyForecast(g2d); // 380px from top
            
            g2d.dispose();
        }
        
        private void drawDropShadow(Graphics2D g2d) {
            // Create shadow effect (0px 8px 32px rgba(0,0,0,0.15))
            g2d.setColor(new Color(0, 0, 0, 38)); // rgba(0,0,0,0.15)
            
            // Multiple shadow layers for blur effect
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
        
        private void drawCardBackground(Graphics2D g2d) {
            // Linear gradient from #FFB75E (top) to #ED8F03 (bottom) at 135° angle
            GradientPaint gradient = new GradientPaint(
                0, 0, GRADIENT_START,
                CARD_WIDTH, CARD_HEIGHT, GRADIENT_END
            );
            g2d.setPaint(gradient);
            
            // Draw rounded rectangle card
            RoundRectangle2D card = new RoundRectangle2D.Double(
                0, 0, CARD_WIDTH, CARD_HEIGHT, CORNER_RADIUS, CORNER_RADIUS
            );
            g2d.fill(card);
        }
        
        private void drawDateText(Graphics2D g2d) {
            // Position: 24px from top, centered horizontally
            // Font: System font, 14px, Regular weight
            // Color: rgba(255,255,255,0.8)
            // Letter spacing: 0.3px (simulated with font metrics)
            
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
            // Position: 40px from top (16px below date)
            // Font: System font, 48px, Ultra Light weight
            // Color: #FFFFFF
            // Letter spacing: -1px
            
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
            // Position: 92px from top (4px below time)
            // Font: System font, 16px, Regular weight
            // Color: rgba(255,255,255,0.9)
            // Letter spacing: 0.5px
            
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
            // Position: 140px from top (48px below location)
            // Size: 80px × 80px
            // Color: #FFFFFF
            // Style: Line art, 2px stroke width
            
            int iconCenterX = CARD_WIDTH / 2;
            int iconCenterY = 140 + 40; // 140px from top + half icon size
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
            // Position: 240px from top (20px below icon)
            // Font: System font, 72px, Ultra Thin weight
            // Color: #FFFFFF
            // Letter spacing: -2px
            
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
            // Position: 320px from top (20px below temperature)
            // Font: System font, 18px, Regular weight
            // Color: rgba(255,255,255,0.9)
            
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
            // Position: 360px from top
            // Width: 240px (full width minus padding)
            // Height: 1px
            // Color: rgba(255,255,255,0.3)
            
            g2d.setColor(DIVIDER_COLOR);
            g2d.setStroke(new BasicStroke(1.0f));
            
            int lineY = 360;
            int lineStartX = PADDING_HORIZONTAL;
            int lineEndX = CARD_WIDTH - PADDING_HORIZONTAL;
            
            g2d.drawLine(lineStartX, lineY, lineEndX, lineY);
        }
        
        private void drawHourlyForecast(Graphics2D g2d) {
            // Position: 380px from top
            // Layout: Horizontal row, evenly spaced
            // Each mini card: 50px wide
            
            List<WeatherData.HourlyForecast> hourlyData = currentWeatherData.getHourlyData();
            if (hourlyData.isEmpty()) return;
            
            int forecastY = 380;
            int cardWidth = 50;
            int totalWidth = CARD_WIDTH - (PADDING_HORIZONTAL * 2); // 240px
            int spacing = (totalWidth - (cardWidth * 4)) / 3; // Space between cards
            
            for (int i = 0; i < Math.min(4, hourlyData.size()); i++) {
                WeatherData.HourlyForecast forecast = hourlyData.get(i);
                int cardX = PADDING_HORIZONTAL + (i * (cardWidth + spacing));
                
                drawMiniWeatherCard(g2d, cardX, forecastY, cardWidth, forecast);
            }
        }
        
        private void drawMiniWeatherCard(Graphics2D g2d, int x, int y, int width, WeatherData.HourlyForecast forecast) {
            // Time (12px font, rgba(255,255,255,0.7))
            Font timeFont = new Font("Segoe UI", Font.PLAIN, 12);
            g2d.setFont(timeFont);
            g2d.setColor(HOURLY_TEXT);
            
            FontMetrics timeFm = g2d.getFontMetrics();
            int timeWidth = timeFm.stringWidth(forecast.getTime());
            int timeX = x + (width - timeWidth) / 2;
            g2d.drawString(forecast.getTime(), timeX, y + 15);
            
            // Icon (20px size)
            int iconCenterX = x + width / 2;
            int iconCenterY = y + 35;
            WeatherIcon.drawMiniIcon(g2d, iconCenterX, iconCenterY, 20, forecast.getWeatherType(), PRIMARY_TEXT);
            
            // Temperature (14px font, #FFFFFF)
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
    
    // Method to update weather data (for future API integration)
    public void updateWeatherData(WeatherData newData) {
        this.currentWeatherData = newData;
        updateCurrentTime();
        cardPanel.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HyperDetailedWeatherCard();
        });
    }
}
