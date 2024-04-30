package me.sootysplash;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModMenuINT implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigINT config = ConfigINT.getInstance();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Config"))
                    .setSavingRunnable(config::save);

            ConfigCategory nameTagStuff = builder.getOrCreateCategory(Text.of("NameTag Stuff"));
            ConfigEntryBuilder build = builder.entryBuilder();

            nameTagStuff.addEntry(build.startStrList(Text.of("User Selection"), config.selection)
                    .setDefaultValue(new ArrayList<>(List.of("Health", "Name", "HurtTime", "Ping")))
                    .setSaveConsumer(newValue -> config.selection = MainInt.correctedArrayList(newValue))
                    .setExpanded(true)
                    .setTooltip(Text.of("What info to display on entities\nFields are displayed from left to right on the nametag"))
                    .build());

            nameTagStuff.addEntry(build.startStringDropdownMenu(Text.of("Available Information"), "Name")
                    .setSuggestionMode(false)
                    .setSelections(MainInt.options)
                    .setTooltip(Text.of("Distance: The distance from You to the entity\nBps: The speed of the entity in blocks per second\nPing: The player's latency to the server\nHealth: How much health + absorption the entity has\nDamageSource: The entity's recent source of damage\nAir: The amount of time the entity can remain underwater for\nGamemode: The player's current gamemode\nYaw: The entity's horizontal rotation\nVehicle: What the entity is currently riding\nArmor: How much armor the entity has\nName: The entity's display name\nUseTime: How long an entity has been using an item\\time left\nHurtTime: The countdown until the entity can take damage again\nPitch: The entity's vertical rotation\nArm: The player's current main hand\nAge: How long the entity has existed"))
                    .build());


            return builder.build();
        };
    }

}
