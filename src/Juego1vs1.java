import javax.swing.*;
import java.awt.Color;

/**
 * MODO DE JUEGO: 1 VS 1 (LOCAL)
 * 
 * Partida clásica para dos jugadores en el mismo teclado y ratón.
 * Los turnos alternan ordenadamente entre Jugador X y Jugador O.
 */
public class Juego1vs1 extends TableroBase {

    private char turnoActual = JUGADOR_X;

    public Juego1vs1(JFrame menuPadre) {
        super("Tres en Raya - 1 vs 1 Local", menuPadre);
        nuevaPartida();
    }

    @Override
    protected void casillaClickeada(int fila, int col) {
        if (juegoTerminado) return;
        if (tablero[fila][col] != VACIO) return;

        // Se aplica el movimiento del jugador en turno
        aplicarMovimiento(fila, col, turnoActual);

        // Si alguien ganó o empató, fin de turno
        if (verificarFinDeJuego()) return;

        // Cambiar turno entre X y O
        turnoActual = (turnoActual == JUGADOR_X) ? JUGADOR_O : JUGADOR_X;
        actualizarEstadoVisual();
    }

    @Override
    protected void onNuevaPartida() {
        turnoActual = JUGADOR_X;
        actualizarEstadoVisual();
    }

    @Override
    protected void onLimpiar() {
        // No hay temporizadores activos en el modo local
    }

    private void actualizarEstadoVisual() {
        if (juegoTerminado) return;
        labelStatus.setText("Turno: " + (turnoActual == JUGADOR_X
            ? TemaVisual.NOMBRE_JUGADOR + " (X)"
            : TemaVisual.NOMBRE_OPONENTE + " (O)"));
        labelStatus.setForeground(turnoActual == JUGADOR_X ? TemaVisual.CYAN : TemaVisual.ROSA);
    }

    @Override
    protected String getNombreModo() {
        return "MODO: 1 VS 1 LOCAL";
    }

    @Override
    protected Color getColorModo() {
        return TemaVisual.CYAN;
    }

    @Override
    protected String getNombreOponente() {
        return TemaVisual.NOMBRE_OPONENTE + " (O)";
    }

    @Override
    protected String getTextoBotonReinicio() {
        return "Nueva Partida";
    }
}
