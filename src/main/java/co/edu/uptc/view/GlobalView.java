package co.edu.uptc.view;

import java.awt.Color;
import java.awt.Font;

/**
 * Clase de utilidades global para definir colores de la aplicacion
 */
public class GlobalView {

    // =======================================================
    // ==               PALETA DE COLORES BASE              ==
    // =======================================================

    // Colores Primarios
    public static final Color PRIMARY_ACCENT_COLOR = new Color(223, 166, 31); // Amarillo/Naranja principal
    public static final Color DARK_PRIMARY_TEXT = new Color(21, 20, 20);      // Gris muy oscuro para texto y fondos
    public static final Color LIGHT_TEXT_COLOR = Color.WHITE;                // Blanco para texto sobre fondos oscuros

    // Colores de Fondo
    public static final Color GENERAL_BACKGROUND = new Color(240, 240, 240); // Fondo general de paneles principales (ligero gris)
    public static final Color GENERAL_BACKGROUND_LIGHT = new Color(247, 247, 247); // Fondo más claro para secciones dentro de paneles
    public static final Color GENERAL_BACKGROUND_DARK = new Color(44, 44, 44);   // Fondo oscuro para algunas secciones o diálogos
    public static final Color WELCOME_ROUND_BACKGROUND = new Color(44, 44, 44); // Coincide con GENERAL_BACKGROUND_DARK
    public static final Color CARDS_BACKGROUND = new Color(247, 247, 247);      // Coincide con GENERAL_BACKGROUND_LIGHT

    // Colores de Borde y Separación
    public static final Color BORDER_COLOR = new Color(200, 200, 200);   // Gris claro para bordes de campos y tablas
    public static final Color DIVIDER_COLOR = new Color(220, 220, 220); // Gris muy claro para separadores

    // Colores de Mensajes y Alertas
    public static final Color SUCCESS_COLOR = new Color(104, 196, 102); // Verde para éxito (Confirmar)
    public static final Color DANGER_COLOR = new Color(212, 66, 70);    // Rojo para peligro/error (Cancelar)
    public static final Color WARNING_COLOR = new Color(235, 135, 68);  // Naranja para advertencias
    public static final Color INFO_COLOR = new Color(68, 138, 255);     // Azul para información

    // =======================================================
    // ==           COLORES POR COMPONENTE/SECCIÓN          ==
    // =======================================================

    // Aside (Menú Lateral)
    public static final Color ASIDE_BACKGROUND = PRIMARY_ACCENT_COLOR;
    public static final Color ASIDE_BUTTONS_BACKGROUND = PRIMARY_ACCENT_COLOR;
    public static final Color ASIDE_BUTTONS_HOVER_COLOR = new Color(210, 153, 18);
    public static final Color ASIDE_BUTTONS_ACTIVE_BACKGROUND = DARK_PRIMARY_TEXT.brighter();

    // Header (Encabezado Superior)
    public static final Color HEADER_BACKGROUND = DARK_PRIMARY_TEXT;
    public static final Color HEADER_TEXT_COLOR = LIGHT_TEXT_COLOR;

    // Campos de Texto
    public static final Color TEXT_COLOR = DARK_PRIMARY_TEXT;
    public static final Color PLACEHOLDER_COLOR = Color.GRAY;
    public static final Color TEXT_FIELD_BACKGROUND = Color.WHITE;

    // Botones Generales
    public static final Color BUTTON_BACKGROUND_COLOR = Color.WHITE;
    public static final Color BUTTON_FOREGROUND_COLOR = DARK_PRIMARY_TEXT;
    public static final Color BUTTON_HOVER_COLOR = new Color(230, 230, 230); // Gris muy claro para hover

    // Botones Específicos
    public static final Color CONFIRM_BUTTON_BACKGROUND = SUCCESS_COLOR;
    public static final Color CANCEL_BUTTON_BACKGROUND = DANGER_COLOR;
    public static final Color CLOSE_BUTTON_BACKGROUND = new Color(237, 28, 36); // Rojo fuerte
    public static final Color CLOSE_BUTTON_HOVER_COLOR = new Color(200, 28, 36); // Rojo un poco más oscuro
    public static final Color PRODUCTS_OPTIONS_BACKGROUND = new Color(181, 177, 168); // Color de fondo para opciones de producto
    public static final Color PRODUCTS_OPTIONS_BUTTON_COLOR = DARK_PRIMARY_TEXT; // Color de texto/icono en opciones de producto

    // Diálogos y Pop-ups
    public static final Color DIALOG_BACKGROUND = new Color(216, 216, 216);
    public static final Color DIALOG_BORDER_COLOR = new Color(150, 150, 150);
    public static final Color WARNING_POPUP_BACKGROUND = WARNING_COLOR;

    // Tablas
    public static final Color TABLE_HEADER_BACKGROUND = new Color(230, 230, 230); // Gris claro
    public static final Color TABLE_HEADER_FOREGROUND = DARK_PRIMARY_TEXT;
    public static final Color TABLE_STRIPE_COLOR = new Color(242, 242, 242);     // Color para filas alternas (si se implementa)
    public static final Color TABLE_SELECTION_BACKGROUND = new Color(190, 220, 255); // Azul claro para selección
    public static final Color TABLE_SELECTION_FOREGROUND = DARK_PRIMARY_TEXT;


    // =======================================================
    // ==                  FUENTES GLOBALES                 ==
    // =======================================================
    public static final String FONT_FAMILY = "Segoe UI"; // O "Roboto", "Arial" etc.

    public static final Font TITLE_FONT = new Font(FONT_FAMILY, Font.PLAIN, 28);
    public static final Font SUBTITLE_FONT = new Font(FONT_FAMILY, Font.BOLD, 20);
    public static final Font HEADING_FONT = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font BODY_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font SMALL_TEXT_FONT = new Font(FONT_FAMILY, Font.PLAIN, 12);

    // Fuentes específicas de componentes
    public static final Font TEXT_FIELD_FONT = new Font(FONT_FAMILY, Font.PLAIN, 18);
    public static final Font BUTTON_FONT = new Font(FONT_FAMILY, Font.PLAIN, 16);
    public static final Font TABLE_HEADER_FONT = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font TABLE_BODY_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);

}