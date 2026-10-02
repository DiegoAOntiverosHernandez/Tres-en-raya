import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;

/**
 * Minijuego de combate para disputar casillas disputadas:
 * 1. Ventana modal sin barra de título del sistema (undecorated) para impedir que se cierre.
 * 2. Máquina competitiva escalada con mecánicas de furia y contraataque según la dificultad elegida en el tablero.
 * 3. Cuenta regresiva de 10 segundos o K.O. directo al machacar [ESPACIO] o el botón.
 * 
 * Usa MouseAdapter/MouseMotionAdapter en lugar de interfaces completas para evitar métodos vacíos.
 * El manejo de teclas se delega exclusivamente al KeyEventDispatcher (eliminado KeyListener redundante).
 */
public class DueloMinijuego extends JDialog {

    private final int fila;
    private final int col;
    private char ganador = ' ';

    // Estado del combate
    private int balance = 0; // -50 (Máquina gana) a +50 (Jugador gana)
    private int pulsadasJugador = 0;
    private int pulsadasIA = 0;
    private double tiempoRestante = 10.0;
    private boolean spaceHeld = false;
    private Point puntoArrastre;

    // Timers
    private Timer timerReloj;
    private Timer timerIA;
    private Timer timerFlashBoton;
    private KeyEventDispatcher keyDispatcher;

    // Componentes visuales
    private JLabel labelReloj;
    private JProgressBar barraChoque;
    private JLabel labelContadores;
    private JButton btnEspacioVisual;

    private final Random random = new Random();

    public DueloMinijuego(JFrame parent, int fila, int col) {
        super(parent, true);
        setUndecorated(true); // Elimina de raíz la barra del SO y el botón de cerrar [X]
        this.fila = fila;
        this.col = col;

        configurarVentana();
        // Los timers y el keyDispatcher se activan DESPUÉS de la cuenta regresiva
    }

    /**
     * Método principal para lanzar todo el ciclo del duelo
     */
    public static char iniciar(JFrame parent, int fila, int col, boolean conMusica) {
        // Iniciar música Deadlock OST - Start King para el duelo
        if (conMusica) {
            ReproductorMedia.iniciarMusicaDuelo();
        }

        // 3. Abrir ventana de duelo de 10 segundos
        DueloMinijuego dialogo = new DueloMinijuego(parent, fila, col);
        dialogo.setVisible(true); // Bloquea hasta que el combate termine

        return dialogo.ganador;
    }

