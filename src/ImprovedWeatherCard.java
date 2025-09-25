import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Hyper-detailed weather card component that displays weather information
 * with precise visual specifications and professional styling.
 * 
 * Features:
 * - Exact 280x480px dimensions with 20px corner radius
 * - Orange gradient background (#FFB75E to #ED8F03)
 * - Custom sun icon with 12 radiating rays
 * - Typography with exact positioning and font specifications
 * - Hourly forecast display
 * 
 * @author Weather App Team
 * @version 2.0
 */
public class ImprovedWeatherCard extends JPanel {
    
    // Weather data fields
    private String dateText = "Monday, 27th april";
    private String timeText = "9:43am";
    private String locationText = "London";
    private String temperatureText = "17°";
    private String dayText = "Monday";
    private String[] hourlyTimes = {"10am", "11am", "12pm", "1pm"};
    private String[] hourlyTemps = {"15°", "16°", "18°", "19°"};
    
    // Color instances (created once for performance)
    private Color gradientStart;
    private Color gradientEnd;
    private Color primaryText;
    private Color secondaryText;
    private Color tertiaryText;
    private Color quaternaryText;
    
    /**
     * Creates a new weather card with default styling and data.
     */
    public ImprovedWeatherCard() {
        initializeColors();
        setupComponent();
        WeatherAppLogger.info("Weather card component initialized");
    }
    
    /**
     * Initialize color instances from constants.
     */
    private void initializeColors() {
        this.gradientStart = ColorUtils.fromRGB(WeatherAppConstants.Colors.GRADIENT_START_RGB);
        this.gradientEnd = ColorUtils.fromRGB(WeatherAppConstants.Colors.GRADIENT_END_RGB);
        this.primaryText = ColorUtils.fromRGBA(WeatherAppConstants.Colors.PRIMARY_TEXT);
        this.secondaryText = ColorUtils.fromRGBA(WeatherAppConstants.Colors.SECONDARY_TEXT);
        this.tertiaryText = ColorUtils.fromRGBA(WeatherAppConstants.Colors.TERTIARY_TEXT);
        this.quaternaryText = ColorUtils.fromRGBA(WeatherAppConstants.Colors.QUATERNARY_TEXT);
    }
    
    /**
     * Sets up the component properties and dimensions.
     */
    private void setupComponent() {
        setPreferredSize(new Dimension(
            WeatherAppConstants.UI.CARD_WIDTH, 
            WeatherAppConstants.UI.CARD_HEIGHT
        ));
        setSize(WeatherAppConstants.UI.CARD_WIDTH, WeatherAppConstants.UI.CARD_HEIGHT);
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        try {
            Graphics2D g2d = (Graphics2D) g.create();
            enableHighQualityRendering(g2d);
            
            drawDropShadow(g2d);
            drawCardBackground(g2d);
            drawAllTextElements(g2d);
            drawWeatherIcon(g2d);
            drawDividerLine(g2d);
            drawHourlyForecast(g2d);
            
            g2d.dispose();
            
        } catch (Exception e) {
            WeatherAppLogger.error("Error painting weather card", e);
        }
    }
    
    /**
     * Enables high-quality rendering hints for smooth graphics.
     * @param g2d Graphics2D context
     */
    private void enableHighQualityRendering(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
    
    /**
     * Draws the drop shadow effect behind the card.
     * @param g2d Graphics2D context
     */
    private void drawDropShadow(Graphics2D g2d) {
        int shadowSize = 16;
        int shadowOffset = 8;
        
        for (int i = shadowSize; i >= 0; i--) {
            float alpha = (float) (0.15 * (shadowSize - i) / shadowSize);
            g2d.setColor(new Color(0, 0, 0, (int) (alpha * 255)));
            
            RoundRectangle2D shadow = new RoundRectangle2D.Double(
                i - shadowSize, 
                i - shadowSize + shadowOffset, 
                WeatherAppConstants.UI.CARD_WIDTH + (shadowSize - i) * 2, 
                WeatherAppConstants.UI.CARD_HEIGHT + (shadowSize - i) * 2, 
                WeatherAppConstants.UI.CORNER_RADIUS + i, 
                WeatherAppConstants.UI.CORNER_RADIUS + i
            );
            g2d.fill(shadow);
        }
    }
    
    /**
     * Draws the card background with gradient.
     * @param g2d Graphics2D context
     */
    private void drawCardBackground(Graphics2D g2d) {
        GradientPaint gradient = new GradientPaint(
            0, 0, gradientStart,
            WeatherAppConstants.UI.CARD_WIDTH, WeatherAppConstants.UI.CARD_HEIGHT, gradientEnd
        );
        
        g2d.setPaint(gradient);
        
        RoundRectangle2D card = new RoundRectangle2D.Double(
            0, 0, 
            WeatherAppConstants.UI.CARD_WIDTH, 
            WeatherAppConstants.UI.CARD_HEIGHT, 
            WeatherAppConstants.UI.CORNER_RADIUS, 
            WeatherAppConstants.UI.CORNER_RADIUS
        );
        g2d.fill(card);
    }
    
    /**
     * Draws all text elements with exact positioning.
     * @param g2d Graphics2D context
     */
    private void drawAllTextElements(Graphics2D g2d) {
        drawDateText(g2d);
        drawTimeText(g2d);
        drawLocationText(g2d);
        drawTemperatureText(g2d);
        drawDayText(g2d);
    }
    
    /**
     * Draws the date text at the specified position.
     * @param g2d Graphics2D context
     */
    private void drawDateText(Graphics2D g2d) {
        drawCenteredText(g2d, dateText, 
            WeatherAppConstants.Typography.FONT_FAMILY,
            Font.PLAIN,
            WeatherAppConstants.Typography.DATE_FONT_SIZE,
            secondaryText,
            WeatherAppConstants.Layout.DATE_Y_POSITION);
    }
    
    /**
     * Draws the time text at the specified position.
     * @param g2d Graphics2D context
     */
    private void drawTimeText(Graphics2D g2d) {
        drawCenteredText(g2d, timeText,
            WeatherAppConstants.Typography.FONT_FAMILY,
            Font.PLAIN,
            WeatherAppConstants.Typography.TIME_FONT_SIZE,
            primaryText,
            WeatherAppConstants.Layout.TIME_Y_POSITION);
    }
    
    /**
     * Draws the location text at the specified position.
     * @param g2d Graphics2D context
     */
    private void drawLocationText(Graphics2D g2d) {
        drawCenteredText(g2d, locationText,
            WeatherAppConstants.Typography.FONT_FAMILY,
            Font.PLAIN,
            WeatherAppConstants.Typography.LOCATION_FONT_SIZE,
            quaternaryText,
            WeatherAppConstants.Layout.LOCATION_Y_POSITION);
    }
    
    /**
     * Draws the temperature text at the specified position.
     * @param g2d Graphics2D context
     */
    private void drawTemperatureText(Graphics2D g2d) {
        drawCenteredText(g2d, temperatureText,
            WeatherAppConstants.Typography.FONT_FAMILY,
            Font.PLAIN,
            WeatherAppConstants.Typography.TEMPERATURE_FONT_SIZE,
            primaryText,
            WeatherAppConstants.Layout.TEMPERATURE_Y_POSITION);
    }
    
    /**
     * Draws the day text at the specified position.
     * @param g2d Graphics2D context
     */
    private void drawDayText(Graphics2D g2d) {
        drawCenteredText(g2d, dayText,
            WeatherAppConstants.Typography.FONT_FAMILY,
            Font.PLAIN,
            WeatherAppConstants.Typography.DAY_FONT_SIZE,
            quaternaryText,
            WeatherAppConstants.Layout.DAY_Y_POSITION);
    }
    
    /**
     * Helper method to draw centered text.
     * @param g2d Graphics2D context
     * @param text text to draw
     * @param fontFamily font family name
     * @param fontStyle font style
     * @param fontSize font size
     * @param color text color
     * @param yPosition Y position from top
     */
    private void drawCenteredText(Graphics2D g2d, String text, String fontFamily, 
                                 int fontStyle, int fontSize, Color color, int yPosition) {
        Font font = new Font(fontFamily, fontStyle, fontSize);
        g2d.setFont(font);
        g2d.setColor(color);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (WeatherAppConstants.UI.CARD_WIDTH - textWidth) / 2;
        int y = yPosition + fm.getAscent();
        
        g2d.drawString(text, x, y);
    }
    
    /**
     * Draws the weather icon (sun symbol with rays).
     * @param g2d Graphics2D context
     */
    private void drawWeatherIcon(Graphics2D g2d) {
        int iconX = (WeatherAppConstants.UI.CARD_WIDTH - WeatherAppConstants.WeatherIcon.ICON_SIZE) / 2;
        int iconY = WeatherAppConstants.Layout.WEATHER_ICON_Y_POSITION;
        int centerX = iconX + WeatherAppConstants.WeatherIcon.ICON_SIZE / 2;
        int centerY = iconY + WeatherAppConstants.WeatherIcon.ICON_SIZE / 2;
        
        g2d.setColor(primaryText);
        g2d.setStroke(new BasicStroke(
            WeatherAppConstants.WeatherIcon.STROKE_WIDTH, 
            BasicStroke.CAP_ROUND, 
            BasicStroke.JOIN_ROUND
        ));
        
        // Draw sun rays
        for (int i = 0; i < WeatherAppConstants.WeatherIcon.RAY_COUNT; i++) {
            double angle = Math.toRadians(i * WeatherAppConstants.WeatherIcon.RAY_ANGLE_DEGREES);
            
            int innerRadius = WeatherAppConstants.WeatherIcon.CENTER_RADIUS + 4;
            int startX = centerX + (int) (Math.cos(angle) * innerRadius);
            int startY = centerY + (int) (Math.sin(angle) * innerRadius);
            
            int endX = centerX + (int) (Math.cos(angle) * (innerRadius + WeatherAppConstants.WeatherIcon.RAY_LENGTH));
            int endY = centerY + (int) (Math.sin(angle) * (innerRadius + WeatherAppConstants.WeatherIcon.RAY_LENGTH));
            
            g2d.drawLine(startX, startY, endX, endY);
        }
        
        // Draw center circle
        g2d.drawOval(
            centerX - WeatherAppConstants.WeatherIcon.CENTER_RADIUS, 
            centerY - WeatherAppConstants.WeatherIcon.CENTER_RADIUS, 
            WeatherAppConstants.WeatherIcon.CENTER_RADIUS * 2, 
            WeatherAppConstants.WeatherIcon.CENTER_RADIUS * 2
        );
    }
    
    /**
     * Draws the divider line.
     * @param g2d Graphics2D context
     */
    private void drawDividerLine(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 77)); // 0.3 alpha
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(
            WeatherAppConstants.UI.CARD_PADDING, 
            WeatherAppConstants.Layout.DIVIDER_Y_POSITION, 
            WeatherAppConstants.UI.CARD_WIDTH - WeatherAppConstants.UI.CARD_PADDING, 
            WeatherAppConstants.Layout.DIVIDER_Y_POSITION
        );
    }
    
    /**
     * Draws the hourly forecast mini-cards.
     * @param g2d Graphics2D context
     */
    private void drawHourlyForecast(Graphics2D g2d) {
        int forecastY = WeatherAppConstants.Layout.HOURLY_FORECAST_Y_POSITION;
        int cardWidth = 50;
        int totalWidth = WeatherAppConstants.UI.CARD_WIDTH - (WeatherAppConstants.UI.CARD_PADDING * 2);
        int spacing = (totalWidth - (cardWidth * 4)) / 3;
        
        for (int i = 0; i < 4 && i < hourlyTimes.length && i < hourlyTemps.length; i++) {
            int cardX = WeatherAppConstants.UI.CARD_PADDING + (i * (cardWidth + spacing));
            drawMiniWeatherCard(g2d, cardX, forecastY, hourlyTimes[i], hourlyTemps[i]);
        }
    }
    
    /**
     * Draws a single mini weather card for hourly forecast.
     * @param g2d Graphics2D context
     * @param x X position
     * @param y Y position
     * @param time time text
     * @param temp temperature text
     */
    private void drawMiniWeatherCard(Graphics2D g2d, int x, int y, String time, String temp) {
        // Time text
        Font timeFont = new Font(WeatherAppConstants.Typography.FONT_FAMILY, Font.PLAIN, 
                                WeatherAppConstants.Typography.HOURLY_TIME_FONT_SIZE);
        g2d.setFont(timeFont);
        g2d.setColor(tertiaryText);
        
        FontMetrics timeFm = g2d.getFontMetrics();
        int timeWidth = timeFm.stringWidth(time);
        int timeX = x + (50 - timeWidth) / 2;
        int timeY = y + timeFm.getAscent();
        g2d.drawString(time, timeX, timeY);
        
        // Mini weather icon
        g2d.setColor(primaryText);
        g2d.setStroke(new BasicStroke(2));
        int iconX = x + (50 - 20) / 2;
        int iconY = y + 20;
        g2d.drawOval(iconX, iconY, 20, 20);
        
        // Temperature text
        Font tempFont = new Font(WeatherAppConstants.Typography.FONT_FAMILY, Font.PLAIN, 
                                WeatherAppConstants.Typography.HOURLY_TEMP_FONT_SIZE);
        g2d.setFont(tempFont);
        g2d.setColor(primaryText);
        
        FontMetrics tempFm = g2d.getFontMetrics();
        int tempWidth = tempFm.stringWidth(temp);
        int tempX = x + (50 - tempWidth) / 2;
        int tempY = y + 50 + tempFm.getAscent();
        g2d.drawString(temp, tempX, tempY);
    }
    
    // === PUBLIC API METHODS ===
    
    /**
     * Updates the weather data displayed on the card.
     * @param date date string
     * @param time time string
     * @param location location string
     * @param temperature temperature string
     * @param day day string
     */
    public void updateWeatherData(String date, String time, String location, 
                                 String temperature, String day) {
        if (date != null) this.dateText = date;
        if (time != null) this.timeText = time;
        if (location != null) this.locationText = location;
        if (temperature != null) this.temperatureText = temperature;
        if (day != null) this.dayText = day;
        
        SwingUtilities.invokeLater(this::repaint);
        WeatherAppLogger.debug("Weather data updated for location: " + location);
    }
    
    /**
     * Updates the hourly forecast data.
     * @param times array of time strings
     * @param temps array of temperature strings
     */
    public void updateHourlyForecast(String[] times, String[] temps) {
        if (times != null && times.length == 4 && temps != null && temps.length == 4) {
            this.hourlyTimes = times.clone();
            this.hourlyTemps = temps.clone();
            SwingUtilities.invokeLater(this::repaint);
            WeatherAppLogger.debug("Hourly forecast updated");
        } else {
            WeatherAppLogger.warn("Invalid hourly forecast data provided");
        }
    }
    
    /**
     * Gets the current location text.
     * @return current location
     */
    public String getCurrentLocation() {
        return locationText;
    }
    
    /**
     * Gets the current temperature text.
     * @return current temperature
     */
    public String getCurrentTemperature() {
        return temperatureText;
    }
}
