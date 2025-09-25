import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service class for handling weather data operations.
 * Provides methods to fetch and generate weather information.
 * 
 * In a real application, this would integrate with actual weather APIs.
 * For demonstration purposes, it generates realistic mock data.
 * 
 * @author Weather App Team
 * @version 2.0
 */
public class WeatherService {
    
    /**
     * Generates weather data for a given location.
     * This is a mock implementation that would be replaced with real API calls.
     * 
     * @param cityName the city name
     * @param countryName the country name
     * @return CompletableFuture containing weather data
     */
    public static CompletableFuture<EnhancedWeatherData> getWeatherForLocation(String cityName, String countryName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate API call delay
                Thread.sleep(200);
                
                // Generate realistic weather data based on location
                EnhancedWeatherData weatherData = generateMockWeatherData(cityName, countryName);
                
                WeatherAppLogger.info("Generated weather data for " + cityName + ", " + countryName);
                return weatherData;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                WeatherAppLogger.error("Weather data generation interrupted", e);
                throw new RuntimeException("Failed to generate weather data", e);
            } catch (Exception e) {
                WeatherAppLogger.error("Error generating weather data", e);
                throw new RuntimeException("Failed to generate weather data", e);
            }
        });
    }
    
    /**
     * Generates mock weather data for demonstration purposes.
     * In a real application, this would be replaced with actual API calls.
     * 
     * @param cityName the city name
     * @param countryName the country name
     * @return generated weather data
     */
    private static EnhancedWeatherData generateMockWeatherData(String cityName, String countryName) {
        // Generate temperature based on rough geographic location
        int baseTemperature = getBaseTemperatureForLocation(countryName);
        int temperature = baseTemperature + ThreadLocalRandom.current().nextInt(-5, 6);
        
        // Generate weather condition
        String weatherCondition = generateWeatherCondition(temperature);
        
        // Generate hourly forecast
        String[] hourlyTimes = {"10am", "11am", "12pm", "1pm"};
        String[] hourlyTemps = new String[4];
        for (int i = 0; i < 4; i++) {
            int hourlyTemp = temperature + ThreadLocalRandom.current().nextInt(-3, 4);
            hourlyTemps[i] = hourlyTemp + "°";
        }
        
        return new EnhancedWeatherData.Builder()
            .setDate("Monday, 27th april")
            .setTime("9:43am")
            .setLocation(cityName)
            .setCountry(countryName)
            .setTemperature(temperature)
            .setWeatherCondition(weatherCondition)
            .setDay("Monday")
            .setHourlyForecast(hourlyTimes, hourlyTemps)
            .build();
    }
    
    /**
     * Gets a base temperature for a location based on country.
     * This is a simplified approach for demonstration.
     * 
     * @param countryName the country name
     * @return base temperature in Celsius
     */
    private static int getBaseTemperatureForLocation(String countryName) {
        if (countryName == null) return 15;
        
        String country = countryName.toLowerCase();
        
        // Arctic/Cold regions
        if (country.contains("norway") || country.contains("finland") || 
            country.contains("iceland") || country.contains("russia")) {
            return ThreadLocalRandom.current().nextInt(-5, 5);
        }
        
        // Desert/Hot regions
        if (country.contains("saudi") || country.contains("emirates") || 
            country.contains("qatar") || country.contains("egypt")) {
            return ThreadLocalRandom.current().nextInt(25, 40);
        }
        
        // Tropical regions
        if (country.contains("singapore") || country.contains("thailand") || 
            country.contains("malaysia") || country.contains("indonesia")) {
            return ThreadLocalRandom.current().nextInt(24, 32);
        }
        
        // Temperate regions (default)
        return ThreadLocalRandom.current().nextInt(10, 22);
    }
    
    /**
     * Generates a weather condition based on temperature.
     * 
     * @param temperature the temperature in Celsius
     * @return weather condition string
     */
    private static String generateWeatherCondition(int temperature) {
        if (temperature < 0) {
            return "snow";
        } else if (temperature < 10) {
            return ThreadLocalRandom.current().nextBoolean() ? "cloudy" : "rainy";
        } else if (temperature < 25) {
            String[] conditions = {"sunny", "cloudy", "partly_cloudy"};
            return conditions[ThreadLocalRandom.current().nextInt(conditions.length)];
        } else {
            return "sunny";
        }
    }
    
    /**
     * Validates if weather data is reasonable.
     * 
     * @param weatherData the weather data to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidWeatherData(EnhancedWeatherData weatherData) {
        if (weatherData == null) return false;
        
        try {
            // Check required fields
            if (weatherData.getLocation() == null || weatherData.getLocation().trim().isEmpty()) {
                return false;
            }
            
            // Check temperature range (reasonable for Earth)
            int temp = weatherData.getTemperature();
            if (temp < -50 || temp > 60) {
                WeatherAppLogger.warn("Temperature out of reasonable range: " + temp);
                return false;
            }
            
            // Check hourly forecast
            String[] times = weatherData.getHourlyTimes();
            String[] temps = weatherData.getHourlyTemperatures();
            if (times == null || temps == null || times.length != temps.length) {
                WeatherAppLogger.warn("Invalid hourly forecast data");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            WeatherAppLogger.error("Error validating weather data", e);
            return false;
        }
    }
}
