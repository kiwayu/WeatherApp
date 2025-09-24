# Weather App

A Java-based weather application with real-time weather data and intelligent city search functionality.

## Features

- Auto-complete city search with country information
- Real-time weather data and hourly forecasts
- Modern UI with rounded corners and smooth graphics
- Multiple temperature units (Celsius/Fahrenheit)
- Custom weather icons

## Requirements

- Java 8 or higher
- Internet connection

## Running the Application

1. Navigate to the project directory
2. Compile the application:
   ```bash
   javac -cp "lib/json-simple-1.1.1.jar" src/*.java
   ```
3. Run the application:
   ```bash
   java -cp "lib/json-simple-1.1.1.jar;src" AppLauncher
   ```

## Usage

1. Start typing a city name in the search field
2. Select from the dropdown suggestions that include country information
3. Press Enter or click Search to view weather data
4. View current conditions and hourly forecasts

## Project Structure

```
WeatherApp/
├── lib/json-simple-1.1.1.jar     # JSON parsing library
├── src/
│   ├── AppLauncher.java           # Main entry point
│   ├── WeatherApp.java            # Weather API integration
│   ├── WeatherCardWithAPI.java    # Main weather interface
│   ├── WeatherData.java           # Data models
│   └── WeatherIcon.java           # Custom icon rendering
└── assets/                        # Weather condition images
```

## APIs Used

- Open Meteo Weather API for real-time weather data
- Open Meteo Geocoding API for city search and location resolution

Both APIs are free and require no API key.

## License

Open source project available for modification and distribution.