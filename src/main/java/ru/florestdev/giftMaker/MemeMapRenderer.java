package ru.florestdev.giftMaker;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class MemeMapRenderer extends MapRenderer {

    private final BufferedImage image;
    private boolean rendered = false;

    public MemeMapRenderer(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if (rendered) return;

        // 1. Масштабируем картинку до 128x128
        Image scaledImage = image.getScaledInstance(128, 128, Image.SCALE_SMOOTH);

        // 2. Конвертируем Image в BufferedImage, чтобы работать с пикселями
        BufferedImage bufferedScaledImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        bufferedScaledImage.getGraphics().drawImage(scaledImage, 0, 0, null);

        // 3. ВАЖНО: Преобразуем цвета в палитру Minecraft
        BufferedImage minecraftReadyImage = MapPalette.resizeImage(bufferedScaledImage);

        // 4. Отрисовываем
        canvas.drawImage(0, 0, minecraftReadyImage);

        rendered = true;
    }
}