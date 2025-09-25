import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Enhanced city search panel with autocomplete functionality.
 * Provides a rounded search field with real-time city suggestions.
 * 
 * Features:
 * - Rounded search field with semi-transparent styling
 * - Real-time autocomplete with configurable minimum search length
 * - Extensive city database with country information
 * - Asynchronous search processing
 * - Error handling and logging
 * - Keyboard and mouse interaction support
 * 
 * @author Weather App Team
 * @version 2.0
 */
public class ImprovedCitySearchPanel extends JPanel {
    
    // Components
    private SimpleRoundedTextField searchField;
    private JPopupMenu suggestionPopup;
    private final List<String> cityDatabase;
    private CitySearchListener listener;
    
    // Search state
    private volatile boolean isSearching = false;
    private String lastSearchTerm = "";
    
    /**
     * Interface for handling city selection events.
     */
    public interface CitySearchListener {
        /**
         * Called when a city is selected.
         * @param cityName the selected city name
         * @param countryName the selected country name
         */
        void onCitySelected(String cityName, String countryName);
    }
    
    /**
     * Creates a new city search panel with default configuration.
     */
    public ImprovedCitySearchPanel() {
        this.cityDatabase = initializeCityDatabase();
        setupComponents();
        setupLayout();
        setupEventListeners();
        
        WeatherAppLogger.info("City search panel initialized with " + cityDatabase.size() + " cities");
    }
    
    /**
     * Initializes the comprehensive city database.
     * @return List of city, country combinations
     */
    private List<String> initializeCityDatabase() {
        return new ArrayList<>(Arrays.asList(
            // Major European cities
            "London, United Kingdom", "Paris, France", "Berlin, Germany", "Madrid, Spain",
            "Rome, Italy", "Amsterdam, Netherlands", "Vienna, Austria", "Prague, Czech Republic",
            "Moscow, Russia", "Stockholm, Sweden", "Oslo, Norway", "Copenhagen, Denmark",
            "Helsinki, Finland", "Zurich, Switzerland", "Brussels, Belgium", "Lisbon, Portugal",
            "Dublin, Ireland", "Edinburgh, Scotland", "Manchester, United Kingdom",
            "Birmingham, United Kingdom", "Liverpool, United Kingdom", "Barcelona, Spain",
            "Milan, Italy", "Munich, Germany", "Hamburg, Germany", "Warsaw, Poland",
            "Budapest, Hungary", "Athens, Greece", "Istanbul, Turkey",
            
            // North American cities
            "New York, United States", "Los Angeles, United States", "Chicago, United States",
            "Miami, United States", "San Francisco, United States", "Boston, United States",
            "Washington, United States", "Seattle, United States", "Denver, United States",
            "Las Vegas, United States", "Vancouver, Canada", "Toronto, Canada", "Montreal, Canada",
            "Calgary, Canada", "Ottawa, Canada", "Mexico City, Mexico", "Guadalajara, Mexico",
            
            // Asian cities
            "Tokyo, Japan", "Beijing, China", "Shanghai, China", "Mumbai, India", "Delhi, India",
            "Bangkok, Thailand", "Singapore, Singapore", "Seoul, South Korea", "Hong Kong, China",
            "Taipei, Taiwan", "Manila, Philippines", "Jakarta, Indonesia", "Kuala Lumpur, Malaysia",
            "Ho Chi Minh City, Vietnam", "Hanoi, Vietnam", "Osaka, Japan", "Kyoto, Japan",
            
            // Middle Eastern cities
            "Dubai, United Arab Emirates", "Abu Dhabi, United Arab Emirates", "Doha, Qatar",
            "Kuwait City, Kuwait", "Riyadh, Saudi Arabia", "Tel Aviv, Israel", "Jerusalem, Israel",
            "Beirut, Lebanon", "Damascus, Syria", "Baghdad, Iraq", "Tehran, Iran",
            
            // African cities
            "Cairo, Egypt", "Lagos, Nigeria", "Cape Town, South Africa", "Johannesburg, South Africa",
            "Nairobi, Kenya", "Casablanca, Morocco", "Tunis, Tunisia", "Algiers, Algeria",
            "Addis Ababa, Ethiopia", "Accra, Ghana", "Dakar, Senegal",
            
            // Oceanian cities
            "Sydney, Australia", "Melbourne, Australia", "Perth, Australia", "Brisbane, Australia",
            "Adelaide, Australia", "Auckland, New Zealand", "Wellington, New Zealand",
            "Christchurch, New Zealand",
            
            // South American cities
            "São Paulo, Brazil", "Rio de Janeiro, Brazil", "Buenos Aires, Argentina",
            "Lima, Peru", "Bogotá, Colombia", "Santiago, Chile", "Caracas, Venezuela",
            "Montevideo, Uruguay", "La Paz, Bolivia", "Quito, Ecuador"
        ));
    }
    
