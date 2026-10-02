import java.io.File;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/**
 * Clase multimedia con rutas totalmente dinámicas y relativas al proyecto:
 * - recursos/video/ para videos como la intro de Smash Bros.
 * - recursos/musica/ para música de combate (Gas Gas Gas).
 * - recursos/audio/ para efectos de sonido.
 */
public class ReproductorMedia {

    private static Process procesoMusica = null;
    private static String tipoMusicaActual = "";

    /**
     * Obtiene la carpeta raíz donde reside el proyecto (.jar o clases compiladas)
     */
    public static File obtenerDirectorioBase() {
        try {
            File codeSource = new File(
                    ReproductorMedia.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (codeSource.isFile()) {
                return codeSource.getParentFile();
            }
            if (codeSource.isDirectory() && codeSource.getName().equalsIgnoreCase("bin")) {
                return codeSource.getParentFile();
            }
            return codeSource;
        } catch (Exception e) {
            System.err.println("Error al obtener directorio base: " + e.getMessage());
            return new File(".");
        }
    }

    /**
     * Resuelve un archivo dinámicamente buscando en la raíz del proyecto o en el
     * directorio actual.
     * Solo busca por ruta exacta y por directorio actual, sin fallback genérico
     * peligroso.
     */
    public static File resolverArchivo(String rutaRelativa) {
        File base = obtenerDirectorioBase();
        File archivo = new File(base, rutaRelativa);
        if (archivo.exists())
            return archivo;

        File archivoActual = new File(rutaRelativa);
        if (archivoActual.exists())
            return archivoActual;

        return archivo; // Retorna la referencia original (puede no existir)
    }

    public static File obtenerVideoIntro() {
        return resolverArchivo("recursos/video/Super Smash Bros. Ultimate _3 2 1 GO!_ Green Screen (4K).mp4");
    }

    public static File obtenerMusicaGas() {
        return resolverArchivo("recursos/musica/Manuel - Gas Gas Gas.mp3");
    }

    public static File obtenerMusicaDuelo() {
        File archivo = resolverArchivo("recursos/musica/Deadlock OST - Start King.mp3");
        if (archivo.exists()) return archivo;
        File archivoPorPatron = buscarArchivoPorPatron("recursos/musica", "start", "king");
        return (archivoPorPatron != null && archivoPorPatron.exists()) ? archivoPorPatron : archivo;
    }

    public static File obtenerMusicaMenu() {
        File archivo = resolverArchivo("recursos/musica/Music_hideout_play_base_lp_155bpm.mp3");
        if (archivo.exists())
            return archivo;
        File archivoPorPatron = buscarArchivoPorPatron("recursos/musica", "hideout");
        return (archivoPorPatron != null && archivoPorPatron.exists()) ? archivoPorPatron : archivo;
    }

    public static File obtenerMusicaFondo() {
        File archivo = resolverArchivo("recursos/musica/Deadlock Soundtrack - Sinners Sacrifice.mp3");
        if (archivo.exists())
            return archivo;

        // Fallback: buscar por patrón en el directorio de música
        File archivoPorPatron = buscarArchivoPorPatron("recursos/musica", "deadlock", "sinner", "sacrifice");
        return (archivoPorPatron != null && archivoPorPatron.exists()) ? archivoPorPatron : archivo;
    }

    /**
     * Busca un archivo en un directorio relativo que contenga al menos uno de los
     * patrones dados.
     * Unificado: antes existían buscarArchivoPorPatron() y buscarImagenPorPatron()
     * separados.
     */
    private static File buscarArchivoPorPatron(String dirRelativo, String... patrones) {
        File base = obtenerDirectorioBase();
        File dir = new File(base, dirRelativo);
        if (!dir.exists())
            dir = new File(dirRelativo);
        if (!dir.exists() || !dir.isDirectory())
            return null;

        File[] files = dir.listFiles();
        if (files == null)
            return null;

        for (File archivo : files) {
            if (!archivo.isFile())
                continue;
            String name = archivo.getName().toLowerCase();
            for (String patron : patrones) {
                if (name.contains(patron.toLowerCase()))
                    return archivo;
            }
        }
        return null;
    }

    /**
     * Busca una imagen en los directorios de recursos que contenga al menos uno de
     * los patrones.
     * Usa buscarArchivoPorPatron internamente, eliminando la duplicación previa.
     */
    private static File buscarImagenPorPatron(String... patrones) {
        // Corregido: eliminado el typo "iamgenes" que existía como fallback
        String[] dirs = { "recursos/imagenes", "recursos" };
        for (String dir : dirs) {
            File resultado = buscarArchivoPorPatron(dir, patrones);
            if (resultado != null)
                return resultado;
        }
        return null;
    }

    public static File obtenerImagenX() {
        File archivo = resolverArchivo("recursos/imagenes/Archmother.png");
        if (archivo.exists())
            return archivo;
        File archivoPorPatron = buscarImagenPorPatron("arch", "archmother");
        return archivoPorPatron != null ? archivoPorPatron : archivo;
    }

    public static File obtenerImagenO() {
        File archivo = resolverArchivo("recursos/imagenes/Hidden_King.png");
        if (archivo.exists())
            return archivo;
        File archivoPorPatron = buscarImagenPorPatron("hidden", "king");
        return archivoPorPatron != null ? archivoPorPatron : archivo;
    }

    public static ImageIcon cargarIconoPersonaje(char jugador, int tamano) {
        try {
            boolean esX = (jugador == 'X' || jugador == 'x');
            File archivo = esX ? obtenerImagenX() : obtenerImagenO();
            Image img = null;
            if (archivo != null && archivo.exists()) {
                img = ImageIO.read(archivo);
            }
            if (img == null) {
                String recurso = esX ? "/recursos/imagenes/Archmother.png" : "/recursos/imagenes/Hidden_King.png";
                java.net.URL url = ReproductorMedia.class.getResource(recurso);
                if (url != null) {
                    img = ImageIO.read(url);
                }
            }
            if (img != null) {
                Image scaled = img.getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen del personaje " + jugador + ": " + e.getMessage());
        }
        return null;
    }

    private static Font fuenteJuego = null;

    public static Font obtenerFuente(int estilo, float tamano) {
        if (fuenteJuego == null) {
            cargarFuenteJuego();
        }
        if (fuenteJuego != null) {
            return fuenteJuego.deriveFont(estilo, tamano);
        }
        return new Font("Segoe UI", estilo, (int) tamano);
    }

    public static Font obtenerFuente(float tamano) {
        return obtenerFuente(Font.BOLD, tamano);
    }

    /**
     * Carga la fuente personalizada con 2 niveles de fallback:
     * 1. Archivo externo vía resolverArchivo().
     * 2. Recurso embebido vía getResourceAsStream().
     * Simplificado: eliminado el nivel intermedio redundante de listar .ttf del
     * directorio,
     * ya que resolverArchivo() ya busca en el directorio.
     */
    private static synchronized void cargarFuenteJuego() {
        if (fuenteJuego != null)
            return;
        try {
            // Nivel 1: Archivo externo
            File archivo = resolverArchivo("recursos/fuente/ValvePulp-Bold.ttf");
            if (archivo.exists()) {
                fuenteJuego = Font.createFont(Font.TRUETYPE_FONT, archivo);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuenteJuego);
                return;
            }
            // Nivel 2: Recurso embebido (para JAR)
            java.io.InputStream is = ReproductorMedia.class.getResourceAsStream("/recursos/fuente/ValvePulp-Bold.ttf");
            if (is != null) {
                fuenteJuego = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuenteJuego);
                return;
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar la fuente personalizada Valve Pulp: " + e.getMessage());
        }
    }

    /**
     * Versión síncrona: bloquea hasta que termine el video de intro de Smash Bros.
     */
    public static void reproducirIntroSmash() {
        File video = obtenerVideoIntro();
        if (!video.exists())
            return;

        File script = resolverArchivo("play_intro.ps1");
        if (!script.exists())
            return;

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell",
                    "-NoProfile",
                    "-ExecutionPolicy", "Bypass",
                    "-File", script.getAbsolutePath(),
                    video.getAbsolutePath());
            Process proceso = pb.start();
            proceso.waitFor();
        } catch (Exception e) {
            System.err.println("Error al reproducir intro: " + e.getMessage());
        }
    }

    /**
     * Versión asíncrona: reproduce en un hilo separado y ejecuta el callback al
     * terminar.
     */
    public static void reproducirIntroSmash(Runnable onComplete) {
        new Thread(() -> {
            reproducirIntroSmash();
            if (onComplete != null)
                SwingUtilities.invokeLater(onComplete);
        }).start();
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ReproductorMedia::detenerMusica));
    }

    /**
     * Método genérico unificado para iniciar música.
     * Antes existían iniciarMusicaFondo() e iniciarMusicaGas() con ~30 líneas
     * duplicadas.
     */
    private static synchronized void iniciarMusica(String tipo, File audio, String volumen) {
        if (tipo.equals(tipoMusicaActual) && procesoMusica != null && procesoMusica.isAlive()) {
            return;
        }
        detenerMusica();
        if (audio == null || !audio.exists())
            return;
        File script = resolverArchivo("play_music.ps1");
        if (script == null || !script.exists())
            return;
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell", "-NoProfile", "-ExecutionPolicy", "Bypass",
                    "-File", script.getAbsolutePath(),
                    audio.getAbsolutePath(), volumen);
            procesoMusica = pb.start();
            tipoMusicaActual = tipo;
        } catch (Exception e) {
            System.err.println("Error al iniciar música (" + tipo + "): " + e.getMessage());
            procesoMusica = null;
            tipoMusicaActual = "";
        }
    }

    public static void iniciarMusicaMenu() {
        iniciarMusica("MENU", obtenerMusicaMenu(), "0.5");
    }

    /**
     * Inicia la música de fondo ambiental (Deadlock Soundtrack - Sinners Sacrifice)
     * para los modos de juego
     */
    public static void iniciarMusicaFondo() {
        iniciarMusica("FONDO", obtenerMusicaFondo(), "0.75");
    }

    /**
     * Inicia la música de combate rápido (Gas Gas Gas) exclusiva para el minijuego
     * de pulsadas
     */
    public static void iniciarMusicaGas() {
        iniciarMusica("GAS", obtenerMusicaGas(), "1.0");
    }

    /**
     * Inicia la música de duelo (Deadlock OST - Start King) para el minijuego de pulsadas
     */
    public static void iniciarMusicaDuelo() {
        iniciarMusica("DUELO", obtenerMusicaDuelo(), "0.5");
    }

    // ==================== EFECTO DE SONIDO: CASILLA ====================

    public static File obtenerSonidoCasilla() {
        File archivo = resolverArchivo("recursos/audio/deadlock-orb-deny.mp3");
        if (archivo.exists())
            return archivo;
        File archivoPorPatron = buscarArchivoPorPatron("recursos/audio", "deadlock", "orb", "deny");
        return (archivoPorPatron != null && archivoPorPatron.exists()) ? archivoPorPatron : archivo;
    }

    /**
     * Reproduce el efecto de sonido "deadlock-orb-deny" al colocar ficha en una
     * casilla.
     * Se ejecuta como proceso de fuego-y-olvido en un hilo aparte para no bloquear
     * la UI.
     * Usa PowerShell con MediaPlayer de WPF para reproducción instantánea sin
     * bucle.
     */
    public static void reproducirSonidoCasilla() {
        File audio = obtenerSonidoCasilla();
        if (audio == null || !audio.exists())
            return;
        new Thread(() -> {
            try {
                String script = "Add-Type -AssemblyName PresentationCore;" +
                        "Add-Type -AssemblyName WindowsBase;" +
                        "$p = New-Object System.Windows.Media.MediaPlayer;" +
                        "$p.Open([Uri]'" + audio.getAbsolutePath().replace("'", "''") + "');" +
                        "$p.Volume = 0.3;" +
                        "$p.Play();" +
                        "Start-Sleep -Milliseconds 2500;" +
                        "$p.Stop();" +
                        "$p.Close()";
                new ProcessBuilder("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", script)
                        .start();
            } catch (Exception e) {
                System.err.println("Error al reproducir sonido de casilla: " + e.getMessage());
            }
        }).start();
    }

    public static File obtenerSonidoVictoria() {
        File archivo = resolverArchivo("recursos/audio/Music_stinger_first_blood.mp3.ogg");
        if (archivo.exists()) return archivo;
        File archivoPorPatron = buscarArchivoPorPatron("recursos/audio", "first", "blood");
        return (archivoPorPatron != null && archivoPorPatron.exists()) ? archivoPorPatron : archivo;
    }

    public static File obtenerSonidoDerrota() {
        File archivo = resolverArchivo("recursos/audio/Music_stinger_player_death.mp3.ogg");
        if (archivo.exists()) return archivo;
        File archivoPorPatron = buscarArchivoPorPatron("recursos/audio", "player", "death");
        return (archivoPorPatron != null && archivoPorPatron.exists()) ? archivoPorPatron : archivo;
    }

    public static void reproducirSonidoVictoria() {
        reproducirEfectoCorto(obtenerSonidoVictoria(), 4500);
    }

    public static void reproducirSonidoDerrota() {
        reproducirEfectoCorto(obtenerSonidoDerrota(), 4500);
    }

    private static void reproducirEfectoCorto(File audio, int durationMs) {
        if (audio == null || !audio.exists()) return;
        new Thread(() -> {
            try {
                String script = "Add-Type -AssemblyName PresentationCore;" +
                        "Add-Type -AssemblyName WindowsBase;" +
                        "$p = New-Object System.Windows.Media.MediaPlayer;" +
                        "$p.Open([Uri]'" + audio.getAbsolutePath().replace("'", "''") + "');" +
                        "$p.Volume = 0.5;" +
                        "$p.Play();" +
                        "Start-Sleep -Milliseconds " + durationMs + ";" +
                        "$p.Stop();" +
                        "$p.Close()";
                new ProcessBuilder("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", script)
                        .start();
            } catch (Exception e) {
                System.err.println("Error al reproducir sonido: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Apaga y elimina el proceso de música inmediatamente usando taskkill /F /T
     */
    public static synchronized void detenerMusica() {
        tipoMusicaActual = "";
        if (procesoMusica != null) {
            try {
                long pid = procesoMusica.pid();
                if (pid > 0) {
                    try {
                        Process pKill = new ProcessBuilder("taskkill", "/F", "/T", "/PID", String.valueOf(pid)).start();
                        pKill.waitFor();
                    } catch (Exception e) {
                        System.err.println("Error al terminar proceso de música (PID " + pid + "): " + e.getMessage());
                    }
                }
                procesoMusica.descendants().forEach(ProcessHandle::destroyForcibly);
                procesoMusica.destroyForcibly();
            } catch (Exception e) {
                System.err.println("Error al detener música: " + e.getMessage());
            }
            procesoMusica = null;
        }
    }
}
