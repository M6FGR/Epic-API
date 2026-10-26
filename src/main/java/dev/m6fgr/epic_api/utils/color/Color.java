package dev.m6fgr.epic_api.utils.color;

public enum Color {
    BLACK(0x000000, 255),
    DARK_BLUE(0x0000AA, 255),
    DARK_GREEN(0x00AA00, 255),
    DARK_AQUA(0x00AAAA, 255),
    DARK_RED(0xAA0000, 255),
    DARK_PURPLE(0xAA00AA, 255),
    GOLD(0xFFAA00, 255),
    GRAY(0xAAAAAA, 255),
    DARK_GRAY(0x555555, 255),
    BLUE(0x5555FF, 255),
    GREEN(0x55FF55, 255),
    AQUA(0x55FFFF, 255),
    RED(0xFF5555, 255),
    LIGHT_PURPLE(0xFF55FF, 255),
    YELLOW(0xFFFF55, 255),
    WHITE(0xFFFFFF, 255),

    ORANGE(0xFF8000, 255),
    CYAN(0x00FFFF, 255),
    MAGENTA(0xFF00FF, 255),
    LIME(0x80FF00, 255),
    PINK(0xFFC0CB, 255),
    PURPLE(0x800080, 255),
    BROWN(0x8B4513, 255),
    CORAL(0xFF7F50, 255),
    GOLDENROD(0xDAA520, 255),
    TEAL(0x008080, 255),
    NAVY(0x000080, 255),
    MAROON(0x800000, 255),
    OLIVE(0x808000, 255),

    TRANSPARENT(0x000000, 0),
    SEMI_TRANSPARENT_BLACK(0x000000, 128),
    SEMI_TRANSPARENT_WHITE(0xFFFFFF, 128);

    private final int r;
    private final int g;
    private final int b;
    private final int a;

    private final int rgb;
    private final int rgba;

    Color(int rgb, int alpha) {
        this.r = (rgb >> 16) & 0xFF;
        this.g = (rgb >> 8) & 0xFF;
        this.b = rgb & 0xFF;
        this.a = alpha & 0xFF;

        this.rgb = rgb;
        this.rgba = (this.r << 24) | (this.g << 16) | (this.b << 8) | this.a;
    }

    Color(int r, int g, int b, int a) {
        this.r = r & 0xFF;
        this.g = g & 0xFF;
        this.b = b & 0xFF;
        this.a = a & 0xFF;

        this.rgb = (this.r << 16) | (this.g << 8) | this.b;
        this.rgba = (this.r << 24) | (this.g << 16) | (this.b << 8) | this.a;
    }

    public int getRed() { return r; }
    public int getGreen() { return g; }
    public int getBlue() { return b; }
    public int getAlpha() { return a; }

    public float getRedFloat() { return r / 255.0F; }
    public float getGreenFloat() { return g / 255.0F; }
    public float getBlueFloat() { return b / 255.0F; }
    public float getAlphaFloat() { return a / 255.0F; }

    public int getRGB() { return rgb; }
    public int getRGBA() { return rgba; }

    public int getARGB() {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public int withAlpha(int customAlpha) {
        return (r << 24) | (g << 16) | (b << 8) | (customAlpha & 0xFF);
    }

    public int withAlphaARGB(int customAlpha) {
        return ((customAlpha & 0xFF) << 24) | (r << 16) | (g << 8) | b;
    }
}