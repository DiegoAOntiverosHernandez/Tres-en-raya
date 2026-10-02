import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * TRES EN RAYA - MENÚ PRINCIPAL
 * 
 * Pantalla de bienvenida con selector visual de modos de juego:
 * 1. SMASH FRENESÍ (Tiempo real + Duelos)
 * 2. Contra la maquina (Turnos clásicos)
 * 3. 1 vs 1 (Local)
 */
public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        super("Tres en Raya - Menú Principal");
        inicializarUI();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            ReproductorMedia.iniciarMusicaMenu();
        }
    }

    private void inicializarUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            java.io.File iconFile = ReproductorMedia.resolverArchivo("recursos/imagenes/images.jpg");
            if (iconFile.exists()) {
                setIconImage(new ImageIcon(iconFile.getAbsolutePath()).getImage());
            }
        } catch(Exception e) {}
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                ReproductorMedia.detenerMusica();
                System.exit(0);
            }
        });
        setSize(480, 680);
        setMinimumSize(new Dimension(440, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(TemaVisual.BG);
        setLayout(new BorderLayout(0, 16));

        // ==================== HEADER ====================
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setOpaque(false);
        panelHeader.setBorder(new EmptyBorder(24, 20, 8, 20));

        JLabel labelSub = new JLabel("EDICIÓN ARCADE", SwingConstants.CENTER);
        labelSub.setFont(ReproductorMedia.obtenerFuente(13f));
        labelSub.setForeground(TemaVisual.CYAN);
        labelSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelTit = new JLabel("TRES EN RAYA", SwingConstants.CENTER);
        labelTit.setFont(ReproductorMedia.obtenerFuente(34f));
        labelTit.setForeground(TemaVisual.TEXTO);
        labelTit.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelDesc = new JLabel("Elige un modo de juego para comenzar", SwingConstants.CENTER);
        labelDesc.setFont(ReproductorMedia.obtenerFuente(Font.PLAIN, 12f));
        labelDesc.setForeground(TemaVisual.MUTED);
        labelDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelHeader.add(labelSub);
        panelHeader.add(Box.createVerticalStrut(4));
        panelHeader.add(labelTit);
        panelHeader.add(Box.createVerticalStrut(4));
        panelHeader.add(labelDesc);
        add(panelHeader, BorderLayout.NORTH);

        // ==================== PANEL MODOS (CENTRO) ====================
        JPanel panelModos = new JPanel(new GridLayout(3, 1, 0, 14));
        panelModos.setOpaque(false);
        panelModos.setBorder(new EmptyBorder(4, 24, 8, 24));

        // Tarjeta 1: Smash Frenesí
        panelModos.add(crearTarjetaModo(
                "TIEMPO REAL + DUELOS",
                "SMASH FRENESÍ",
                "¡Sin turnos! La IA carga casillas en vivo. Disputa su casilla para desatar el minijuego de machacar espacio.",
                TemaVisual.ROJO,
                TresEnRaya.MODO_SMASH_FRENESI));

        // Tarjeta 2: Contra IA Clásico
        panelModos.add(crearTarjetaModo(
                "VS COMPUTADORA",
                "CONTRA LA IA",
                "Modo clásico por turnos contra el algoritmo inteligente de la máquina.",
                TemaVisual.ROSA,
                TresEnRaya.MODO_IA_CLASICO));

        // Tarjeta 3: 1 vs 1 Local
        panelModos.add(crearTarjetaModo(
                "MULTIJUGADOR LOCAL",
                "1 VS 1 (LOCAL)",
                "Desafía a un amigo en la misma pantalla por turnos tradicionales.",
                TemaVisual.CYAN,
                TresEnRaya.MODO_1VS1));

        add(panelModos, BorderLayout.CENTER);

        // ==================== PANEL INFERIOR ====================
        JPanel panelSur = new JPanel(new BorderLayout(0, 8));
        panelSur.setOpaque(false);
        panelSur.setBorder(new EmptyBorder(8, 24, 20, 24));

        JButton btnSalir = new JButton("Salir del Juego");
        btnSalir.setFont(ReproductorMedia.obtenerFuente(13f));
        btnSalir.setBackground(TemaVisual.PANEL);
        btnSalir.setForeground(TemaVisual.MUTED);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(BorderFactory.createLineBorder(TemaVisual.BORDER, 1));
        btnSalir.setPreferredSize(new Dimension(0, 42));
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSalir.setBackground(TemaVisual.EXIT_HOVER_BG);
                btnSalir.setForeground(TemaVisual.EXIT_HOVER_FG);
                btnSalir.setBorder(BorderFactory.createLineBorder(TemaVisual.ROJO, 1));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSalir.setBackground(TemaVisual.PANEL);
                btnSalir.setForeground(TemaVisual.MUTED);
                btnSalir.setBorder(BorderFactory.createLineBorder(TemaVisual.BORDER, 1));
            }
        });
        btnSalir.addActionListener(e -> {
            ReproductorMedia.detenerMusica();
            System.exit(0);
        });

        panelSur.add(btnSalir, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearTarjetaModo(String tag, String titulo, String descripcion, Color colorAcento, int modo) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setBackground(TemaVisual.PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaVisual.BORDER, 1),
                new EmptyBorder(12, 16, 12, 16)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Parte superior: Tag y Título
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel labelTag = new JLabel(tag);
        labelTag.setFont(ReproductorMedia.obtenerFuente(11f));
        labelTag.setForeground(colorAcento);

        JLabel labelTit = new JLabel(titulo);
        labelTit.setFont(ReproductorMedia.obtenerFuente(19f));
        labelTit.setForeground(TemaVisual.TEXTO);

        headerPanel.add(labelTag);
        headerPanel.add(Box.createVerticalStrut(2));
        headerPanel.add(labelTit);
        card.add(headerPanel, BorderLayout.NORTH);

        // Parte central: Descripción en HTML para auto-wrap
        JLabel labelDesc = new JLabel(
                "<html><body style='width: 320px; color: #94a3b8; font-family: Segoe UI; font-size: 11px;'>"
                        + descripcion + "</body></html>");
        card.add(labelDesc, BorderLayout.CENTER);

        // Indicador de flecha a la derecha
        JLabel labelFlecha = new JLabel("→", SwingConstants.CENTER);
        labelFlecha.setFont(new Font("Segoe UI", Font.BOLD, 22));
        labelFlecha.setForeground(TemaVisual.MUTED);
        labelFlecha.setPreferredSize(new Dimension(30, 0));
        card.add(labelFlecha, BorderLayout.EAST);

        // Efectos hover y click
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(TemaVisual.CARD_HOVER);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorAcento, 2),
                        new EmptyBorder(11, 15, 11, 15)));
                labelFlecha.setForeground(colorAcento);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(TemaVisual.PANEL);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(TemaVisual.BORDER, 1),
                        new EmptyBorder(12, 16, 12, 16)));
                labelFlecha.setForeground(TemaVisual.MUTED);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                abrirJuego(modo);
            }
        });

        return card;
    }

    private void abrirJuego(int modo) {
        setVisible(false);
        SwingUtilities.invokeLater(() -> {
            TableroBase juego;
            switch (modo) {
                case TresEnRaya.MODO_SMASH_FRENESI:
                    juego = new JuegoSmashFrenesi(this);
                    break;
                case TresEnRaya.MODO_IA_CLASICO:
                    juego = new JuegoContraIA(this);
                    break;
                default:
                    juego = new Juego1vs1(this);
                    break;
            }
            juego.setVisible(true);
        });
    }
}
