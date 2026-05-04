package ru.florestdev.giftMaker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public final class GiftMaker extends JavaPlugin implements CommandExecutor {
    MemeGenerator generator = null;
    Random random = null;
    @Override
    public void onEnable() {
        getLogger().info("GIFTMAKER IS STARTED!");
        generator = new MemeGenerator();
        random = new Random();

        // Таймер: каждые 1200 секунд (20 минут)
        // 20 тиков * 60 секунд * 20 минут = 24000 тиков
        new BukkitRunnable() {
            @Override
            public void run() {
                Collection<? extends Player> playersCollection = getServer().getOnlinePlayers();

                // ИСПРАВЛЕНО: Правильная проверка пустого списка
                if (playersCollection.isEmpty()) return;

                List<Player> playersList = new ArrayList<>(playersCollection);

                // ИСПРАВЛЕНО: Используем класс-переменную random
                Player player = playersList.get(random.nextInt(playersList.size()));

                giveMemeGift(player, "мемы");
            }
        }.runTaskTimerAsynchronously(this, 100L, 20L * 60 * 20); // Задержка 5 сек, далее каждые 20 мин
    }

    public void giveMemeGift(Player player, String query) {
        CompletableFuture.supplyAsync(() -> {
            // generator.getPhotoMemes теперь возвращает List
            try {
                return generator.getPhotoMemes(query);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(images -> {
            // ИСПРАВЛЕНО: Используем images (List)
            if (images != null && !images.isEmpty()) {
                BufferedImage randomImage = images.get(random.nextInt(images.size()));

                Bukkit.getScheduler().runTask(this, () -> {
                    ItemStack mapItem = createMemeMapItem(randomImage, player);
                    player.getInventory().addItem(mapItem);
                    player.sendMessage("§d[🎁] §fВам выпал мемный подарок!");
                });
            }
        });
    }

    // Этот метод ВАЖНО вызывать только в главном потоке
    public ItemStack createMemeMapItem(BufferedImage image, Player player) {
        MapView view = Bukkit.createMap(player.getWorld());
        for (MapRenderer renderer : view.getRenderers()) {
            view.removeRenderer(renderer);
        }
        view.addRenderer(new MemeMapRenderer(image));

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();
        meta.setMapView(view);
        meta.setDisplayName("§dСлучайный мем!");
        mapItem.setItemMeta(meta);

        return mapItem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Твоя Senior-логика здесь
        if (sender.hasPermission("gifts.admin")) {
            sender.sendMessage("Выдали подарок случайному игроку!");
            new BukkitRunnable() {
                @Override
                public void run() {
                    Collection<? extends Player> playersCollection = getServer().getOnlinePlayers();

                    // ИСПРАВЛЕНО: Правильная проверка пустого списка
                    if (playersCollection.isEmpty()) return;

                    List<Player> playersList = new ArrayList<>(playersCollection);

                    // ИСПРАВЛЕНО: Используем класс-переменную random
                    Player player = playersList.get(random.nextInt(playersList.size()));

                    giveMemeGift(player, "мемы");
                }
            }.runTaskAsynchronously(this); // Задержка 5 сек, далее каждые 20 мин
        }
        return true;
    }

    @Override
    public void onDisable() {
        getLogger().info("GIFTMAKER IS DISABLED!");
    }
}
