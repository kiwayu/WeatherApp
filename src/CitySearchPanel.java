import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CitySearchPanel extends JPanel {
    private SimpleRoundedTextField searchField;
    private JPopupMenu suggestionPopup;
    private List<String> cityDatabase;
    private CitySearchListener listener;
    
    // Interface for callback when city is selected
    public interface CitySearchListener {
        void onCitySelected(String cityName, String countryName);
    }
    
    public CitySearchPanel() {
        initializeCityDatabase();
        setupComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeCityDatabase() {
        // Sample city database with country information
        cityDatabase = new ArrayList<>();
        cityDatabase.add("London, United Kingdom");
        cityDatabase.add("Paris, France");
        cityDatabase.add("New York, United States");
        cityDatabase.add("Tokyo, Japan");
        cityDatabase.add("Berlin, Germany");
        cityDatabase.add("Madrid, Spain");
        cityDatabase.add("Rome, Italy");
        cityDatabase.add("Amsterdam, Netherlands");
        cityDatabase.add("Vienna, Austria");
        cityDatabase.add("Prague, Czech Republic");
        cityDatabase.add("Moscow, Russia");
        cityDatabase.add("Sydney, Australia");
        cityDatabase.add("Toronto, Canada");
        cityDatabase.add("Mumbai, India");
        cityDatabase.add("Beijing, China");
        cityDatabase.add("São Paulo, Brazil");
        cityDatabase.add("Cairo, Egypt");
        cityDatabase.add("Lagos, Nigeria");
        cityDatabase.add("Dubai, United Arab Emirates");
        cityDatabase.add("Singapore, Singapore");
        cityDatabase.add("Seoul, South Korea");
        cityDatabase.add("Bangkok, Thailand");
        cityDatabase.add("Istanbul, Turkey");
        cityDatabase.add("Stockholm, Sweden");
        cityDatabase.add("Oslo, Norway");
        cityDatabase.add("Copenhagen, Denmark");
        cityDatabase.add("Helsinki, Finland");
        cityDatabase.add("Zurich, Switzerland");
        cityDatabase.add("Brussels, Belgium");
        cityDatabase.add("Lisbon, Portugal");
        cityDatabase.add("Dublin, Ireland");
        cityDatabase.add("Edinburgh, Scotland");
        cityDatabase.add("Manchester, United Kingdom");
        cityDatabase.add("Birmingham, United Kingdom");
        cityDatabase.add("Liverpool, United Kingdom");
        cityDatabase.add("Los Angeles, United States");
        cityDatabase.add("Chicago, United States");
        cityDatabase.add("Miami, United States");
        cityDatabase.add("San Francisco, United States");
        cityDatabase.add("Boston, United States");
        cityDatabase.add("Washington, United States");
        cityDatabase.add("Vancouver, Canada");
        cityDatabase.add("Montreal, Canada");
        cityDatabase.add("Melbourne, Australia");
        cityDatabase.add("Perth, Australia");
        cityDatabase.add("Brisbane, Australia");
        cityDatabase.add("Auckland, New Zealand");
        cityDatabase.add("Wellington, New Zealand");
        cityDatabase.add("Cape Town, South Africa");
        cityDatabase.add("Johannesburg, South Africa");
        cityDatabase.add("Nairobi, Kenya");
    }
    
    private void setupComponents() {
        setOpaque(false);
        
        // Create rounded search field
        searchField = new SimpleRoundedTextField(20);
        searchField.setPreferredSize(new Dimension(200, 36));
        
        // Create suggestion popup
        suggestionPopup = new JPopupMenu();
        suggestionPopup.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1));
        suggestionPopup.setBackground(new Color(50, 50, 50, 200));
    }
    
    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        add(searchField);
    }
    
    private void setupEventListeners() {
        // Add document listener for real-time search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
        });
        
        // Handle Enter key
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = searchField.getText().trim();
                if (!text.isEmpty()) {
                    selectCity(text);
                }
            }
        });
    }
    
    private void updateSuggestions() {
        SwingUtilities.invokeLater(() -> {
            String searchText = searchField.getText().toLowerCase().trim();
            
            if (searchText.length() < 2) {
                suggestionPopup.setVisible(false);
                return;
            }
            
            // Clear existing suggestions
            suggestionPopup.removeAll();
            
            // Find matching cities
            List<String> matches = new ArrayList<>();
            for (String city : cityDatabase) {
                if (city.toLowerCase().contains(searchText)) {
                    matches.add(city);
                    if (matches.size() >= 8) break; // Limit to 8 suggestions
                }
            }
            
            if (matches.isEmpty()) {
                suggestionPopup.setVisible(false);
                return;
            }
            
            // Create suggestion menu items
            for (String match : matches) {
                JMenuItem item = createSuggestionItem(match);
                suggestionPopup.add(item);
            }
            
            // Show popup
            if (!suggestionPopup.isVisible()) {
                suggestionPopup.show(searchField, 0, searchField.getHeight());
            } else {
                suggestionPopup.revalidate();
                suggestionPopup.repaint();
            }
        });
    }
    
    private JMenuItem createSuggestionItem(String cityCountry) {
        JMenuItem item = new JMenuItem(cityCountry);
        item.setBackground(new Color(60, 60, 60, 180));
        item.setForeground(Color.WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        item.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        // Hover effect
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
        
        item.addActionListener(e -> {
            selectCity(cityCountry);
            suggestionPopup.setVisible(false);
        });
        
        return item;
    }
    
    private void selectCity(String cityCountry) {
        String[] parts = cityCountry.split(", ");
        String cityName = parts[0];
        String countryName = parts.length > 1 ? parts[1] : "";
        
        // Set text and hide popup
        searchField.setText(cityCountry);
        suggestionPopup.setVisible(false);
        
        // Request focus back to the search field
        SwingUtilities.invokeLater(() -> {
            searchField.requestFocusInWindow();
            searchField.setCaretPosition(searchField.getText().length());
        });
        
        if (listener != null) {
            listener.onCitySelected(cityName, countryName);
        }
    }
    
    public void setCitySearchListener(CitySearchListener listener) {
        this.listener = listener;
    }
    
    public void setPlaceholderText(String placeholder) {
        // This would require a more complex implementation for true placeholder text
        // For now, we'll set it as the initial text
        if (searchField.getText().isEmpty()) {
            searchField.setText(placeholder);
            searchField.setForeground(new Color(255, 255, 255, 150));
        }
    }
    
    public String getSearchText() {
        return searchField.getText();
    }
    
    public void clearSearch() {
        SwingUtilities.invokeLater(() -> {
            searchField.setText("");
            suggestionPopup.setVisible(false);
            searchField.requestFocusInWindow();
        });
    }
    
    public void resetSearchField() {
        SwingUtilities.invokeLater(() -> {
            searchField.setText("");
            suggestionPopup.setVisible(false);
            searchField.setCaretPosition(0);
            searchField.requestFocusInWindow();
        });
    }
}
