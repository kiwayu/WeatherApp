/**
 * Enhanced data model for weather information.
 * Provides a comprehensive structure for weather data with validation and builder pattern.
 * 
 * @author Weather App Team
 * @version 2.0
 */
public class EnhancedWeatherData {
    
    // Core weather data
    private final String date;
    private final String time;
    private final String location;
    private final String country;
    private final int temperature;
    private final String weatherCondition;
    private final String day;
    
    // Hourly forecast data
    private final String[] hourlyTimes;
    private final String[] hourlyTemperatures;
    
    // Metadata
    private final long timestamp;
    private final String dataSource;
    
    /**
     * Private constructor - use Builder to create instances.
     */
    private EnhancedWeatherData(Builder builder) {
        this.date = builder.date;
        this.time = builder.time;
        this.location = builder.location;
        this.country = builder.country;
        this.temperature = builder.temperature;
        this.weatherCondition = builder.weatherCondition;
        this.day = builder.day;
        this.hourlyTimes = builder.hourlyTimes != null ? builder.hourlyTimes.clone() : null;
        this.hourlyTemperatures = builder.hourlyTemperatures != null ? builder.hourlyTemperatures.clone() : null;
        this.timestamp = builder.timestamp;
        this.dataSource = builder.dataSource;
    }
    
    // === GETTERS ===
    
    public String getDate() {
        return date;
    }
    
    public String getTime() {
        return time;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getCountry() {
        return country;
    }
    
    public int getTemperature() {
        return temperature;
    }
    
    public String getTemperatureString() {
        return temperature + "°";
    }
    
    public String getWeatherCondition() {
        return weatherCondition;
    }
    
    public String getDay() {
        return day;
    }
    
    public String[] getHourlyTimes() {
        return hourlyTimes != null ? hourlyTimes.clone() : null;
    }
    
    public String[] getHourlyTemperatures() {
        return hourlyTemperatures != null ? hourlyTemperatures.clone() : null;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getDataSource() {
        return dataSource;
    }
    
    /**
     * Gets the full location string (city, country).
     * @return formatted location string
     */
    public String getFullLocation() {
        if (country != null && !country.trim().isEmpty()) {
            return location + ", " + country;
        }
        return location;
    }
    
    /**
     * Checks if the weather data is recent (within last hour).
     * @return true if recent, false otherwise
     */
    public boolean isRecent() {
        long currentTime = System.currentTimeMillis();
        long hourInMillis = 60 * 60 * 1000;
        return (currentTime - timestamp) < hourInMillis;
    }
    
    /**
     * Checks if hourly forecast data is available.
     * @return true if hourly data exists
     */
    public boolean hasHourlyForecast() {
        return hourlyTimes != null && hourlyTemperatures != null && 
               hourlyTimes.length > 0 && hourlyTimes.length == hourlyTemperatures.length;
    }
    
    @Override
    public String toString() {
        return String.format("WeatherData{location='%s', country='%s', temperature=%d°, condition='%s', time='%s'}",
                location, country, temperature, weatherCondition, time);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        EnhancedWeatherData that = (EnhancedWeatherData) obj;
        
        return temperature == that.temperature &&
               timestamp == that.timestamp &&
               (location != null ? location.equals(that.location) : that.location == null) &&
               (country != null ? country.equals(that.country) : that.country == null) &&
               (weatherCondition != null ? weatherCondition.equals(that.weatherCondition) : that.weatherCondition == null);
    }
    
    @Override
    public int hashCode() {
        int result = location != null ? location.hashCode() : 0;
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + temperature;
        result = 31 * result + (weatherCondition != null ? weatherCondition.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
    
    /**
     * Builder class for creating EnhancedWeatherData instances.
     * Provides a fluent API for constructing weather data objects.
     */
    public static class Builder {
        private String date;
        private String time;
        private String location;
        private String country;
        private int temperature;
        private String weatherCondition = "sunny";
        private String day;
        private String[] hourlyTimes;
        private String[] hourlyTemperatures;
        private long timestamp = System.currentTimeMillis();
        private String dataSource = "WeatherService";
        
        public Builder setDate(String date) {
            this.date = date;
            return this;
        }
        
        public Builder setTime(String time) {
            this.time = time;
            return this;
        }
        
        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }
        
        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }
        
        public Builder setTemperature(int temperature) {
            this.temperature = temperature;
            return this;
        }
        
        public Builder setWeatherCondition(String weatherCondition) {
            this.weatherCondition = weatherCondition;
            return this;
        }
        
        public Builder setDay(String day) {
            this.day = day;
            return this;
        }
        
        public Builder setHourlyForecast(String[] times, String[] temperatures) {
            if (times != null && temperatures != null && times.length == temperatures.length) {
                this.hourlyTimes = times.clone();
                this.hourlyTemperatures = temperatures.clone();
            }
            return this;
        }
        
        public Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder setDataSource(String dataSource) {
            this.dataSource = dataSource;
            return this;
        }
        
        /**
         * Builds the EnhancedWeatherData instance.
         * @return new EnhancedWeatherData instance
         * @throws IllegalStateException if required fields are missing
         */
        public EnhancedWeatherData build() {
            validateBuilder();
            return new EnhancedWeatherData(this);
        }
        
        /**
         * Validates that required fields are set.
         * @throws IllegalStateException if validation fails
         */
        private void validateBuilder() {
            if (location == null || location.trim().isEmpty()) {
                throw new IllegalStateException("Location is required");
            }
            if (date == null || date.trim().isEmpty()) {
                throw new IllegalStateException("Date is required");
            }
            if (time == null || time.trim().isEmpty()) {
                throw new IllegalStateException("Time is required");
            }
            if (day == null || day.trim().isEmpty()) {
                throw new IllegalStateException("Day is required");
            }
        }
    }
}
