import javax.swing.SwingUtilities;

/**
 * TRES EN RAYA - PUNTO DE ENTRADA PRINCIPAL
 * 
 * Cada modo de juego ha sido separado en su propio archivo .java independiente
 * para que el codigo sea limpio, modular y facil de leer:
 * 
 * 1. TableroBase.java:
 *    Clase base con la ventana visual, diseno Slate moderno, marcadores,
 *    cuadricula 3x3, verificacion de victorias y boton de salir al menu.
 * 
 * 2. Juego1vs1.java:
 *    Modo multijugador local por turnos (Jugador X vs Jugador O).
 * 
 * 3. JuegoContraIA.java:
 *    Modo clasico por turnos contra la computadora (máquina).
 * 
 * 4. JuegoSmashFrenesi.java:
 *    Modo en tiempo real sin turnos. La máquina carga casillas en vivo y si
 *    el jugador disputa la misma casilla se desata el duelo de machaque.
 * 
 * 5. DueloMinijuego.java:
 *    Minijuego de barra de choque y pulsaciones rapidas de espacio.
 * 
 * 6. MenuPrincipal.java:
 *    Menu visual para seleccionar el modo de juego y navegar.
 * 
 * 7. ReproductorMedia.java:
 *    Control de audio y video para el juego.
 */
public class TresEnRaya {

    // Identificadores de modo de juego
    public static final int MODO_1VS1 = 0;
    public static final int MODO_IA_CLASICO = 1;
    public static final int MODO_SMASH_FRENESI = 2;

    /**
     * Inicia el juego abriendo directamente el menu principal.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }
}
