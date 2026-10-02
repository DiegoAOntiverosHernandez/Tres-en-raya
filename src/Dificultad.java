import java.awt.Color;

/**
 * Niveles de dificultad para el Modo Smash Frenesí y su Minijuego de Choque:
 * - FÁCIL: Máquina relajada, velocidad moderada.
 * - NORMAL: Máquina equilibrada, requiere pulsaciones constantes.
 * - DIFÍCIL: Máquina agresiva y altamente competitiva con ráfagas frenéticas.
 */
public enum Dificultad {
    FACIL("FÁCIL", new Color(34, 197, 94), new Color(20, 83, 45), new Color(134, 239, 172), 160, 0.72, 1800, 1100),
    NORMAL("NORMAL", new Color(234, 179, 8), new Color(120, 53, 15), new Color(253, 224, 71), 115, 0.86, 1400, 900),
    DIFICIL("DIFÍCIL", new Color(239, 68, 68), new Color(127, 29, 29), new Color(254, 202, 202), 125, 0.93, 200, 250);

    // Dificultad global compartida entre la partida y los minijuegos
    private static Dificultad dificultadActual = NORMAL;

    public static Dificultad getActual() { return dificultadActual; }
    public static void setActual(Dificultad d) { dificultadActual = d; }

    private final String etiqueta;
    private final Color colorBorde;
    private final Color colorFondoActivo;
    private final Color colorTextoActivo;

    // Parámetros para el Minijuego de machacar espacio
    private final int delayMinijuegoMs;
    private final double probExitoMinijuego;

    // Parámetros para el tablero en tiempo real
    private final int delayIaFrenesiMs;
    private final int delayIaCargaMs;

    Dificultad(String etiqueta, Color colorBorde, Color colorFondoActivo, Color colorTextoActivo,
            int delayMinijuegoMs, double probExitoMinijuego,
            int delayIaFrenesiMs, int delayIaCargaMs) {
        this.etiqueta = etiqueta;
        this.colorBorde = colorBorde;
        this.colorFondoActivo = colorFondoActivo;
        this.colorTextoActivo = colorTextoActivo;
        this.delayMinijuegoMs = delayMinijuegoMs;
        this.probExitoMinijuego = probExitoMinijuego;
        this.delayIaFrenesiMs = delayIaFrenesiMs;
        this.delayIaCargaMs = delayIaCargaMs;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public Color getColorBorde() {
        return colorBorde;
    }

    public Color getColorFondoActivo() {
        return colorFondoActivo;
    }

    public Color getColorTextoActivo() {
        return colorTextoActivo;
    }

    public int getDelayMinijuegoMs() {
        return delayMinijuegoMs;
    }

    public double getProbExitoMinijuego() {
        return probExitoMinijuego;
    }

    public int getDelayIaFrenesiMs() {
        return delayIaFrenesiMs;
    }

    public int getDelayIaCargaMs() {
        return delayIaCargaMs;
    }
}
