/**
 * Constants used throughout the Weather Application.
 * Centralizes all configuration values for easy maintenance.
 */
public final class WeatherAppConstants {
    
    // Prevent instantiation
    private WeatherAppConstants() {}
    
    // === UI DIMENSIONS ===
    public static final class UI {
        public static final int CARD_WIDTH = 280;
        public static final int CARD_HEIGHT = 480;
        public static final int SCENE_WIDTH = 320;
        public static final int SCENE_HEIGHT = 600;
        public static final int CORNER_RADIUS = 20;
        public static final int CARD_PADDING = 20;
        public static final int SEARCH_FIELD_WIDTH = 200;
        public static final int SEARCH_FIELD_HEIGHT = 36;
    }
    
    // === COLORS ===
    public static final class Colors {
        // Card gradient colors
        public static final int[] GRADIENT_START_RGB = {255, 183, 94};  // #FFB75E
        public static final int[] GRADIENT_END_RGB = {237, 143, 3};     // #ED8F03
        
        // Scene background gradient
        public static final int[] SCENE_START_RGB = {131, 96, 195};     // #8360c3
        public static final int[] SCENE_END_RGB = {46, 191, 145};       // #2ebf91
        
        // Text colors (RGB + Alpha)
        public static final int[] PRIMARY_TEXT = {255, 255, 255, 255};   // White
        public static final int[] SECONDARY_TEXT = {255, 255, 255, 204}; // 80% White
        public static final int[] TERTIARY_TEXT = {255, 255, 255, 179};  // 70% White
        public static final int[] QUATERNARY_TEXT = {255, 255, 255, 230}; // 90% White
        
        // Search field colors
        public static final int[] SEARCH_BACKGROUND = {255, 255, 255, 30};
        public static final int[] SEARCH_BORDER = {255, 255, 255, 100};
    }
    
    // === TYPOGRAPHY ===
    public static final class Typography {
        public static final String FONT_FAMILY = "SansSerif";
        public static final int DATE_FONT_SIZE = 14;
        public static final int TIME_FONT_SIZE = 48;
        public static final int LOCATION_FONT_SIZE = 16;
        public static final int TEMPERATURE_FONT_SIZE = 72;
        public static final int DAY_FONT_SIZE = 18;
        public static final int SEARCH_FONT_SIZE = 14;
        public static final int HOURLY_TIME_FONT_SIZE = 12;
        public static final int HOURLY_TEMP_FONT_SIZE = 14;
    }
    
    // === LAYOUT POSITIONS ===
    public static final class Layout {
        public static final int DATE_Y_POSITION = 24;
        public static final int TIME_Y_POSITION = 40;
        public static final int LOCATION_Y_POSITION = 92;
        public static final int WEATHER_ICON_Y_POSITION = 140;
        public static final int TEMPERATURE_Y_POSITION = 240;
        public static final int DAY_Y_POSITION = 320;
        public static final int DIVIDER_Y_POSITION = 360;
        public static final int HOURLY_FORECAST_Y_POSITION = 380;
    }
    
    // === WEATHER ICON ===
    public static final class WeatherIcon {
        public static final int ICON_SIZE = 80;
        public static final int CENTER_RADIUS = 12;
        public static final int RAY_LENGTH = 16;
        public static final int STROKE_WIDTH = 2;
        public static final int RAY_COUNT = 12;
        public static final double RAY_ANGLE_DEGREES = 30.0;
    }
    
    // === APPLICATION ===
    public static final class App {
        public static final String TITLE = "Hyper-Detailed Weather Card";
        public static final int MIN_SEARCH_LENGTH = 2;
        public static final int MAX_SUGGESTIONS = 8;
        public static final int MIN_TEMPERATURE = 5;
        public static final int MAX_TEMPERATURE = 25;
    }
}
