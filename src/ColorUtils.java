import java.awt.Color;

/**
 * Utility class for creating colors from RGB values.
 * Provides consistent color creation throughout the application.
 */
public final class ColorUtils {
    
    // Prevent instantiation
    private ColorUtils() {}
    
    /**
     * Creates a Color from RGB values.
     * @param rgb array containing [r, g, b] values (0-255)
     * @return Color object
     * @throws IllegalArgumentException if rgb array is invalid
     */
    public static Color fromRGB(int[] rgb) {
        validateRGBArray(rgb, 3);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }
    
    /**
     * Creates a Color from RGBA values.
     * @param rgba array containing [r, g, b, a] values (0-255)
     * @return Color object
     * @throws IllegalArgumentException if rgba array is invalid
     */
    public static Color fromRGBA(int[] rgba) {
        validateRGBArray(rgba, 4);
        return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
    }
    
    /**
     * Creates a semi-transparent version of a color.
     * @param baseColor the base color
     * @param alpha alpha value (0-255)
     * @return new Color with specified alpha
     */
    public static Color withAlpha(Color baseColor, int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("Alpha must be between 0 and 255");
        }
        return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
    }
    
    /**
     * Validates RGB/RGBA array.
     * @param values the color values array
     * @param expectedLength expected array length
     * @throws IllegalArgumentException if array is invalid
     */
    private static void validateRGBArray(int[] values, int expectedLength) {
        if (values == null) {
            throw new IllegalArgumentException("Color values array cannot be null");
        }
        if (values.length != expectedLength) {
            throw new IllegalArgumentException("Expected " + expectedLength + " values, got " + values.length);
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] < 0 || values[i] > 255) {
                throw new IllegalArgumentException("Color value at index " + i + " must be between 0 and 255");
            }
        }
    }
}
