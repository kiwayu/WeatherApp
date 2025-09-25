import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextField extends JTextField {
    private int cornerRadius;
    private Color borderColor;
    private Color backgroundColor;
    
    public RoundedTextField(int columns, int cornerRadius) {
        super(columns);
        this.cornerRadius = cornerRadius;
        this.borderColor = new Color(255, 255, 255, 100); // Semi-transparent white
        this.backgroundColor = new Color(255, 255, 255, 20); // Very transparent white
        
        setOpaque(false);
        setBorder(new RoundedBorder());
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded background
        g2d.setColor(backgroundColor);
        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        
        // Draw rounded border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
        
        g2d.dispose();
        
        // Call super AFTER drawing background to ensure text renders properly
        super.paintComponent(g);
    }
    
    private class RoundedBorder extends AbstractBorder {
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
