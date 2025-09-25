public class WeatherDataModel {
    private String date;
    private String time;
    private String location;
    private int temperature;
    private String weatherType;
    private String day;
    private String[][] hourlyForecast; // [time, temperature] pairs
    
    // Constructors
    public WeatherDataModel() {
        // Default constructor
    }
    
    public WeatherDataModel(String date, String time, String location, 
                           int temperature, String weatherType, String day) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.temperature = temperature;
        this.weatherType = weatherType;
        this.day = day;
    }
    
    // Getters and Setters
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public int getTemperature() {
        return temperature;
    }
    
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    
    public String getWeatherType() {
        return weatherType;
    }
    
    public void setWeatherType(String weatherType) {
        this.weatherType = weatherType;
    }
    
    public String getDay() {
        return day;
    }
    
    public void setDay(String day) {
        this.day = day;
    }
    
    public String[][] getHourlyForecast() {
        return hourlyForecast;
    }
    
    public void setHourlyForecast(String[][] hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }
    
    // Helper class for hourly forecast data
    public static class HourlyForecast {
        private String time;
        private String temperature;
        private String weatherType;
        
        public HourlyForecast(String time, String temperature, String weatherType) {
            this.time = time;
            this.temperature = temperature;
            this.weatherType = weatherType;
        }
        
        // Getters and Setters
        public String getTime() {
            return time;
        }
        
        public void setTime(String time) {
            this.time = time;
        }
        
        public String getTemperature() {
            return temperature;
        }
        
        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }
        
        public String getWeatherType() {
            return weatherType;
        }
        
        public void setWeatherType(String weatherType) {
            this.weatherType = weatherType;
        }
    }
    
    @Override
    public String toString() {
        return "WeatherDataModel{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                ", temperature=" + temperature +
                ", weatherType='" + weatherType + '\'' +
                ", day='" + day + '\'' +
                '}';
    }
}
