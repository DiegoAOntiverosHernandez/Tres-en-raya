import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;

/**
 * TABLERO BASE REUTILIZABLE
 * 
 * Contiene la interfaz gráfica común:
 * - Ventana estilizada (Tema oscuro)
 * - Marcadores (Jugador, Empates, Oponente)
 * - Cuadrícula 3x3 de casillas interactivas
 * - Botones de control (Nueva Partida, Música, Reiniciar Marcador, Salir al Menú)
 * - Detección de línea ganadora y empates
 * - Lógica compartida de IA (calcularMovimientoIA / hayGanador)
 * 
 * Las reglas de cada modo específico se delegan a sus subclases:
 * - Juego1vs1.java
 * - JuegoContraIA.java
 * - JuegoSmashFrenesi.java
 */
public abstract class TableroBase extends JFrame {

    public static final char VACIO = ' ';
    public static final char JUGADOR_X = 'X';
    public static final char JUGADOR_O = 'O';

    // Estado del tablero
    protected final char[][] tablero = new char[3][3];
    protected final JButton[][] botones = new JButton[3][3];
    protected boolean juegoTerminado = false;
    protected final JFrame menuPadre;

    // Iconos de personajes (Archmother para X, Hidden King para O)
    protected static final ImageIcon iconoX;
    protected static final ImageIcon iconoO;
    protected static final ImageIcon iconoXMini;
    protected static final ImageIcon iconoOMini;

    static {
        iconoX = ReproductorMedia.cargarIconoPersonaje(JUGADOR_X, 85);
        iconoO = ReproductorMedia.cargarIconoPersonaje(JUGADOR_O, 85);
        iconoXMini = ReproductorMedia.cargarIconoPersonaje(JUGADOR_X, 22);
        iconoOMini = ReproductorMedia.cargarIconoPersonaje(JUGADOR_O, 22);
    }

    // Marcadores
    protected int scoreX = 0;
    protected int scoreO = 0;
    protected int scoreEmpates = 0;

    // Componentes UI
    protected JPanel panelNorte;
    protected JLabel labelStatus;
    protected JLabel labelScoreX;
    protected JLabel labelScoreO;
    protected JLabel labelScoreEmpates;
    protected JLabel labelNombreO;
    protected JButton btnReiniciar;
    protected JButton btnMusica;
    protected boolean musicaActiva = true;

    protected final Random random = new Random();

    public TableroBase(String tituloVentana, JFrame menuPadre) {
        super(tituloVentana);
        this.menuPadre = menuPadre;
        inicializarUI();
    }



