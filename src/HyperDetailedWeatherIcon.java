import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class HyperDetailedWeatherIcon extends Group {
    
    // EXACT specifications for the sun icon
    private static final double ICON_SIZE = 80;
    private static final double CENTER_RADIUS = 12;
    private static final double RAY_LENGTH = 16;
    private static final double STROKE_WIDTH = 2;
    private static final int RAY_COUNT = 12;
    
    public HyperDetailedWeatherIcon() {
        createSunIcon();
    }
    
    private void createSunIcon() {
        // Center circle: 24px diameter (12px radius), 2px stroke
        Circle center = new Circle(ICON_SIZE / 2, ICON_SIZE / 2, CENTER_RADIUS);
        center.setFill(Color.TRANSPARENT);
        center.setStroke(Color.WHITE);
        center.setStrokeWidth(STROKE_WIDTH);
        
        // Create 12 rays evenly spaced 30° apart
        Group rays = new Group();
        
        for (int i = 0; i < RAY_COUNT; i++) {
            double angle = Math.toRadians(i * 30); // 30° intervals
            
            // Calculate ray positions
            double centerX = ICON_SIZE / 2;
            double centerY = ICON_SIZE / 2;
            
            // Inner point (at edge of center circle)
            double innerRadius = CENTER_RADIUS + 4; // Small gap from circle
            double startX = centerX + Math.cos(angle) * innerRadius;
            double startY = centerY + Math.sin(angle) * innerRadius;
            
            // Outer point
            double endX = centerX + Math.cos(angle) * (innerRadius + RAY_LENGTH);
            double endY = centerY + Math.sin(angle) * (innerRadius + RAY_LENGTH);
            
            // Create ray line
            Line ray = new Line(startX, startY, endX, endY);
            ray.setStroke(Color.WHITE);
            ray.setStrokeWidth(STROKE_WIDTH);
            
            rays.getChildren().add(ray);
        }
        
        // Add center circle and rays to the icon
        getChildren().addAll(rays, center);
        
        // Set precise size
        setPrefSize(ICON_SIZE, ICON_SIZE);
        setMaxSize(ICON_SIZE, ICON_SIZE);
        setMinSize(ICON_SIZE, ICON_SIZE);
    }
    
    // Method to change weather icon type (for future enhancements)
    public void setWeatherType(String weatherType) {
        getChildren().clear();
        
        switch (weatherType.toLowerCase()) {
            case "sunny":
            case "clear":
                createSunIcon();
                break;
            case "cloudy":
                createCloudIcon();
                break;
            case "rainy":
                createRainIcon();
                break;
            default:
                createSunIcon();
                break;
        }
    }
    
    private void createCloudIcon() {
        // Simple cloud representation with circles
        double centerX = ICON_SIZE / 2;
        double centerY = ICON_SIZE / 2;
        
        Circle cloud1 = new Circle(centerX - 15, centerY, 12);
        Circle cloud2 = new Circle(centerX, centerY - 8, 16);
        Circle cloud3 = new Circle(centerX + 15, centerY, 12);
        
        cloud1.setFill(Color.TRANSPARENT);
        cloud1.setStroke(Color.WHITE);
        cloud1.setStrokeWidth(STROKE_WIDTH);
        
        cloud2.setFill(Color.TRANSPARENT);
        cloud2.setStroke(Color.WHITE);
        cloud2.setStrokeWidth(STROKE_WIDTH);
        
        cloud3.setFill(Color.TRANSPARENT);
        cloud3.setStroke(Color.WHITE);
        cloud3.setStrokeWidth(STROKE_WIDTH);
        
        getChildren().addAll(cloud1, cloud2, cloud3);
    }
    
    private void createRainIcon() {
        // Create cloud first
        createCloudIcon();
        
        // Add rain drops
        double centerX = ICON_SIZE / 2;
        double centerY = ICON_SIZE / 2;
        
        for (int i = 0; i < 3; i++) {
            double x = centerX - 10 + (i * 10);
            Line rainDrop = new Line(x, centerY + 15, x, centerY + 25);
            rainDrop.setStroke(Color.WHITE);
            rainDrop.setStrokeWidth(1.5);
            getChildren().add(rainDrop);
        }
    }
}
