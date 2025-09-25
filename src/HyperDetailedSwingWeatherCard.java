import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class HyperDetailedSwingWeatherCard extends JPanel {
    
    // EXACT dimensions as specified
    private static final int CARD_WIDTH = 280;
    private static final int CARD_HEIGHT = 480;
    private static final int CORNER_RADIUS = 20;
    private static final int CARD_PADDING = 20;
    
    // Color specifications
    private static final Color GRADIENT_START = new Color(255, 183, 94);  // #FFB75E
    private static final Color GRADIENT_END = new Color(237, 143, 3);     // #ED8F03
    private static final Color PRIMARY_TEXT = Color.WHITE;
    private static final Color SECONDARY_TEXT = new Color(255, 255, 255, 204); // 0.8 alpha
    private static final Color TERTIARY_TEXT = new Color(255, 255, 255, 179);  // 0.7 alpha
    private static final Color QUATERNARY_TEXT = new Color(255, 255, 255, 230); // 0.9 alpha
    
    // Weather data
    private String dateText = "Monday, 27th april";
    private String timeText = "9:43am";
    private String locationText = "London";
    private String temperatureText = "17°";
    private String dayText = "Monday";
    private String[] hourlyTimes = {"10am", "11am", "12pm", "1pm"};
    private String[] hourlyTemps = {"15°", "16°", "18°", "19°"};
    
    public HyperDetailedSwingWeatherCard() {
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setSize(CARD_WIDTH, CARD_HEIGHT);
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable anti-aliasing for smooth rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw drop shadow first
        drawDropShadow(g2d);
        
        // Draw card background with gradient
        drawCardBackground(g2d);
        
        // Draw all text elements with EXACT positioning
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
    
    private void drawDropShadow(Graphics2D g2d) {
        // Create shadow effect: 0px 8px 32px rgba(0,0,0,0.15)
        int shadowSize = 16;
        int shadowOffset = 8;
        
        for (int i = shadowSize; i >= 0; i--) {
            float alpha = (float) (0.15 * (shadowSize - i) / shadowSize);
            g2d.setColor(new Color(0, 0, 0, (int) (alpha * 255)));
            
            RoundRectangle2D shadow = new RoundRectangle2D.Double(
                i - shadowSize, 
                i - shadowSize + shadowOffset, 
                CARD_WIDTH + (shadowSize - i) * 2, 
                CARD_HEIGHT + (shadowSize - i) * 2, 
                CORNER_RADIUS + i, 
                CORNER_RADIUS + i
            );
            g2d.fill(shadow);
        }
    }
    
    private void drawCardBackground(Graphics2D g2d) {
        // Create gradient background with EXACT colors at 135° angle
        GradientPaint gradient = new GradientPaint(
            0, 0, GRADIENT_START,
            CARD_WIDTH, CARD_HEIGHT, GRADIENT_END
        );
        
        g2d.setPaint(gradient);
        
        // Draw rounded rectangle with exact corner radius
        RoundRectangle2D card = new RoundRectangle2D.Double(
            0, 0, CARD_WIDTH, CARD_HEIGHT, CORNER_RADIUS, CORNER_RADIUS
        );
        g2d.fill(card);
    }
    
    private void drawDateText(Graphics2D g2d) {
        // Position: 24px from top, centered horizontally
        // Font: System font, 14px, Regular weight
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        g2d.setFont(font);
        g2d.setColor(SECONDARY_TEXT);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(dateText);
        int x = (CARD_WIDTH - textWidth) / 2;
        int y = 24 + fm.getAscent();
        
        g2d.drawString(dateText, x, y);
    }
    
    private void drawTimeText(Graphics2D g2d) {
        // Position: 40px from top (16px below date)
        // Font: System font, 48px, Ultra Light weight
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 48);
        g2d.setFont(font);
        g2d.setColor(PRIMARY_TEXT);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(timeText);
        int x = (CARD_WIDTH - textWidth) / 2;
        int y = 40 + fm.getAscent();
        
        g2d.drawString(timeText, x, y);
    }
    
    private void drawLocationText(Graphics2D g2d) {
        // Position: 92px from top (4px below time)
        // Font: System font, 16px, Regular weight
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
        g2d.setFont(font);
        g2d.setColor(QUATERNARY_TEXT);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(locationText);
        int x = (CARD_WIDTH - textWidth) / 2;
        int y = 92 + fm.getAscent();
        
        g2d.drawString(locationText, x, y);
    }
    
    private void drawWeatherIcon(Graphics2D g2d) {
        // Position: 140px from top (48px below location)
        // Size: 80px × 80px sun symbol with 12 radiating lines
        int iconX = (CARD_WIDTH - 80) / 2;
        int iconY = 140;
        int iconSize = 80;
        int centerX = iconX + iconSize / 2;
        int centerY = iconY + iconSize / 2;
        int centerRadius = 12;
        int rayLength = 16;
        
        g2d.setColor(PRIMARY_TEXT);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Draw 12 sun rays evenly spaced 30° apart
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30);
            
            // Inner point (at edge of center circle)
            int innerRadius = centerRadius + 4;
            int startX = centerX + (int) (Math.cos(angle) * innerRadius);
            int startY = centerY + (int) (Math.sin(angle) * innerRadius);
            
            // Outer point
            int endX = centerX + (int) (Math.cos(angle) * (innerRadius + rayLength));
            int endY = centerY + (int) (Math.sin(angle) * (innerRadius + rayLength));
            
            g2d.drawLine(startX, startY, endX, endY);
        }
        
        // Draw center circle (24px diameter, 2px stroke)
        g2d.drawOval(centerX - centerRadius, centerY - centerRadius, 
                    centerRadius * 2, centerRadius * 2);
    }
    
    private void drawTemperatureText(Graphics2D g2d) {
        // Position: 240px from top (20px below icon)
        // Font: System font, 72px, Ultra Thin weight
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 72);
        g2d.setFont(font);
        g2d.setColor(PRIMARY_TEXT);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(temperatureText);
        int x = (CARD_WIDTH - textWidth) / 2;
        int y = 240 + fm.getAscent();
        
        g2d.drawString(temperatureText, x, y);
    }
    
    private void drawDayText(Graphics2D g2d) {
        // Position: 320px from top (20px below temperature)
        // Font: System font, 18px, Regular weight
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
        g2d.setFont(font);
        g2d.setColor(QUATERNARY_TEXT);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(dayText);
        int x = (CARD_WIDTH - textWidth) / 2;
        int y = 320 + fm.getAscent();
        
        g2d.drawString(dayText, x, y);
    }
    
    private void drawDividerLine(Graphics2D g2d) {
        // Position: 360px from top
        // Width: 240px (full width minus padding)
        // Height: 1px, Color: rgba(255,255,255,0.3)
        g2d.setColor(new Color(255, 255, 255, 77)); // 0.3 alpha
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(CARD_PADDING, 360, CARD_WIDTH - CARD_PADDING, 360);
    }
    
    private void drawHourlyForecast(Graphics2D g2d) {
        // Position: 380px from top
        // Layout: Horizontal row, evenly spaced (4 mini weather cards)
        int forecastY = 380;
        int cardWidth = 50;
        int totalWidth = CARD_WIDTH - (CARD_PADDING * 2);
        int spacing = (totalWidth - (cardWidth * 4)) / 3;
        
        for (int i = 0; i < 4; i++) {
            int cardX = CARD_PADDING + (i * (cardWidth + spacing));
            drawMiniWeatherCard(g2d, cardX, forecastY, hourlyTimes[i], hourlyTemps[i]);
        }
    }
    
    private void drawMiniWeatherCard(Graphics2D g2d, int x, int y, String time, String temp) {
        // Each mini card: 50px wide
        // Content: Time (12px font), Icon (20px size), Temperature (14px font)
        
        // Time text
        Font timeFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        g2d.setFont(timeFont);
        g2d.setColor(TERTIARY_TEXT);
        
        FontMetrics timeFm = g2d.getFontMetrics();
        int timeWidth = timeFm.stringWidth(time);
        int timeX = x + (50 - timeWidth) / 2;
        int timeY = y + timeFm.getAscent();
        g2d.drawString(time, timeX, timeY);
        
        // Mini weather icon (simplified circle)
        g2d.setColor(PRIMARY_TEXT);
        g2d.setStroke(new BasicStroke(2));
        int iconX = x + (50 - 20) / 2;
        int iconY = y + 20;
        g2d.drawOval(iconX, iconY, 20, 20);
        
        // Temperature text
        Font tempFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        g2d.setFont(tempFont);
        g2d.setColor(PRIMARY_TEXT);
        
        FontMetrics tempFm = g2d.getFontMetrics();
        int tempWidth = tempFm.stringWidth(temp);
        int tempX = x + (50 - tempWidth) / 2;
        int tempY = y + 50 + tempFm.getAscent();
        g2d.drawString(temp, tempX, tempY);
    }
    
    // Method to update weather data
    public void updateWeatherData(String date, String time, String location, 
                                 String temperature, String day) {
        this.dateText = date;
        this.timeText = time;
        this.locationText = location;
        this.temperatureText = temperature;
        this.dayText = day;
        repaint();
    }
    
    public void updateHourlyForecast(String[] times, String[] temps) {
        if (times.length == 4 && temps.length == 4) {
            this.hourlyTimes = times;
            this.hourlyTemps = temps;
            repaint();
        }
    }
}
