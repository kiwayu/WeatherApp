import java.awt.*;
import java.awt.geom.*;

/**
 * Custom weather icon renderer with exact specifications
 */
public class WeatherIcon {
    
    public static void drawSunIcon(Graphics2D g2d, int centerX, int centerY, int size, Color color) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Calculate dimensions based on size (80px)
        int centerCircleRadius = size / 7; // ~12px for 80px size
        int rayLength = size / 5; // ~16px for 80px size
        int rayStartDistance = centerCircleRadius + 4; // 4px gap
        
        // Draw center circle
        int circleSize = centerCircleRadius * 2;
        g2d.drawOval(centerX - centerCircleRadius, centerY - centerCircleRadius, circleSize, circleSize);
        
        // Draw 12 radiating rays (30° apart)
        for (int i = 0; i < 12; i++) {
            double angle = i * 30.0; // 360/12 = 30 degrees
            double radians = Math.toRadians(angle);
            
            // Calculate ray start and end points
            int startX = (int) (centerX + Math.cos(radians) * rayStartDistance);
            int startY = (int) (centerY + Math.sin(radians) * rayStartDistance);
            int endX = (int) (centerX + Math.cos(radians) * (rayStartDistance + rayLength));
            int endY = (int) (centerY + Math.sin(radians) * (rayStartDistance + rayLength));
            
            g2d.drawLine(startX, startY, endX, endY);
        }
    }
    
    public static void drawCloudIcon(Graphics2D g2d, int centerX, int centerY, int size, Color color) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create cloud shape with overlapping circles
        int baseRadius = size / 6;
        
        // Main cloud body (larger circles)
        g2d.drawOval(centerX - size/3, centerY - baseRadius/2, baseRadius * 2, baseRadius * 2);
        g2d.drawOval(centerX - baseRadius/2, centerY - baseRadius, (int)(baseRadius * 2.5), (int)(baseRadius * 2.5));
        g2d.drawOval(centerX + baseRadius/2, centerY - baseRadius/2, baseRadius * 2, baseRadius * 2);
        
        // Additional smaller clouds for detail
        g2d.drawOval(centerX - size/4, centerY + baseRadius/4, baseRadius, baseRadius);
        g2d.drawOval(centerX + size/6, centerY + baseRadius/4, baseRadius, baseRadius);
    }
    
    public static void drawRainIcon(Graphics2D g2d, int centerX, int centerY, int size, Color color) {
        // Draw cloud first
        drawCloudIcon(g2d, centerX, centerY - size/8, (int)(size * 0.7), color);
        
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Draw rain drops
        int dropLength = size / 4;
        int dropSpacing = size / 6;
        
        for (int i = -1; i <= 1; i++) {
            int dropX = centerX + (i * dropSpacing);
            int dropStartY = centerY + size/6;
            int dropEndY = dropStartY + dropLength;
            
            g2d.drawLine(dropX, dropStartY, dropX, dropEndY);
        }
    }
    
    public static void drawSnowIcon(Graphics2D g2d, int centerX, int centerY, int size, Color color) {
        // Draw cloud first
        drawCloudIcon(g2d, centerX, centerY - size/8, (int)(size * 0.7), color);
        
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Draw snowflakes as small crosses
        int flakeSize = size / 12;
        int flakeSpacing = size / 6;
        
        for (int i = -1; i <= 1; i++) {
            for (int j = 0; j <= 1; j++) {
                int flakeX = centerX + (i * flakeSpacing);
                int flakeY = centerY + size/6 + (j * flakeSpacing/2);
                
                // Draw cross-shaped snowflake
                g2d.drawLine(flakeX - flakeSize, flakeY, flakeX + flakeSize, flakeY);
                g2d.drawLine(flakeX, flakeY - flakeSize, flakeX, flakeY + flakeSize);
                
                // Add diagonal lines for more detail
                g2d.drawLine(flakeX - flakeSize/2, flakeY - flakeSize/2, flakeX + flakeSize/2, flakeY + flakeSize/2);
                g2d.drawLine(flakeX - flakeSize/2, flakeY + flakeSize/2, flakeX + flakeSize/2, flakeY - flakeSize/2);
            }
        }
    }
    
    public static void drawMiniIcon(Graphics2D g2d, int centerX, int centerY, int size, String weatherType, Color color) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        switch(weatherType.toLowerCase()) {
            case "clear":
            case "sunny":
                // Mini sun - just circle with 4 rays
                int radius = size / 3;
                g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                
                // 4 main rays (simplified)
                g2d.drawLine(centerX, centerY - size/2, centerX, centerY - radius - 2);
                g2d.drawLine(centerX, centerY + radius + 2, centerX, centerY + size/2);
                g2d.drawLine(centerX - size/2, centerY, centerX - radius - 2, centerY);
                g2d.drawLine(centerX + radius + 2, centerY, centerX + size/2, centerY);
                break;
                
            case "cloudy":
                // Mini cloud
                int cloudRadius = size / 4;
                g2d.drawOval(centerX - cloudRadius, centerY - cloudRadius/2, cloudRadius * 2, cloudRadius * 2);
                g2d.drawOval(centerX + cloudRadius/2, centerY - cloudRadius/2, cloudRadius, cloudRadius);
                break;
                
            case "rain":
                // Mini cloud with drops
                cloudRadius = size / 5;
                g2d.drawOval(centerX - cloudRadius, centerY - cloudRadius, cloudRadius * 2, cloudRadius * 2);
                g2d.drawLine(centerX - cloudRadius/2, centerY + cloudRadius/2, centerX - cloudRadius/2, centerY + size/2);
                g2d.drawLine(centerX + cloudRadius/2, centerY + cloudRadius/2, centerX + cloudRadius/2, centerY + size/2);
                break;
                
            case "snow":
                // Mini snowflake
                g2d.drawLine(centerX, centerY - size/2, centerX, centerY + size/2);
                g2d.drawLine(centerX - size/2, centerY, centerX + size/2, centerY);
                g2d.drawLine(centerX - size/3, centerY - size/3, centerX + size/3, centerY + size/3);
                g2d.drawLine(centerX - size/3, centerY + size/3, centerX + size/3, centerY - size/3);
                break;
                
            default:
                // Default mini sun
                radius = size / 3;
                g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                break;
        }
    }
}
