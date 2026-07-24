package com.example.mymap;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
public class MapStorage {
    private static final Map<String, Integer> blockColors = new HashMap<>();
    private static File saveFile;
    public static void init(File runDir) {
        saveFile = new File(runDir, "config/footprint_map_data.txt");
        load();
    }
    public static void saveBlockColor(int x, int z, int color) {
        String key = x + "," + z;
        if (!blockColors.containsKey(key)) {
            blockColors.put(key, color);
            saveSingleLine(key, color);
        }
    }
    public static Integer getColor(int x, int z) { return blockColors.get(x + "," + z); }
    private static void saveSingleLine(String key, int color) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile, true))) {
            writer.println(key + ":" + color);
        } catch (IOException e) { e.printStackTrace(); }
    }
    private static void load() {
        if (!saveFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    blockColors.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
