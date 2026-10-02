# Tres en Raya - Edición Arcade (Deadlock Theme)

¡Bienvenido a **Tres en Raya - Edición Arcade**! Este  es un juego de Tres en Raya cualquiera; pero es una versión moderna,  y temática (inspirada en los personajes Archmother y Hidden King de Deadlock), que lleva el clásico juego de mesa a otro nivel con música, efectos visuales y mecánicas en tiempo real.

## 🎮 Modos de Juego

1. **Multijugador Local (1 VS 1)**: El clásico juego por turnos para jugar con un amigo en la misma pantalla.
2. **Contra la IA**: Enfréntate a la computadora en una partida por turnos.
3. **Smash Frenesí (Tiempo Real + Duelos)**: Un modo frenético sin turnos donde la máquina intenta ganar en tiempo real. Si intentas colocar una ficha en la misma casilla que la máquina, ¡se desata un minijuego de duelo de pulsaciones (machacar la tecla Espacio) para decidir quién se queda con la casilla!

## 🛠 Estructura del Proyecto

El código está modularizado para ser fácil de leer y mantener:

*   **`src/`** - Código fuente en Java:
    *   `TresEnRaya.java`: Punto de entrada principal.
    *   `MenuPrincipal.java`: Interfaz gráfica del menú de selección de modos.
    *   `TableroBase.java`: Lógica base del tablero, sistema de victorias y UI compartida.
    *   `Juego1vs1.java`, `JuegoContraIA.java`, `JuegoSmashFrenesi.java`: Implementaciones de cada modo de juego.
    *   `DueloMinijuego.java`: Lógica e interfaz del minijuego de "pulsadas" (Button mashing).
    *   `ReproductorMedia.java`: Gestor de contenido multimedia (música, efectos de sonido y video).
    *   `TemaVisual.java`: Paleta de colores, constantes gráficas y diseño visual.
*   **`recursos/`** - Assets del juego (imágenes, gifs, audios, música de fondo y fuentes personalizadas).
*   **`distribucion/`** - Contiene el ejecutable nativo (`.exe`) listo para jugar en Windows sin dependencias.

## 📦 Librerías y Tecnologías Usadas

Este proyecto está construido enteramente en **Java Puro (Vanilla)** sin librerías externas voluminosas. 

*   **Java Swing & AWT**: Utilizado para toda la Interfaz Gráfica de Usuario (GUI), renderizado de gráficos, ventanas modales y eventos de teclado/ratón.
*   **Windows PowerShell (WPF)**: La clase `ReproductorMedia` utiliza procesos en segundo plano de PowerShell con `System.Windows.Media.MediaPlayer` para reproducir audios (`.mp3`, `.ogg`) y videos `.mp4` de forma asíncrona sin bloquear el hilo principal de Java.
*   **JDK jpackage**: Utilizado para empaquetar el juego en un ejecutable nativo de Windows (`.exe`) independiente.

## 🚀 Cómo Jugar

1. Descarga el archivo **`TresEnRaya_Windows.zip`** que se encuentra en este repositorio.
2. Extrae (descomprime) el archivo `.zip` en cualquier carpeta de tu computadora.
3. Entra en la carpeta extraída y dale doble clic a **`TresEnRaya.exe`** (el archivo con el ícono del juego).
4. ¡A jugar! No necesitas instalar Java ni configurar nada extra; el juego incluye su propio motor interno.

## 👨‍💻 Autor
Diego A. Ontiveros Hernandez
