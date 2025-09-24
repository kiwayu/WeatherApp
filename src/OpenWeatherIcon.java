import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * OpenWeatherMap icon integration for weather cards
 * Downloads and caches weather icons from OpenWeatherMap API
 */
public class OpenWeatherIcon {
    
    // OpenWeatherMap icon base URL
    private static final String ICON_BASE_URL = "https://openweathermap.org/img/wn/";
    private static final String ICON_SIZE_SUFFIX = "@2x.png"; // High resolution icons
    
    // Icon cache to avoid repeated downloads
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();
    
    // Weather condition to OpenWeatherMap icon code mapping
    private static final Map<String, String> CONDITION_TO_ICON = new HashMap<>();
    
    static {
        // Initialize condition mappings
        CONDITION_TO_ICON.put("Clear", "01d");           // Clear sky day
        CONDITION_TO_ICON.put("clear", "01d");
        CONDITION_TO_ICON.put("sunny", "01d");
        
        CONDITION_TO_ICON.put("Cloudy", "03d");          // Scattered clouds
        CONDITION_TO_ICON.put("cloudy", "03d");
        CONDITION_TO_ICON.put("partly cloudy", "02d");   // Few clouds
        
        CONDITION_TO_ICON.put("Rain", "10d");            // Rain
        CONDITION_TO_ICON.put("rain", "10d");
        CONDITION_TO_ICON.put("rainy", "10d");
        CONDITION_TO_ICON.put("light rain", "09d");      // Shower rain
        
        CONDITION_TO_ICON.put("Snow", "13d");            // Snow
        CONDITION_TO_ICON.put("snow", "13d");
        CONDITION_TO_ICON.put("snowy", "13d");
        
        CONDITION_TO_ICON.put("Thunderstorm", "11d");    // Thunderstorm
        CONDITION_TO_ICON.put("thunderstorm", "11d");
        
        CONDITION_TO_ICON.put("Mist", "50d");            // Mist
        CONDITION_TO_ICON.put("mist", "50d");
        CONDITION_TO_ICON.put("fog", "50d");
        CONDITION_TO_ICON.put("haze", "50d");
    }
    
    /**
     * Get weather icon for the given condition
     * @param weatherCondition Weather condition string
     * @param size Desired icon size (will be scaled)
     * @return ImageIcon or null if not found
     */
    public static ImageIcon getWeatherIcon(String weatherCondition, int size) {
        return getWeatherIcon(weatherCondition, size, true);
    }
    
    /**
     * Get weather icon for the given condition
     * @param weatherCondition Weather condition string
     * @param size Desired icon size (will be scaled)
     * @param isDayTime true for day icons, false for night icons
     * @return ImageIcon or null if not found
     */
    public static ImageIcon getWeatherIcon(String weatherCondition, int size, boolean isDayTime) {
        if (weatherCondition == null) {
            weatherCondition = "Clear";
        }
        
        // Get the appropriate icon code
        String iconCode = CONDITION_TO_ICON.get(weatherCondition);
        if (iconCode == null) {
            iconCode = "01d"; // Default to clear sky
        }
        
        // Adjust for day/night
        if (!isDayTime && iconCode.endsWith("d")) {
            iconCode = iconCode.replace("d", "n");
        }
        
        String cacheKey = iconCode + "_" + size;
        
        // Check cache first
        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }
        
        // Download and cache the icon
        ImageIcon icon = downloadIcon(iconCode, size);
        if (icon != null) {
            iconCache.put(cacheKey, icon);
        }
        
        return icon;
    }
    
    /**
     * Download icon from OpenWeatherMap
     */
    private static ImageIcon downloadIcon(String iconCode, int size) {
        try {
            String iconUrl = ICON_BASE_URL + iconCode + ICON_SIZE_SUFFIX;
            URL url = new URL(iconUrl);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5 second timeout
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "WeatherApp/1.0");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedImage image = ImageIO.read(inputStream);
                inputStream.close();
                
                if (image != null) {
                    // Scale the image to desired size
                    Image scaledImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);
                }
            }
            
            connection.disconnect();
            
        } catch (IOException e) {
            System.err.println("Failed to download weather icon: " + iconCode);
            e.printStackTrace();
        }
        
        // Return fallback custom icon if download fails
        return createFallbackIcon(iconCode, size);
    }
    
    /**
     * Create a fallback icon if download fails
     */
    private static ImageIcon createFallbackIcon(String iconCode, int size) {
        BufferedImage fallbackImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = fallbackImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Determine weather type from icon code
        String weatherType = "Clear";
        if (iconCode.startsWith("01")) weatherType = "Clear";
        else if (iconCode.startsWith("02") || iconCode.startsWith("03") || iconCode.startsWith("04")) weatherType = "Cloudy";
        else if (iconCode.startsWith("09") || iconCode.startsWith("10")) weatherType = "Rain";
        else if (iconCode.startsWith("11")) weatherType = "Thunderstorm";
        else if (iconCode.startsWith("13")) weatherType = "Snow";
        else if (iconCode.startsWith("50")) weatherType = "Mist";
        
        // Use custom weather icon as fallback
        WeatherIcon.drawSunIcon(g2d, size/2, size/2, size, Color.WHITE);
        
        g2d.dispose();
        return new ImageIcon(fallbackImage);
    }
    
    /**
     * Get mini weather icon for hourly forecast
     */
    public static ImageIcon getMiniWeatherIcon(String weatherCondition, int size) {
        return getWeatherIcon(weatherCondition, size, true);
    }
    
    /**
     * Preload common weather icons
     */
    public static void preloadIcons() {
        SwingUtilities.invokeLater(() -> {
            // Preload common weather icons in background
            String[] commonConditions = {"Clear", "Cloudy", "Rain", "Snow"};
            for (String condition : commonConditions) {
                getWeatherIcon(condition, 80, true);
                getWeatherIcon(condition, 20, true);
            }
        });
    }
    
    /**
     * Clear the icon cache
     */
    public static void clearCache() {
        iconCache.clear();
    }
    
    /**
     * Get cache size for debugging
     */
    public static int getCacheSize() {
        return iconCache.size();
    }
    
    /**
     * Convert weather condition to OpenWeatherMap icon code
     */
    public static String getIconCode(String weatherCondition) {
        return CONDITION_TO_ICON.getOrDefault(weatherCondition, "01d");
    }
    
    /**
     * Check if icon is available in cache
     */
    public static boolean isIconCached(String weatherCondition, int size) {
        String iconCode = getIconCode(weatherCondition);
        String cacheKey = iconCode + "_" + size;
        return iconCache.containsKey(cacheKey);
    }
}
