import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

/**
 * Main application class for the Hyper-Detailed Weather Card application.
 * 
 * This application provides a beautiful, pixel-perfect weather interface with:
 * - Exact visual specifications matching design requirements
 * - Real-time city search with autocomplete
 * - Professional weather card display
 * - Proper error handling and logging
 * - Modern Java best practices
 * 
 * @author Weather App Team
 * @version 2.0
 */
public class ImprovedWeatherApp extends JFrame {
    
    // Core components
    private ImprovedWeatherCard weatherCard;
    private ImprovedCitySearchPanel searchPanel;
    
    // Application state
    private EnhancedWeatherData currentWeatherData;
    private volatile boolean isLoadingWeather = false;
    
    /**
     * Creates and initializes the weather application.
     */
    public ImprovedWeatherApp() {
        try {
            WeatherAppLogger.info("Starting Hyper-Detailed Weather Application v2.0");
            initializeApplication();
            createUserInterface();
            loadDefaultWeatherData();
            WeatherAppLogger.info("Application initialized successfully");
            
        } catch (Exception e) {
            WeatherAppLogger.error("Failed to initialize application", e);
            showErrorDialog("Application Initialization Error", 
                          "Failed to start the weather application. Please try again.");
            System.exit(1);
        }
    }
    
    /**
     * Initializes the application settings and logging.
     */
    private void initializeApplication() {
        // Enable debug logging if needed
        WeatherAppLogger.setDebugEnabled(false);
        
        // Set system look and feel if available
        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            WeatherAppLogger.warn("Could not set system look and feel: " + e.getMessage());
        }
    }
    
    /**
     * Creates and configures the user interface.
     */
    private void createUserInterface() {
        configureMainWindow();
        createComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Configures the main application window.
     */
    private void configureMainWindow() {
        setTitle(WeatherAppConstants.App.TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(WeatherAppConstants.UI.SCENE_WIDTH, WeatherAppConstants.UI.SCENE_HEIGHT);
        setLocationRelativeTo(null);
        
        // Create scene background with gradient
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintSceneBackground(g);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);
    }
    
    /**
     * Paints the scene background with gradient.
     * @param g Graphics context
     */
    private void paintSceneBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        Color sceneStart = ColorUtils.fromRGB(WeatherAppConstants.Colors.SCENE_START_RGB);
        Color sceneEnd = ColorUtils.fromRGB(WeatherAppConstants.Colors.SCENE_END_RGB);
        
        GradientPaint sceneGradient = new GradientPaint(
            0, 0, sceneStart,
            getWidth(), getHeight(), sceneEnd
        );
        g2d.setPaint(sceneGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * Creates the main UI components.
     */
    private void createComponents() {
        // Create search panel
        searchPanel = new ImprovedCitySearchPanel();
        
        // Create weather card
        weatherCard = new ImprovedWeatherCard();
        
        WeatherAppLogger.debug("UI components created");
    }
    
    /**
     * Sets up the layout of components.
     */
    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        
        // Search panel at top
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchContainer.setOpaque(false);
        searchContainer.add(searchPanel);
        contentPanel.add(searchContainer, BorderLayout.NORTH);
        
        // Weather card in center
        JPanel cardContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cardContainer.setOpaque(false);
        cardContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        cardContainer.add(weatherCard);
        contentPanel.add(cardContainer, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        WeatherAppLogger.debug("Layout configured");
    }
    
    /**
     * Sets up event handlers for user interactions.
     */
    private void setupEventHandlers() {
        searchPanel.setCitySearchListener(new ImprovedCitySearchPanel.CitySearchListener() {
            @Override
            public void onCitySelected(String cityName, String countryName) {
                handleCitySelection(cityName, countryName);
            }
        });
        
        WeatherAppLogger.debug("Event handlers configured");
    }
    
    /**
     * Handles city selection from the search panel.
     * @param cityName selected city name
     * @param countryName selected country name
     */
    private void handleCitySelection(String cityName, String countryName) {
        if (isLoadingWeather) {
            WeatherAppLogger.debug("Weather loading already in progress, ignoring selection");
            return;
        }
        
        WeatherAppLogger.info("User selected city: " + cityName + ", " + countryName);
        loadWeatherForLocation(cityName, countryName);
    }
    
    /**
     * Loads weather data for the specified location.
     * @param cityName city name
     * @param countryName country name
     */
    private void loadWeatherForLocation(String cityName, String countryName) {
        isLoadingWeather = true;
        
        // Show loading state (optional - could add progress indicator)
        WeatherAppLogger.debug("Loading weather data for " + cityName + ", " + countryName);
        
        CompletableFuture<EnhancedWeatherData> weatherFuture = 
            WeatherService.getWeatherForLocation(cityName, countryName);
        
        weatherFuture
            .thenAccept(this::updateWeatherDisplay)
            .exceptionally(this::handleWeatherLoadError)
            .whenComplete((result, throwable) -> {
                isLoadingWeather = false;
            });
    }
    
    /**
     * Updates the weather display with new data.
     * @param weatherData the new weather data
     */
    private void updateWeatherDisplay(EnhancedWeatherData weatherData) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (WeatherService.isValidWeatherData(weatherData)) {
                    currentWeatherData = weatherData;
                    
                    // Update weather card
                    weatherCard.updateWeatherData(
                        weatherData.getDate(),
                        weatherData.getTime(),
                        weatherData.getLocation(),
                        weatherData.getTemperatureString(),
                        weatherData.getDay()
                    );
                    
                    // Update hourly forecast if available
                    if (weatherData.hasHourlyForecast()) {
                        weatherCard.updateHourlyForecast(
                            weatherData.getHourlyTimes(),
                            weatherData.getHourlyTemperatures()
                        );
                    }
                    
                    WeatherAppLogger.info("Weather display updated successfully");
                    
                } else {
                    WeatherAppLogger.error("Received invalid weather data");
                    showErrorDialog("Data Error", "Received invalid weather data. Please try again.");
                }
                
            } catch (Exception e) {
                WeatherAppLogger.error("Error updating weather display", e);
                showErrorDialog("Display Error", "Failed to update weather display. Please try again.");
            }
        });
    }
    
    /**
     * Handles errors during weather data loading.
     * @param throwable the error that occurred
     * @return null (required by CompletableFuture)
     */
    private Void handleWeatherLoadError(Throwable throwable) {
        WeatherAppLogger.error("Failed to load weather data", throwable);
        
        SwingUtilities.invokeLater(() -> {
            showErrorDialog("Weather Data Error", 
                          "Failed to load weather data. Please check your connection and try again.");
        });
        
        return null;
    }
    
    /**
     * Loads default weather data for initial display.
     */
    private void loadDefaultWeatherData() {
        try {
            EnhancedWeatherData defaultData = new EnhancedWeatherData.Builder()
                .setDate("Monday, 27th april")
                .setTime("9:43am")
                .setLocation("London")
                .setCountry("United Kingdom")
                .setTemperature(17)
                .setWeatherCondition("sunny")
                .setDay("Monday")
                .setHourlyForecast(
                    new String[]{"10am", "11am", "12pm", "1pm"},
                    new String[]{"15°", "16°", "18°", "19°"}
                )
                .build();
            
            updateWeatherDisplay(defaultData);
            WeatherAppLogger.info("Default weather data loaded");
            
        } catch (Exception e) {
            WeatherAppLogger.error("Failed to load default weather data", e);
        }
    }
    
    /**
     * Shows an error dialog to the user.
     * @param title dialog title
     * @param message error message
     */
    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    // === PUBLIC API ===
    
    /**
     * Gets the current weather data.
     * @return current weather data or null if none loaded
     */
    public EnhancedWeatherData getCurrentWeatherData() {
        return currentWeatherData;
    }
    
    /**
     * Checks if weather is currently being loaded.
     * @return true if loading, false otherwise
     */
    public boolean isLoadingWeather() {
        return isLoadingWeather;
    }
    
    /**
     * Requests focus for the search field.
     */
    public void focusSearchField() {
        searchPanel.requestSearchFocus();
    }
    
    /**
     * Application entry point.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Ensure UI updates happen on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                new ImprovedWeatherApp().setVisible(true);
            } catch (Exception e) {
                System.err.println("Failed to start application: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