    private void configurarVentana() {
        setSize(460, 460);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(TemaVisual.BG);
        setLayout(new BorderLayout(0, 14));

        // Borde arcade dorado y padding para que resalte como tarjeta de combate
        ((JComponent) getContentPane()).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaVisual.ORO, 3),
                BorderFactory.createEmptyBorder(10, 14, 14, 14)));

        // Bloquear cualquier intento de cierre por teclado (Alt+F4)
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Arrastre de ventana con adapters (sin interfaces completas)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                puntoArrastre = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (puntoArrastre != null) {
                    Point pos = getLocation();
                    setLocation(pos.x + e.getX() - puntoArrastre.x, pos.y + e.getY() - puntoArrastre.y);
                }
            }
        });
        setFocusable(true);

        // ==================== PANEL NORTE ====================
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);
        panelNorte.setBorder(new EmptyBorder(8, 12, 0, 12));

        // Título del combate
        JLabel labelTitulo = new JLabel("¡CHOQUE POR LA CASILLA [" + (fila + 1) + "," + (col + 1) + "]!",
                SwingConstants.CENTER);
        labelTitulo.setFont(ReproductorMedia.obtenerFuente(19f));
        labelTitulo.setForeground(TemaVisual.ORO);
        labelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelReloj = new JLabel("Tiempo: 10.0s", SwingConstants.CENTER);
        labelReloj.setFont(ReproductorMedia.obtenerFuente(22f));
        labelReloj.setForeground(TemaVisual.ROJO);
        labelReloj.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelNorte.add(labelTitulo);
        panelNorte.add(Box.createVerticalStrut(6));
        panelNorte.add(labelReloj);
        add(panelNorte, BorderLayout.NORTH);

        // ==================== PANEL CENTRO ====================
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Cabecera del Choque: Archmother vs Hidden King
        ImageIcon iconJugador = ReproductorMedia.cargarIconoPersonaje('X', 36);
        ImageIcon iconIA = ReproductorMedia.cargarIconoPersonaje('O', 36);

        JPanel panelVersus = new JPanel(new BorderLayout());
        panelVersus.setOpaque(false);
        panelVersus.setPreferredSize(new Dimension(380, 40));
        panelVersus.setMaximumSize(new Dimension(380, 40));

        JPanel panelIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        panelIzquierda.setOpaque(false);
        if (iconJugador != null) panelIzquierda.add(new JLabel(iconJugador));
        JLabel lblJugador = new JLabel(TemaVisual.NOMBRE_JUGADOR);
        lblJugador.setFont(ReproductorMedia.obtenerFuente(13f));
        lblJugador.setForeground(TemaVisual.CYAN);
        panelIzquierda.add(lblJugador);

        JLabel lblVs = new JLabel("VS", SwingConstants.CENTER);
        lblVs.setFont(ReproductorMedia.obtenerFuente(15f));
        lblVs.setForeground(TemaVisual.ORO);

        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        panelDerecha.setOpaque(false);
        JLabel lblOponente = new JLabel(TemaVisual.NOMBRE_OPONENTE + " (" + Dificultad.getActual().getEtiqueta() + ")");
        lblOponente.setFont(ReproductorMedia.obtenerFuente(13f));
        lblOponente.setForeground(TemaVisual.ORO);
        panelDerecha.add(lblOponente);
        if (iconIA != null) panelDerecha.add(new JLabel(iconIA));

        panelVersus.add(panelIzquierda, BorderLayout.WEST);
        panelVersus.add(lblVs, BorderLayout.CENTER);
        panelVersus.add(panelDerecha, BorderLayout.EAST);
        panelVersus.setAlignmentX(Component.CENTER_ALIGNMENT);

        barraChoque = new JProgressBar(0, 100);
        barraChoque.setValue(50);
        barraChoque.setPreferredSize(new Dimension(380, 30));
        barraChoque.setMaximumSize(new Dimension(380, 30));
        barraChoque.setForeground(TemaVisual.CYAN);
        barraChoque.setBackground(TemaVisual.ORO);
        barraChoque.setBorder(BorderFactory.createLineBorder(TemaVisual.BORDER, 2));
        barraChoque.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelContadores = new JLabel(TemaVisual.NOMBRE_JUGADOR + ": 0  |  " + TemaVisual.NOMBRE_OPONENTE + ": 0", SwingConstants.CENTER);
        labelContadores.setFont(ReproductorMedia.obtenerFuente(15f));
        labelContadores.setForeground(TemaVisual.TEXTO);
        labelContadores.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnEspacioVisual = new JButton("¡¡MACHACA [ ESPACIO ]!!");
        btnEspacioVisual.setFont(ReproductorMedia.obtenerFuente(19f));
        btnEspacioVisual.setBackground(TemaVisual.PANEL);
        btnEspacioVisual.setForeground(TemaVisual.CYAN);
        btnEspacioVisual.setFocusPainted(false);
        btnEspacioVisual.setFocusable(false);
        btnEspacioVisual.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEspacioVisual.setBorder(BorderFactory.createLineBorder(TemaVisual.CYAN, 2));
        btnEspacioVisual.setPreferredSize(new Dimension(340, 75));
        btnEspacioVisual.setMaximumSize(new Dimension(340, 75));
        btnEspacioVisual.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEspacioVisual.addActionListener(e -> pulsadaJugador());

        panelCentro.add(panelVersus);
        panelCentro.add(Box.createVerticalStrut(8));
        panelCentro.add(barraChoque);
        panelCentro.add(Box.createVerticalStrut(12));
        panelCentro.add(labelContadores);
        panelCentro.add(Box.createVerticalStrut(16));
        panelCentro.add(btnEspacioVisual);
        add(panelCentro, BorderLayout.CENTER);

        timerFlashBoton = new Timer(70, e -> btnEspacioVisual.setBackground(TemaVisual.PANEL));
        timerFlashBoton.setRepeats(false);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                toFront();
                requestFocus();
                requestFocusInWindow();
                iniciarCuentaRegresiva();
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                removerKeyDispatcher();
            }
        });

        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                spaceHeld = false;
            }

            @Override
            public void windowLostFocus(java.awt.event.WindowEvent e) {
                spaceHeld = false;
            }
        });
    }

    /**
     * Cuenta regresiva visual 3-2-1-GO! para que el jugador se prepare antes del duelo.
     * Los controles y timers del combate se activan solo al terminar la cuenta.
     */
    private void iniciarCuentaRegresiva() {
        btnEspacioVisual.setEnabled(false);
        labelReloj.setFont(ReproductorMedia.obtenerFuente(36f));
        labelReloj.setText("3");
        labelReloj.setForeground(TemaVisual.ORO);

        String[] pasos = {"2", "1", "¡¡GO!!"};
        Color[] colores = {TemaVisual.NARANJA, TemaVisual.ROJO, TemaVisual.ESMERALDA};

        Timer timerCuenta = new Timer(800, null);
        final int[] indice = {0};
        timerCuenta.addActionListener(e -> {
            if (indice[0] < pasos.length) {
                labelReloj.setText(pasos[indice[0]]);
                labelReloj.setForeground(colores[indice[0]]);
                indice[0]++;
            } else {
                timerCuenta.stop();
                labelReloj.setFont(ReproductorMedia.obtenerFuente(22f));
                labelReloj.setText("Tiempo: 10.0s");
                labelReloj.setForeground(TemaVisual.ROJO);
                btnEspacioVisual.setEnabled(true);
                registrarKeyDispatcher();
                iniciarTimers();
            }
        });
        timerCuenta.setInitialDelay(800);
        timerCuenta.start();
    }

    private void iniciarTimers() {
        // Reloj regresivo de 10 segundos
        timerReloj = new Timer(100, e -> {
            tiempoRestante -= 0.1;
            if (tiempoRestante <= 0) {
                tiempoRestante = 0;
                finalizarDuelo(balance >= 0 ? 'X' : 'O', false);
            } else {
                labelReloj.setText(String.format("Tiempo: %.1fs", tiempoRestante));
            }
        });
        timerReloj.start();

        // Pulsadas de la máquina con velocidad según la dificultad seleccionada
        int delayInicial = Dificultad.getActual().getDelayMinijuegoMs();
        timerIA = new Timer(delayInicial, e -> ejecutarPulsadaIA());
        timerIA.start();
    }

    private void ejecutarPulsadaIA() {
        if (ganador != ' ') return;

        Dificultad dificultad = Dificultad.getActual();
        double chance = dificultad.getProbExitoMinijuego();
        int empuje = 2;

        // --- COMPORTAMIENTO DINÁMICO Y COMPETITIVO ---
        if (dificultad == Dificultad.DIFICIL) {
            // En Difícil: El Hidden King es un rival feroz y no se rinde
            if (balance > 10) {
                // Ráfaga Frenética cuando el jugador lleva ventaja
                chance = 0.96;
                timerIA.setDelay(75); // ~13.3 pulsadas/segundo
                if (random.nextDouble() < 0.25) {
                    empuje = 3; // Impacto crítico para empujar al jugador
                }
            } else {
                timerIA.setDelay(dificultad.getDelayMinijuegoMs()); // 90ms
            }
        } else if (dificultad == Dificultad.NORMAL) {
            // En Normal: Desafío constante y reactivo
            if (balance > 15) {
                chance = 0.92;
                timerIA.setDelay(100);
            } else {
                timerIA.setDelay(dificultad.getDelayMinijuegoMs()); // 115ms
            }
        } else {
            // En Fácil: Menor resistencia
            if (balance < -15) {
                chance = 0.45; // Relaja el ritmo si va ganando para permitir remontar
            }
            timerIA.setDelay(dificultad.getDelayMinijuegoMs()); // 160ms
        }

        if (random.nextDouble() < chance) {
            pulsadasIA++;
            balance -= empuje;
            actualizarUI();

            if (balance <= -48) {
                finalizarDuelo('O', true);
            }
        }
    }

    private void pulsadaJugador() {
        if (ganador != ' ') return;

        pulsadasJugador++;
        balance += 2;
        actualizarUI();

        // Parpadeo visual del botón al pulsar
        btnEspacioVisual.setBackground(TemaVisual.FLASH_BTN);
        timerFlashBoton.restart();

        if (balance >= 48) {
            finalizarDuelo('X', true);
        }
    }

    private void actualizarUI() {
        int progreso = Math.max(0, Math.min(100, 50 + balance));
        barraChoque.setValue(progreso);
        labelContadores.setText(TemaVisual.NOMBRE_JUGADOR + ": " + pulsadasJugador
            + "  |  " + TemaVisual.NOMBRE_OPONENTE + ": " + pulsadasIA);
    }

    private void registrarKeyDispatcher() {
        keyDispatcher = e -> {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (!spaceHeld) {
                        spaceHeld = true;
                        pulsadaJugador();
                    }
                    return true;
                } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                    spaceHeld = false;
                    return true;
                }
            }
            return false;
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyDispatcher);
    }

    private void removerKeyDispatcher() {
        if (keyDispatcher != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyDispatcher);
            keyDispatcher = null;
        }
    }

    private void finalizarDuelo(char winner, boolean porKO) {
        removerKeyDispatcher();
        detenerTimers();
        ReproductorMedia.detenerMusica(); // Quitar la música de inmediato

        ganador = winner;

        if (winner == 'X') {
            ReproductorMedia.reproducirSonidoVictoria();
        } else {
            ReproductorMedia.reproducirSonidoDerrota();
        }

        // Crear mini pantalla de resultado
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel panelResultado = new JPanel();
        panelResultado.setLayout(new BoxLayout(panelResultado, BoxLayout.Y_AXIS));
        panelResultado.setOpaque(false);
        panelResultado.setBorder(new EmptyBorder(40, 20, 40, 20));

        JLabel labelTituloRes = new JLabel(winner == 'X' ? "¡VICTORIA!" : "¡DERROTA!", SwingConstants.CENTER);
        labelTituloRes.setFont(ReproductorMedia.obtenerFuente(36f));
        labelTituloRes.setForeground(winner == 'X' ? TemaVisual.ESMERALDA : TemaVisual.ROJO);
        labelTituloRes.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelMotivo = new JLabel(porKO ? "Por K.O." : "Por Tiempo", SwingConstants.CENTER);
        labelMotivo.setFont(ReproductorMedia.obtenerFuente(18f));
        labelMotivo.setForeground(TemaVisual.MUTED);
        labelMotivo.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon iconGanador = ReproductorMedia.cargarIconoPersonaje(winner, 120);
        JLabel labelIcono = new JLabel(iconGanador);
        labelIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        Component nombreComp = null;
        java.io.File fileNameImg = ReproductorMedia.resolverArchivo(winner == 'X' ? "recursos/imagenes/1920px-Archmother_name.png" : "recursos/imagenes/1920px-Hidden_King_name.png");
        if (fileNameImg.exists()) {
            try {
                Image img = javax.imageio.ImageIO.read(fileNameImg);
                nombreComp = new JLabel(new ImageIcon(img.getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            } catch (Exception ex) {}
        }
        
        if (nombreComp == null) {
            JLabel labelNombre = new JLabel(winner == 'X' ? TemaVisual.NOMBRE_JUGADOR : TemaVisual.NOMBRE_OPONENTE, SwingConstants.CENTER);
            labelNombre.setFont(ReproductorMedia.obtenerFuente(24f));
            labelNombre.setForeground(winner == 'X' ? TemaVisual.CYAN : TemaVisual.ORO);
            nombreComp = labelNombre;
        }
        ((JComponent)nombreComp).setAlignmentX(Component.CENTER_ALIGNMENT);

        panelResultado.add(Box.createVerticalGlue());
        panelResultado.add(labelTituloRes);
        panelResultado.add(Box.createVerticalStrut(10));
        panelResultado.add(labelMotivo);
        panelResultado.add(Box.createVerticalStrut(30));
        panelResultado.add(labelIcono);
        panelResultado.add(Box.createVerticalStrut(20));
        panelResultado.add(nombreComp);
        panelResultado.add(Box.createVerticalGlue());

        add(panelResultado, BorderLayout.CENTER);
        revalidate();
        repaint();

        Timer fin = new Timer(4500, e -> dispose());
        fin.setRepeats(false);
        fin.start();
    }

    private void detenerTimers() {
        if (timerReloj != null)
            timerReloj.stop();
        if (timerIA != null)
            timerIA.stop();
        if (timerFlashBoton != null)
            timerFlashBoton.stop();
    }

    @Override
    public void dispose() {
        removerKeyDispatcher();
        detenerTimers();
        ReproductorMedia.detenerMusica();
        super.dispose();
    }
}
