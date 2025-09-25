import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class HyperDetailedSwingApp extends JFrame {
    private HyperDetailedSwingWeatherCard weatherCard;
    private CitySearchPanel searchPanel;
    
    public HyperDetailedSwingApp() {
        initializeFrame();
        createLayout();
    }
    
    private void initializeFrame() {
        setTitle("Hyper-Detailed Weather Card");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Set frame size to accommodate card + search + margin (300x580)
        setSize(320, 600);
        setLocationRelativeTo(null);
        
        // Create dark purple gradient background for the scene
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Scene background gradient: #8360c3 to #2ebf91
                GradientPaint sceneGradient = new GradientPaint(
                    0, 0, new Color(131, 96, 195),  // #8360c3
                    getWidth(), getHeight(), new Color(46, 191, 145) // #2ebf91
                );
                g2d.setPaint(sceneGradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);
    }
    
    private void createLayout() {
        // Create the search panel
        searchPanel = new CitySearchPanel();
        searchPanel.setCitySearchListener(new CitySearchPanel.CitySearchListener() {
            @Override
            public void onCitySelected(String cityName, String countryName) {
                updateWeatherForCity(cityName, countryName);
            }
        });
        
        // Create the hyper-detailed weather card
        weatherCard = new HyperDetailedSwingWeatherCard();
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        
        // Add search panel at the top
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchContainer.setOpaque(false);
        searchContainer.add(searchPanel);
        contentPanel.add(searchContainer, BorderLayout.NORTH);
        
        // Add weather card in the center
        JPanel cardContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cardContainer.setOpaque(false);
        cardContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        cardContainer.add(weatherCard);
        contentPanel.add(cardContainer, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Set default weather data to match specifications
        weatherCard.updateWeatherData(
            "Monday, 27th april",
            "9:43am", 
            "London",
            "17°",
            "Monday"
        );
        
        weatherCard.updateHourlyForecast(
            new String[]{"10am", "11am", "12pm", "1pm"},
            new String[]{"15°", "16°", "18°", "19°"}
        );
    }
    
    private void updateWeatherForCity(String cityName, String countryName) {
        // Simulate weather data update based on selected city
        // In a real application, this would call a weather API
        
        // Update location text
        weatherCard.updateWeatherData(
            "Monday, 27th april",
            "9:43am",
            cityName,
            getRandomTemperature() + "°",
            "Monday"
        );
        
        // Update hourly forecast with random data
        String[] randomTemps = {
            (getRandomTemperature() - 2) + "°",
            (getRandomTemperature() - 1) + "°", 
            (getRandomTemperature() + 1) + "°",
            (getRandomTemperature() + 2) + "°"
        };
        
        weatherCard.updateHourlyForecast(
            new String[]{"10am", "11am", "12pm", "1pm"},
            randomTemps
        );
        
        // No popup needed - just update silently
    }
    
    private int getRandomTemperature() {
        // Generate random temperature between 5-25°C
        return 5 + (int)(Math.random() * 20);
    }
    
    public static void main(String[] args) {
        // Set system look and feel for better integration
        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            // Use default look and feel if system L&F fails
        }
        
        SwingUtilities.invokeLater(() -> {
            new HyperDetailedSwingApp().setVisible(true);
        });
    }
}
