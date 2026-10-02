import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * MODO DE JUEGO: SMASH FRENESÍ (TIEMPO REAL + DUELOS)
 * 
 * 1. La partida corre en tiempo real sin turnos estáticos.
 * 2. La máquina apunta y carga casillas (aviso visual en naranja).
 * 3. Si el jugador elige otra casilla libre, se le otorga de inmediato.
 * 4. Si el jugador disputa la misma casilla que la máquina está cargando,
 *    se inicia el duelo de pulsadas (DueloMinijuego).
 * 5. La dificultad (Fácil, Normal, Difícil) sincroniza tanto el ritmo en el tablero
 *    como la agresividad del combate en el minijuego.
 * 
 * La lógica de IA (calcularMovimientoIA / hayGanador) se hereda de TableroBase.
 */
public class JuegoSmashFrenesi extends TableroBase {

    private Timer timerIaFrenesi;
    private Timer timerIaCarga;
    private int iaTargetFila = -1;
    private int iaTargetCol = -1;
    private boolean enDuelo = false;

    // Botones de dificultad en el tablero
    private JButton btnFacil;
    private JButton btnNormal;
    private JButton btnDificil;

    public JuegoSmashFrenesi(JFrame menuPadre) {
        super("Tres en Raya - Smash Frenesí", menuPadre);
        nuevaPartida();
    }

    @Override
    protected void agregarComponentesExtraNorte() {
        JPanel panelDificultad = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panelDificultad.setOpaque(false);

        JLabel lblDif = new JLabel("DIFICULTAD:");
        lblDif.setFont(ReproductorMedia.obtenerFuente(Font.BOLD, 11f));
        lblDif.setForeground(TemaVisual.MUTED);
        panelDificultad.add(lblDif);

        btnFacil = crearBotonDificultadTablero(Dificultad.FACIL);
        btnNormal = crearBotonDificultadTablero(Dificultad.NORMAL);
        btnDificil = crearBotonDificultadTablero(Dificultad.DIFICIL);

        panelDificultad.add(btnFacil);
        panelDificultad.add(btnNormal);
        panelDificultad.add(btnDificil);

        actualizarBotonesDificultadTablero();
        panelNorte.add(panelDificultad);
        panelNorte.add(Box.createVerticalStrut(8));
    }

