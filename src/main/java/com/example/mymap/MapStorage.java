package com.example.mymap;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MapStorage {
    private static final Map<String, Integer> blockColors = new ConcurrentHashMap<>();
    private static File saveFile;
    private static boolean isDirty = false;

    public static void init(File runDir) {
        // Создаём директорию config если её нет
        File configDir = new File(runDir, "config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        saveFile = new File(configDir, "footprint_map_data.txt");
        load();
    }

    public static void saveBlockColor(int x, int z, int color) {
        String key = x + "," + z;
        if (!blockColors.containsKey(key)) {
            blockColors.put(key, color);
            isDirty = true;
        }
    }

    public static Integer getColor(int x, int z) {
        return blockColors.get(x + "," + z);
    }

    public static void saveAll() {
        if (!isDirty) return;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile))) {
            for (Map.Entry<String, Integer> entry : blockColors.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
            isDirty = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load() {
        if (!saveFile.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        blockColors.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                    } catch (NumberFormatException e) {
                        // Пропускаем некорректные строки
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void clearMap() {
        blockColors.clear();
        isDirty = true;
        saveAll();
    }
}
