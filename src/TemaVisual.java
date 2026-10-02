import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Clase de constantes visuales centralizadas del proyecto.
 * Todos los colores del tema Slate oscuro y colores de acento
 * se definen aquí para evitar duplicaciones y hardcoding.
 */
public final class TemaVisual {
    private TemaVisual() {} // No instanciable

    // === Colores del tema base (Slate) ===
    public static final Color BG = new Color(15, 23, 42);           // Slate 900
    public static final Color PANEL = new Color(30, 41, 59);        // Slate 800
    public static final Color BORDER = new Color(51, 65, 85);       // Slate 700
    public static final Color CARD = new Color(24, 32, 47);         // Card BG
    public static final Color CARD_HOVER = new Color(45, 59, 85);
    public static final Color TEXTO = new Color(248, 250, 252);
    public static final Color MUTED = new Color(148, 163, 184);     // Slate 400

    // === Colores de acento ===
    public static final Color CYAN = new Color(56, 189, 248);       // Jugador X
    public static final Color ROSA = new Color(244, 63, 94);        // Jugador O
    public static final Color NARANJA = new Color(234, 88, 12);     // Targeting IA
    public static final Color ESMERALDA = new Color(52, 211, 153);  // Victoria
    public static final Color ROJO = new Color(239, 68, 68);        // Frenesí
    public static final Color ORO = new Color(251, 191, 36);        // Título duelo
    public static final Color WIN_BG = new Color(6, 78, 59);        // Fondo casilla ganadora

    // === Colores de hover / flash específicos ===
    public static final Color EXIT_HOVER_BG = new Color(69, 26, 26);
    public static final Color EXIT_HOVER_FG = new Color(252, 165, 165);
    public static final Color FLASH_BTN = new Color(14, 165, 233);  // Flash botón duelo
    public static final Color IA_TARGET_BG = new Color(124, 45, 18); // Targeting IA fondo

    // === Nombres de personajes ===
    public static final String NOMBRE_JUGADOR = "ARCHMOTHER";
    public static final String NOMBRE_OPONENTE = "HIDDEN KING";

    // === Borders pre-creados (evitar recrearlos en cada render) ===
    public static final Border BORDER_CASILLA = BorderFactory.createLineBorder(BORDER, 2);
    public static final Border BORDER_CASILLA_WIN = BorderFactory.createLineBorder(ESMERALDA, 3);
    public static final Border BORDER_CARD = BorderFactory.createLineBorder(BORDER, 1);
}
