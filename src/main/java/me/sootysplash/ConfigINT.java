package me.sootysplash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.ConfigData;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;

@me.shedaniel.autoconfig.annotation.Config(name = "PacketLogger")
public class ConfigINT implements ConfigData {

    //Andy is the goat https://github.com/AndyRusso/pvplegacyutils/blob/main/src/main/java/io/github/andyrusso/pvplegacyutils/PvPLegacyUtilsConfig.java

    private static final Path file = FabricLoader.getInstance().getConfigDir().resolve("PacketLogger.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ConfigINT instance;
    public List<String> selection = new ArrayList<>(List.of("Health", "Name", "HurtTime", "Ping"));

    public static ConfigINT getInstance() {
        if (instance == null) {
            try {
                instance = GSON.fromJson(Files.readString(file), ConfigINT.class);
            } catch (IOException exception) {
                LOGGER.warn("PacketLogger couldn't load the config, using defaults.");
                instance = new ConfigINT();
            }
        }

        return instance;
    }

    public void save() {
        try {
            Files.writeString(file, GSON.toJson(this));
        } catch (IOException e) {
            LOGGER.error("PacketLogger could not save the config.");
            throw new RuntimeException(e);
        }
    }

}
