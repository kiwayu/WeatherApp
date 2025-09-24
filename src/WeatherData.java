import java.util.List;
import java.util.ArrayList;

/**
 * Data model for weather information
 */
public class WeatherData {
    private String date;
    private String time;
    private String location;
    private double temperature;
    private String weatherType;
    private List<HourlyForecast> hourlyData;
    
    public WeatherData() {
        this.hourlyData = new ArrayList<>();
    }
    
    public WeatherData(String date, String time, String location, double temperature, String weatherType) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.temperature = temperature;
        this.weatherType = weatherType;
        this.hourlyData = new ArrayList<>();
    }
    
    // Getters and setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public String getWeatherType() { return weatherType; }
    public void setWeatherType(String weatherType) { this.weatherType = weatherType; }
    
    public List<HourlyForecast> getHourlyData() { return hourlyData; }
    public void setHourlyData(List<HourlyForecast> hourlyData) { this.hourlyData = hourlyData; }
    
    public void addHourlyForecast(HourlyForecast forecast) {
        this.hourlyData.add(forecast);
    }
    
    /**
     * Inner class for hourly forecast data
     */
    public static class HourlyForecast {
        private String time;
        private double temperature;
        private String weatherType;
        
        public HourlyForecast(String time, double temperature, String weatherType) {
            this.time = time;
            this.temperature = temperature;
            this.weatherType = weatherType;
        }
        
        // Getters and setters
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        
        public String getWeatherType() { return weatherType; }
        public void setWeatherType(String weatherType) { this.weatherType = weatherType; }
    }
}