    private JButton crearBotonDificultadTablero(Dificultad dificultad) {
        JButton btn = new JButton(dificultad.getEtiqueta());
        btn.setFont(ReproductorMedia.obtenerFuente(Font.BOLD, 11f));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(86, 26));
        btn.addActionListener(e -> seleccionarDificultad(dificultad));
        return btn;
    }

    private void seleccionarDificultad(Dificultad dificultad) {
        Dificultad.setActual(dificultad);
        actualizarBotonesDificultadTablero();
        ajustarTimersSegunDificultad();
    }

    private void actualizarBotonesDificultadTablero() {
        aplicarEstiloBotonDificultad(btnFacil, Dificultad.FACIL);
        aplicarEstiloBotonDificultad(btnNormal, Dificultad.NORMAL);
        aplicarEstiloBotonDificultad(btnDificil, Dificultad.DIFICIL);
        if (labelNombreO != null) {
            labelNombreO.setText(TemaVisual.NOMBRE_OPONENTE + " (" + Dificultad.getActual().getEtiqueta() + ")");
        }
    }

    private void aplicarEstiloBotonDificultad(JButton btn, Dificultad dificultad) {
        if (btn == null) return;
        boolean activo = (Dificultad.getActual() == dificultad);
        if (activo) {
            btn.setBackground(dificultad.getColorFondoActivo());
            btn.setForeground(dificultad.getColorTextoActivo());
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(dificultad.getColorBorde(), 2),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
        } else {
            btn.setBackground(TemaVisual.PANEL);
            btn.setForeground(TemaVisual.MUTED);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaVisual.BORDER, 1),
                BorderFactory.createEmptyBorder(3, 7, 3, 7)
            ));
        }
    }

    private void ajustarTimersSegunDificultad() {
        if (!juegoTerminado && timerIaFrenesi != null) {
            int delay = Dificultad.getActual().getDelayIaFrenesiMs();
            timerIaFrenesi.setDelay(delay);
        }
    }

    @Override
    protected void casillaClickeada(int fila, int col) {
        if (juegoTerminado || enDuelo) return;
        if (tablero[fila][col] != VACIO) return;

        boolean choqueDirecto = (iaTargetFila == fila && iaTargetCol == col);

        if (choqueDirecto) {
            // El jugador disputa EXACTAMENTE la casilla a la que la máquina apuntaba
            if (timerIaCarga != null) timerIaCarga.stop();
            iaTargetFila = -1;
            iaTargetCol = -1;
            actualizarBoton(fila, col);

            ejecutarDuelo(fila, col);
        } else {
            // El jugador elige otra casilla libre: se le asigna de inmediato sin duelo
            aplicarMovimiento(fila, col, JUGADOR_X);
            if (verificarFinDeJuego()) return;

            // Si la máquina no estaba cargando ninguna casilla, activar su siguiente jugada pronto
            if (iaTargetFila == -1 && !juegoTerminado) {
                if (timerIaFrenesi != null) {
                    int delayInmediato = (int) (Dificultad.getActual().getDelayIaFrenesiMs() * 0.35);
                    timerIaFrenesi.setInitialDelay(Math.max(250, delayInmediato));
                    timerIaFrenesi.restart();
                }
            }
        }
    }

    private void ejecutarDuelo(int fila, int col) {
        enDuelo = true;
        labelStatus.setText("¡¡3, 2, 1, GO!!");
        labelStatus.setForeground(TemaVisual.ROJO);
        labelStatus.paintImmediately(labelStatus.getVisibleRect());

        // Se delega el duelo de machaque de espacio a DueloMinijuego
        char ganador = DueloMinijuego.iniciar(this, fila, col, musicaActiva);
        enDuelo = false;

        // Sincronizar estilo en el tablero si el jugador cambió la dificultad dentro del minijuego
        actualizarBotonesDificultadTablero();

        // Reanudar música de fondo (Deadlock) tras el duelo de pulsadas
        if (musicaActiva && !juegoTerminado) {
            ReproductorMedia.iniciarMusicaFondo();
        }

        if (ganador != VACIO && tablero[fila][col] == VACIO) {
            aplicarMovimiento(fila, col, ganador);
            labelStatus.setText(ganador == JUGADOR_X
                ? "¡¡Ganaste la casilla con " + TemaVisual.NOMBRE_JUGADOR + "!!"
                : "¡" + TemaVisual.NOMBRE_OPONENTE + " te ganó la casilla!");
            labelStatus.setForeground(ganador == JUGADOR_X ? TemaVisual.CYAN : TemaVisual.ROSA);
            verificarFinDeJuego();
        }

        if (!juegoTerminado && timerIaFrenesi != null) {
            int delayReanudacion = (int) (Dificultad.getActual().getDelayIaFrenesiMs() * 0.5);
            timerIaFrenesi.setInitialDelay(Math.max(350, delayReanudacion));
            timerIaFrenesi.restart();
        }
    }

    private void iniciarLoopIA() {
        detenerTimersIA();
        int delay = Dificultad.getActual().getDelayIaFrenesiMs();
        int initialDelay = (int) (delay * 0.65);
        timerIaFrenesi = new Timer(delay, e -> {
            if (juegoTerminado || enDuelo) return;
            apuntarCasillaIA();
        });
        timerIaFrenesi.setInitialDelay(initialDelay);
        timerIaFrenesi.start();
    }

    private void apuntarCasillaIA() {
        if (juegoTerminado || enDuelo) return;

        // Si ya hay un objetivo en curso que sigue vacío, respetarlo
        if (iaTargetFila != -1 && iaTargetCol != -1) {
            if (tablero[iaTargetFila][iaTargetCol] == VACIO) return;
        }

        int[] objetivo = calcularMovimientoIA();
        if (objetivo == null) return;

        final int targetFila = objetivo[0];
        final int targetCol = objetivo[1];
        iaTargetFila = targetFila;
        iaTargetCol = targetCol;

        botones[targetFila][targetCol].setBackground(TemaVisual.IA_TARGET_BG);
        botones[targetFila][targetCol].setIcon(null);
        botones[targetFila][targetCol].setText("¡IA!");
        botones[targetFila][targetCol].setForeground(TemaVisual.NARANJA);
        labelStatus.setText("¡" + TemaVisual.NOMBRE_OPONENTE + " APUNTANDO A [" + (targetFila + 1) + "," + (targetCol + 1) + "]!");
        labelStatus.setForeground(TemaVisual.NARANJA);

        if (timerIaCarga != null) timerIaCarga.stop();
        int delayCarga = Dificultad.getActual().getDelayIaCargaMs();
        timerIaCarga = new Timer(delayCarga, ev -> {
            if (juegoTerminado || enDuelo) return;

            if (iaTargetFila == targetFila && iaTargetCol == targetCol) {
                if (tablero[targetFila][targetCol] == VACIO) {
                    aplicarMovimiento(targetFila, targetCol, JUGADOR_O);
                    verificarFinDeJuego();
                } else {
                    actualizarBoton(targetFila, targetCol);
                }
                iaTargetFila = -1;
                iaTargetCol = -1;
            }
        });
        timerIaCarga.setRepeats(false);
        timerIaCarga.start();
    }

    private void detenerTimersIA() {
        if (timerIaFrenesi != null) timerIaFrenesi.stop();
        if (timerIaCarga != null) timerIaCarga.stop();
        if (iaTargetFila != -1 && iaTargetCol != -1) {
            int tempFila = iaTargetFila;
            int tempCol = iaTargetCol;
            iaTargetFila = -1;
            iaTargetCol = -1;
            actualizarBoton(tempFila, tempCol);
        }
    }

    @Override
    protected void onNuevaPartida() {
        enDuelo = false;
        actualizarBotonesDificultadTablero();
        labelStatus.setText("¡TIEMPO REAL! Haz clic en cualquier casilla libre");
        labelStatus.setForeground(TemaVisual.CYAN);
        iniciarLoopIA();
    }

    @Override
    protected void onLimpiar() {
        detenerTimersIA();
        enDuelo = false;
    }

    @Override
    protected String getNombreModo() {
        return "MODO: SMASH FRENESÍ";
    }

    @Override
    protected Color getColorModo() {
        return TemaVisual.ROJO;
    }

    @Override
    protected String getNombreOponente() {
        return TemaVisual.NOMBRE_OPONENTE + " (IA)";
    }

    @Override
    protected String getTextoBotonReinicio() {
        return "¡INICIAR FRENESÍ!";
    }
}
