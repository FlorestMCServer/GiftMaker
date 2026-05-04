package ru.florestdev.giftMaker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

public class MemeGenerator {
    private final Random random = new Random();

    public List<BufferedImage> getPhotoMemes(String query) throws IOException {
        // Imgflip API не требует API-ключа для метода get_memes
        String apiUrl = "https://api.imgflip.com/get_memes";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        if (connection.getResponseCode() != 200) {
            return null;
        }

        // Читаем JSON ответ
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        // Парсим JSON (как в твоем Python примере)
        JSONObject jsonResponse = new JSONObject(content.toString());
        if (!jsonResponse.getBoolean("success")) {
            return null;
        }

        JSONArray memes = jsonResponse.getJSONObject("data").getJSONArray("memes");
        List<BufferedImage> images_ = new ArrayList<>();

        // Берем случайный мем из списка (или можно взять несколько)
        // В твоем боте был random.choice, сделаем так же
        if (memes.length() > 0) {
            // Выбираем один случайный мем
            JSONObject meme = memes.getJSONObject(random.nextInt(memes.length()));
            String imageUrl = meme.getString("url");

            // Загружаем картинку по ссылке
            URL imgUrl = new URL(imageUrl);
            BufferedImage image = ImageIO.read(imgUrl);

            if (image != null) {
                images_.add(image);
            }
        }

        return images_;
    }
}