    private void inicializarUI() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        try {
            java.io.File iconFile = ReproductorMedia.resolverArchivo("recursos/imagenes/images.jpg");
            if (iconFile.exists()) {
                setIconImage(new ImageIcon(iconFile.getAbsolutePath()).getImage());
            }
        } catch(Exception e) {}
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                volverAlMenu();
            }
        });

        setSize(480, 720);
        setMinimumSize(new Dimension(440, 660));
        setLocationRelativeTo(null);
        getContentPane().setBackground(TemaVisual.BG);
        setLayout(new BorderLayout(0, 10));

        // ==================== PANEL SUPERIOR ====================
        panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setBackground(TemaVisual.BG);
        panelNorte.setBorder(new EmptyBorder(14, 16, 0, 16));

        // Fila 1: Título y botón volver al menú
        JPanel filaTitulo = new JPanel(new BorderLayout(8, 0));
        filaTitulo.setOpaque(false);

        JPanel panelTit = new JPanel();
        panelTit.setLayout(new BoxLayout(panelTit, BoxLayout.Y_AXIS));
        panelTit.setOpaque(false);

        JLabel labelTitulo = new JLabel("TRES EN RAYA");
        labelTitulo.setFont(ReproductorMedia.obtenerFuente(22f));
        labelTitulo.setForeground(TemaVisual.TEXTO);

        JLabel labelModoInfo = new JLabel(getNombreModo());
        labelModoInfo.setFont(ReproductorMedia.obtenerFuente(11f));
        labelModoInfo.setForeground(getColorModo());

        panelTit.add(labelTitulo);
        panelTit.add(Box.createVerticalStrut(2));
        panelTit.add(labelModoInfo);
        filaTitulo.add(panelTit, BorderLayout.WEST);

        JButton btnVolverMenu = crearBoton("← Salir al Menú", TemaVisual.PANEL);
        btnVolverMenu.setPreferredSize(new Dimension(135, 34));
        btnVolverMenu.addActionListener(e -> volverAlMenu());
        filaTitulo.add(btnVolverMenu, BorderLayout.EAST);
        panelNorte.add(filaTitulo);
        panelNorte.add(Box.createVerticalStrut(8));

        agregarComponentesExtraNorte();

        // Fila 2: Marcadores
        JPanel panelMarcador = new JPanel(new GridLayout(1, 3, 10, 0));
        panelMarcador.setOpaque(false);

        labelScoreX = new JLabel("0", SwingConstants.CENTER);
        labelScoreEmpates = new JLabel("0", SwingConstants.CENTER);
        labelScoreO = new JLabel("0", SwingConstants.CENTER);

        panelMarcador.add(crearTarjetaMarcador(TemaVisual.NOMBRE_JUGADOR + " (X)", labelScoreX, TemaVisual.CYAN, iconoXMini, false));
        panelMarcador.add(crearTarjetaMarcador("EMPATES (-)", labelScoreEmpates, TemaVisual.MUTED, null, false));
        JPanel cardO = crearTarjetaMarcador(getNombreOponente(), labelScoreO, TemaVisual.ROSA, iconoOMini, true);
        panelMarcador.add(cardO);
        panelNorte.add(panelMarcador);
        panelNorte.add(Box.createVerticalStrut(10));

        // Fila 3: Status badge
        JPanel panelBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        panelBadge.setBackground(TemaVisual.PANEL);
        panelBadge.setBorder(TemaVisual.BORDER_CARD);
        labelStatus = new JLabel("¡Listo para jugar!");
        labelStatus.setFont(ReproductorMedia.obtenerFuente(15f));
        labelStatus.setForeground(TemaVisual.CYAN);
        panelBadge.add(labelStatus);
        panelNorte.add(panelBadge);

        add(panelNorte, BorderLayout.NORTH);

        // ==================== PANEL CENTRAL: TABLERO 3x3 ====================
        JPanel panelTableroContenedor = new JPanel(new GridBagLayout());
        panelTableroContenedor.setOpaque(false);
        panelTableroContenedor.setBorder(new EmptyBorder(4, 16, 4, 16));

        JPanel grid3x3 = new JPanel(new GridLayout(3, 3, 8, 8));
        grid3x3.setOpaque(false);
        grid3x3.setPreferredSize(new Dimension(350, 350));

        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                final int filaFinal = fila;
                final int colFinal = col;
                JButton btn = new JButton("");
                btn.setFont(ReproductorMedia.obtenerFuente(42f));
                btn.setFocusPainted(false);
                btn.setBackground(TemaVisual.PANEL);
                btn.setForeground(TemaVisual.TEXTO);
                btn.setBorder(TemaVisual.BORDER_CASILLA);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.addActionListener(e -> casillaClickeada(filaFinal, colFinal));
                botones[fila][col] = btn;
                grid3x3.add(btn);
            }
        }
        panelTableroContenedor.add(grid3x3);
        add(panelTableroContenedor, BorderLayout.CENTER);

        // ==================== PANEL SUR: BOTONES ====================
        JPanel panelSur = new JPanel(new GridLayout(1, 3, 8, 8));
        panelSur.setOpaque(false);
        panelSur.setBorder(new EmptyBorder(0, 16, 14, 16));

        btnReiniciar = crearBoton(getTextoBotonReinicio(), getColorModo());
        btnReiniciar.addActionListener(e -> nuevaPartida());

        btnMusica = crearBoton("Música: ON", TemaVisual.PANEL);
        btnMusica.addActionListener(e -> alternarMusica());

        JButton btnResetScore = crearBoton("Reiniciar Marcador", TemaVisual.PANEL);
        btnResetScore.addActionListener(e -> reiniciarMarcador());

        panelSur.add(btnReiniciar);
        panelSur.add(btnMusica);
        panelSur.add(btnResetScore);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearTarjetaMarcador(String titulo, JLabel valor, Color colorValor, Icon icono, boolean esOponente) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(TemaVisual.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            TemaVisual.BORDER_CARD,
            new EmptyBorder(6, 6, 6, 6)
        ));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        header.setOpaque(false);

        if (icono != null) {
            header.add(new JLabel(icono));
        }

        JLabel labelTitulo = new JLabel(titulo, SwingConstants.CENTER);
        labelTitulo.setFont(ReproductorMedia.obtenerFuente(11f));
        labelTitulo.setForeground(TemaVisual.MUTED);
        header.add(labelTitulo);

        if (esOponente) {
            labelNombreO = labelTitulo;
        }

        valor.setAlignmentX(Component.CENTER_ALIGNMENT);
        valor.setFont(ReproductorMedia.obtenerFuente(22f));
        valor.setForeground(colorValor);

        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(header);
        card.add(Box.createVerticalStrut(2));
        card.add(valor);
        return card;
    }

    public JButton crearBoton(String texto, Color bg) {
        JButton boton = new JButton(texto);
        boton.setFont(ReproductorMedia.obtenerFuente(12f));
        boton.setBackground(bg);
        boton.setForeground(TemaVisual.TEXTO);
        boton.setFocusPainted(false);
        boton.setBorder(TemaVisual.BORDER_CARD);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    // ==================== LÓGICA BASE ====================

    public void nuevaPartida() {
        onLimpiar();
        if (musicaActiva) {
            ReproductorMedia.iniciarMusicaFondo();
        } else {
            ReproductorMedia.detenerMusica();
        }

        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                tablero[fila][col] = VACIO;
                botones[fila][col].setText("");
                botones[fila][col].setIcon(null);
                botones[fila][col].setBackground(TemaVisual.PANEL);
                botones[fila][col].setBorder(TemaVisual.BORDER_CASILLA);
            }
        }

        juegoTerminado = false;
        onNuevaPartida();
    }

    public void aplicarMovimiento(int fila, int col, char jugador) {
        tablero[fila][col] = jugador;
        actualizarBoton(fila, col);
        ReproductorMedia.reproducirSonidoCasilla();
    }

    public void actualizarBoton(int fila, int col) {
        char val = tablero[fila][col];
        if (val == JUGADOR_X) {
            if (iconoX != null) {
                botones[fila][col].setIcon(iconoX);
                botones[fila][col].setText("");
            } else {
                botones[fila][col].setIcon(null);
                botones[fila][col].setText("X");
            }
            botones[fila][col].setForeground(TemaVisual.CYAN);
            botones[fila][col].setBackground(TemaVisual.PANEL);
        } else if (val == JUGADOR_O) {
            if (iconoO != null) {
                botones[fila][col].setIcon(iconoO);
                botones[fila][col].setText("");
            } else {
                botones[fila][col].setIcon(null);
                botones[fila][col].setText("O");
            }
            botones[fila][col].setForeground(TemaVisual.ROSA);
            botones[fila][col].setBackground(TemaVisual.PANEL);
        } else {
            botones[fila][col].setText("");
            botones[fila][col].setIcon(null);
            botones[fila][col].setBackground(TemaVisual.PANEL);
            botones[fila][col].setBorder(TemaVisual.BORDER_CASILLA);
        }
    }

    public boolean verificarFinDeJuego() {
        int[][] lineaGanadora = obtenerLineaGanadora();
        if (lineaGanadora != null) {
            juegoTerminado = true;
            onLimpiar();
            char ganador = tablero[lineaGanadora[0][0]][lineaGanadora[0][1]];

            for (int[] posicion : lineaGanadora) {
                botones[posicion[0]][posicion[1]].setBackground(TemaVisual.WIN_BG);
                botones[posicion[0]][posicion[1]].setBorder(TemaVisual.BORDER_CASILLA_WIN);
            }

            if (ganador == JUGADOR_X) {
                scoreX++;
                labelScoreX.setText(String.valueOf(scoreX));
                labelStatus.setText("¡¡VICTORIA PARA TI (" + TemaVisual.NOMBRE_JUGADOR + ")!!");
                labelStatus.setForeground(TemaVisual.ESMERALDA);
            } else {
                scoreO++;
                labelScoreO.setText(String.valueOf(scoreO));
                labelStatus.setText("¡VICTORIA DE " + getNombreOponente() + "!");
                labelStatus.setForeground(TemaVisual.ROSA);
            }
            return true;
        }

        if (tableroLleno()) {
            juegoTerminado = true;
            onLimpiar();
            scoreEmpates++;
            labelScoreEmpates.setText(String.valueOf(scoreEmpates));
            labelStatus.setText("¡Empate! Partida reñida");
            labelStatus.setForeground(TemaVisual.MUTED);
            return true;
        }
        return false;
    }

    private boolean tableroLleno() {
        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                if (tablero[fila][col] == VACIO) return false;
            }
        }
        return true;
    }

    private int[][] obtenerLineaGanadora() {
        for (int i = 0; i < 3; i++) {
            if (tablero[i][0] != VACIO && tablero[i][0] == tablero[i][1] && tablero[i][1] == tablero[i][2])
                return new int[][]{{i, 0}, {i, 1}, {i, 2}};
            if (tablero[0][i] != VACIO && tablero[0][i] == tablero[1][i] && tablero[1][i] == tablero[2][i])
                return new int[][]{{0, i}, {1, i}, {2, i}};
        }
        if (tablero[0][0] != VACIO && tablero[0][0] == tablero[1][1] && tablero[1][1] == tablero[2][2])
            return new int[][]{{0, 0}, {1, 1}, {2, 2}};
        if (tablero[0][2] != VACIO && tablero[0][2] == tablero[1][1] && tablero[1][1] == tablero[2][0])
            return new int[][]{{0, 2}, {1, 1}, {2, 0}};
        return null;
    }

    // ==================== LÓGICA COMPARTIDA DE IA ====================

    /**
     * Calcula el mejor movimiento para la IA con estrategia simple:
     * 1. Ganar de inmediato si tiene 2 en línea.
     * 2. Bloquear al jugador si está a punto de ganar.
     * 3. Elegir aleatoriamente entre casillas libres.
     * 
     * Compartido entre JuegoContraIA y JuegoSmashFrenesi para evitar duplicación.
     */
    protected int[] calcularMovimientoIA() {
        // Regla 1: Ganar de inmediato si tiene 2 en raya
        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                if (tablero[fila][col] == VACIO) {
                    tablero[fila][col] = JUGADOR_O;
                    if (hayGanador(JUGADOR_O)) {
                        tablero[fila][col] = VACIO;
                        return new int[]{fila, col};
                    }
                    tablero[fila][col] = VACIO;
                }
            }
        }

        // Regla 2: Bloquear al jugador si está a punto de ganar
        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                if (tablero[fila][col] == VACIO) {
                    tablero[fila][col] = JUGADOR_X;
                    if (hayGanador(JUGADOR_X)) {
                        tablero[fila][col] = VACIO;
                        return new int[]{fila, col};
                    }
                    tablero[fila][col] = VACIO;
                }
            }
        }

        // Regla 3: Selección aleatoria entre casillas libres
        List<int[]> libres = new ArrayList<>();
        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                if (tablero[fila][col] == VACIO) libres.add(new int[]{fila, col});
            }
        }
        return libres.isEmpty() ? null : libres.get(random.nextInt(libres.size()));
    }

    /**
     * Verifica si un jugador ha ganado (3 en línea).
     * Unifica la lógica que antes estaba duplicada en hayGanadorSimulado().
     */
    protected boolean hayGanador(char jugador) {
        int[][] linea = obtenerLineaGanadora();
        return linea != null && tablero[linea[0][0]][linea[0][1]] == jugador;
    }

    // ==================== FUNCIONES AUXILIARES ====================

    private void reiniciarMarcador() {
        scoreX = 0;
        scoreO = 0;
        scoreEmpates = 0;
        labelScoreX.setText("0");
        labelScoreO.setText("0");
        labelScoreEmpates.setText("0");
        nuevaPartida();
    }

    private void alternarMusica() {
        musicaActiva = !musicaActiva;
        btnMusica.setText(musicaActiva ? "Música: ON" : "Música: OFF");
        if (musicaActiva) {
            ReproductorMedia.iniciarMusicaFondo();
        } else {
            ReproductorMedia.detenerMusica();
        }
    }

    protected void volverAlMenu() {
        onLimpiar();
        ReproductorMedia.detenerMusica();
        dispose();
        if (menuPadre != null) {
            menuPadre.setLocationRelativeTo(null);
            menuPadre.setVisible(true);
        } else {
            SwingUtilities.invokeLater(() -> {
                MenuPrincipal menu = new MenuPrincipal();
                menu.setVisible(true);
            });
        }
    }

    /**
     * Hook para que los modos puedan insertar controles adicionales en la cabecera
     */
    protected void agregarComponentesExtraNorte() {}

    // ==================== MÉTODOS ABSTRACTOS PARA LOS MODOS ====================
    protected abstract void casillaClickeada(int fila, int col);
    protected abstract void onNuevaPartida();
    protected abstract void onLimpiar();
    protected abstract String getNombreModo();
    protected abstract Color getColorModo();
    protected abstract String getNombreOponente();
    protected abstract String getTextoBotonReinicio();
}

