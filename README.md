# Weather App

A Java weather application with detailed design and intelligent city search.

## Features

- Detailed weather card with precise visual design
- Smart city search with autocomplete for 100+ cities
- Rounded search interface with smooth user experience
- Professional error handling and logging
- Modern gradient UI with clean typography

## Requirements

- Java 8 or higher
- No additional dependencies needed

## Quick Start

```bash
java -cp src ImprovedWeatherApp
```

## Usage

1. Type a city name in the search bar
2. Select from suggestions or press Enter
3. View weather data with animations

## Available Versions

Run one of these commands based on your needs:

**Recommended:**
```bash
java -cp src ImprovedWeatherApp
```

**Visual specifications version:**
```bash
java -cp src HyperDetailedSwingApp
```

**Original with live APIs:**
```bash
java -cp "lib/json-simple-1.1.1.jar;src" AppLauncher
```

## Project Structure

```
WeatherApp/
├── src/
│   ├── ImprovedWeatherApp.java        # Main application
│   ├── HyperDetailedSwingApp.java     # Visual version
│   └── AppLauncher.java               # API version
└── lib/
    └── json-simple-1.1.1.jar          # For API version only
```