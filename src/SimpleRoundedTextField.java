import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SimpleRoundedTextField extends JTextField {
    private int cornerRadius = 20;
    
    public SimpleRoundedTextField(int columns) {
        super(columns);
        setOpaque(false);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
        setBorder(createRoundedBorder());
        setBackground(new Color(255, 255, 255, 30));
    }
    
    private Border createRoundedBorder() {
        return new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fill(new RoundRectangle2D.Double(x, y, width-1, height-1, cornerRadius, cornerRadius));
                
                // Draw border
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(new RoundRectangle2D.Double(x, y, width-1, height-1, cornerRadius, cornerRadius));
                
                g2d.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(10, 15, 10, 15);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
    }
}