    /**
     * Sets up the UI components.
     */
    private void setupComponents() {
        setOpaque(false);
        
        // Create search field
        searchField = new SimpleRoundedTextField(20);
        searchField.setPreferredSize(new Dimension(
            WeatherAppConstants.UI.SEARCH_FIELD_WIDTH, 
            WeatherAppConstants.UI.SEARCH_FIELD_HEIGHT
        ));
        
        // Create suggestion popup
        suggestionPopup = new JPopupMenu();
        suggestionPopup.setBorder(BorderFactory.createLineBorder(
            ColorUtils.fromRGBA(WeatherAppConstants.Colors.SEARCH_BORDER), 1));
        suggestionPopup.setBackground(new Color(50, 50, 50, 200));
    }
    
    /**
     * Sets up the panel layout.
     */
    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        add(searchField);
    }
    
    /**
     * Sets up event listeners for user interaction.
     */
    private void setupEventListeners() {
        // Document listener for real-time search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scheduleSearch();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                scheduleSearch();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                scheduleSearch();
            }
        });
        
        // Enter key handler
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEnterKeyPress();
            }
        });
    }
    
    /**
     * Schedules a search operation with debouncing.
     */
    private void scheduleSearch() {
        String currentTerm = searchField.getText().trim();
        
        // Debounce: only search if term has changed
        if (currentTerm.equals(lastSearchTerm)) {
            return;
        }
        
        lastSearchTerm = currentTerm;
        
        // Perform search asynchronously to avoid blocking UI
        CompletableFuture.supplyAsync(() -> performSearch(currentTerm))
                        .thenAccept(this::displaySuggestions)
                        .exceptionally(throwable -> {
                            handleSearchError(throwable);
                            return null;
                        });
    }
    
    /**
     * Performs the actual search operation.
     * @param searchTerm the term to search for
     * @return list of matching cities
     */
    private List<String> performSearch(String searchTerm) {
        if (searchTerm.length() < WeatherAppConstants.App.MIN_SEARCH_LENGTH) {
            return new ArrayList<>();
        }
        
        isSearching = true;
        List<String> matches = new ArrayList<>();
        String lowerSearchTerm = searchTerm.toLowerCase();
        
        try {
            for (String city : cityDatabase) {
                if (city.toLowerCase().contains(lowerSearchTerm)) {
                    matches.add(city);
                    if (matches.size() >= WeatherAppConstants.App.MAX_SUGGESTIONS) {
                        break;
                    }
                }
            }
            
            WeatherAppLogger.debug("Search for '" + searchTerm + "' found " + matches.size() + " matches");
            
        } catch (Exception e) {
            WeatherAppLogger.error("Error during city search", e);
        } finally {
            isSearching = false;
        }
        
        return matches;
    }
    
    /**
     * Displays search suggestions in the popup menu.
     * @param matches list of matching cities
     */
    private void displaySuggestions(List<String> matches) {
        SwingUtilities.invokeLater(() -> {
            try {
                suggestionPopup.removeAll();
                
                if (matches.isEmpty()) {
                    suggestionPopup.setVisible(false);
                    return;
                }
                
                // Create menu items for matches
                for (String match : matches) {
                    JMenuItem item = createSuggestionMenuItem(match);
                    suggestionPopup.add(item);
                }
                
                // Show popup
                if (!suggestionPopup.isVisible()) {
                    suggestionPopup.show(searchField, 0, searchField.getHeight());
                } else {
                    suggestionPopup.revalidate();
                    suggestionPopup.repaint();
                }
                
            } catch (Exception e) {
                WeatherAppLogger.error("Error displaying suggestions", e);
            }
        });
    }
    
    /**
     * Creates a styled menu item for a city suggestion.
     * @param cityCountry the city and country string
     * @return configured JMenuItem
     */
    private JMenuItem createSuggestionMenuItem(String cityCountry) {
        JMenuItem item = new JMenuItem(cityCountry);
        item.setBackground(new Color(60, 60, 60, 180));
        item.setForeground(Color.WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        item.setFont(new Font(WeatherAppConstants.Typography.FONT_FAMILY, Font.PLAIN, 13));
        
        // Hover effects
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(100, 100, 100, 180));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(new Color(60, 60, 60, 180));
            }
        });
        
        // Selection handler
        item.addActionListener(e -> selectCity(cityCountry));
        
        return item;
    }
    
    /**
     * Handles Enter key press in the search field.
     */
    private void handleEnterKeyPress() {
        String text = searchField.getText().trim();
        if (!text.isEmpty()) {
            // Try to find exact match first
            String exactMatch = findExactMatch(text);
            if (exactMatch != null) {
                selectCity(exactMatch);
            } else {
                // Use the entered text as-is
                selectCity(text);
            }
        }
    }
    
    /**
     * Finds an exact match for the search term.
     * @param searchTerm the search term
     * @return exact match or null if not found
     */
    private String findExactMatch(String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        for (String city : cityDatabase) {
            if (city.toLowerCase().startsWith(lowerSearchTerm)) {
                return city;
            }
        }
        return null;
    }
    
    /**
     * Handles city selection.
     * @param cityCountry the selected city and country
     */
    private void selectCity(String cityCountry) {
        try {
            String[] parts = cityCountry.split(", ");
            String cityName = parts[0];
            String countryName = parts.length > 1 ? parts[1] : "";
            
            searchField.setText(cityCountry);
            suggestionPopup.setVisible(false);
            
            // Return focus to search field
            SwingUtilities.invokeLater(() -> {
                searchField.requestFocusInWindow();
                searchField.setCaretPosition(searchField.getText().length());
            });
            
            // Notify listener
            if (listener != null) {
                listener.onCitySelected(cityName, countryName);
                WeatherAppLogger.info("City selected: " + cityName + ", " + countryName);
            }
            
        } catch (Exception e) {
            WeatherAppLogger.error("Error selecting city: " + cityCountry, e);
        }
    }
    
    /**
     * Handles search errors.
     * @param throwable the error that occurred
     */
    private void handleSearchError(Throwable throwable) {
        WeatherAppLogger.error("Search operation failed", throwable);
        SwingUtilities.invokeLater(() -> suggestionPopup.setVisible(false));
    }
    
    // === PUBLIC API ===
    
    /**
     * Sets the city search listener.
     * @param listener the listener to set
     */
    public void setCitySearchListener(CitySearchListener listener) {
        this.listener = listener;
    }
    
    /**
     * Gets the current search text.
     * @return current search text
     */
    public String getSearchText() {
        return searchField.getText();
    }
    
    /**
     * Clears the search field and hides suggestions.
     */
    public void clearSearch() {
        SwingUtilities.invokeLater(() -> {
            searchField.setText("");
            suggestionPopup.setVisible(false);
            searchField.requestFocusInWindow();
        });
    }
    
    /**
     * Sets focus to the search field.
     */
    public void requestSearchFocus() {
        SwingUtilities.invokeLater(() -> searchField.requestFocusInWindow());
    }
    
    /**
     * Checks if a search operation is currently in progress.
     * @return true if searching, false otherwise
     */
    public boolean isSearching() {
        return isSearching;
    }
    
    /**
     * Gets the number of cities in the database.
     * @return city count
     */
    public int getCityCount() {
        return cityDatabase.size();
    }
}
