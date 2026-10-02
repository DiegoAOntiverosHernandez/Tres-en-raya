import javax.swing.*;
import java.awt.Color;

/**
 * MODO DE JUEGO: CONTRA LA MÁQUINA (TURNOS)
 * 
 * Partida clásica por turnos contra la máquina:
 * 1. El jugador humano coloca X.
 * 2. Se bloquea la pantalla 380ms para simular análisis de la máquina.
 * 3. La máquina calcula su mejor casilla y coloca O.
 * 
 * La lógica de IA (calcularMovimientoIA / hayGanador) se hereda de TableroBase.
 */
public class JuegoContraIA extends TableroBase {

    private boolean turnoBloqueado = false;
    private Timer timerPensamientoIA;

    public JuegoContraIA(JFrame menuPadre) {
        super("Tres en Raya - Contra la IA", menuPadre);
        nuevaPartida();
    }

    @Override
    protected void casillaClickeada(int fila, int col) {
        if (juegoTerminado || turnoBloqueado) return;
        if (tablero[fila][col] != VACIO) return;

        // 1. Movimiento del jugador (X)
        aplicarMovimiento(fila, col, JUGADOR_X);
        if (verificarFinDeJuego()) return;

        // 2. Bloquear clics mientras la máquina calcula
        turnoBloqueado = true;
        labelStatus.setText(TemaVisual.NOMBRE_OPONENTE + " está pensando...");
        labelStatus.setForeground(TemaVisual.ROSA);

        if (timerPensamientoIA != null) timerPensamientoIA.stop();
        timerPensamientoIA = new Timer(380, ev -> {
            if (juegoTerminado) return;

            int[] movimientoIA = calcularMovimientoIA();
            if (movimientoIA != null) {
                aplicarMovimiento(movimientoIA[0], movimientoIA[1], JUGADOR_O);
            }

            turnoBloqueado = false;
            if (!verificarFinDeJuego()) {
                labelStatus.setText("Tu turno: " + TemaVisual.NOMBRE_JUGADOR + " (X)");
                labelStatus.setForeground(TemaVisual.CYAN);
            }
        });
        timerPensamientoIA.setRepeats(false);
        timerPensamientoIA.start();
    }

    @Override
    protected void onNuevaPartida() {
        turnoBloqueado = false;
        labelStatus.setText("Tu turno: " + TemaVisual.NOMBRE_JUGADOR + " (X)");
        labelStatus.setForeground(TemaVisual.CYAN);
    }

    @Override
    protected void onLimpiar() {
        if (timerPensamientoIA != null) {
            timerPensamientoIA.stop();
        }
        turnoBloqueado = false;
    }

    @Override
    protected String getNombreModo() {
        return "MODO: CONTRA LA IA";
    }

    @Override
    protected Color getColorModo() {
        return TemaVisual.ROSA;
    }

    @Override
    protected String getNombreOponente() {
        return TemaVisual.NOMBRE_OPONENTE + " (IA)";
    }

    @Override
    protected String getTextoBotonReinicio() {
        return "Nueva Partida";
    }
}
