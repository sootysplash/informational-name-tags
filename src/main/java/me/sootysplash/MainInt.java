package me.sootysplash;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Arm;
import net.minecraft.util.Pair;
import net.minecraft.world.GameMode;

import java.util.*;

public class MainInt implements ClientModInitializer {
    public static List<String> options = new ArrayList<>(List.of("Distance", "Bps", "Ping", "Health", "DamageSource", "Air", "Gamemode", "Yaw", "Vehicle", "Armor", "Name", "UseTime", "HurtTime", "Pitch", "Arm", "Age"));

    public static MutableText colored(String str, String preset, LivingEntity ent) {
        int r = 0;
        int g = 0;
        int b = 0;
        int altRGB = 0;
        switch (preset) {
            case "health" -> {
                if (ent.getAbsorptionAmount() > 0f) {
                    r = 255;
                    g = 165;
                } else {
                    Pair<Integer, Integer> pr = hpRatio(ent);
                    r = pr.getRight();
                    g = pr.getLeft();
                }
            }
            case "hurtTime" -> {
                r = 255;
                Pair<Integer, Integer> pr = htRatio(ent);
                g = pr.getRight();
                b = pr.getLeft();
            }
            case "ping" -> {
                int pong = Integer.parseInt(str);
                // 0-149, green
                // 150-299, yellow
                // 300-599, orange
                // 600-999, light red
                // 1000+, red
                if (pong <= 70) {
                    g = 255;
                    b = 33;
                } else if (pong <= 110) {
                    r = 243;
                    g = 255;
                } else if (pong < 160) {
                    r = 255;
                    g = 165;
                } else if (pong < 220) {
                    r = 255;
                    g = 67;
                } else {
                    r = 145;
                    b = 3;
                }
                str = str.concat("ms");
            }
            case "gamemode" -> {
                //thanks gpt for the color ideas
                switch (str) {
                    case "C":
                        g = 255;
                        b = 255;
                        break;
                    case "S":
                        g = 255;
                        break;
                    case "A":
                        r = 255;
                        g = 215;
                        break;
                    case "SP":
                        r = 128;
                        b = 128;
                        break;
                }

                str = "GM: ".concat(str);
            }
        }
        TextColor txtcolor;
        if (altRGB != 0) {
            txtcolor = TextColor.fromRgb(altRGB);
        } else {
            int rgb = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
            txtcolor = TextColor.fromRgb(rgb);
        }
        Style style = Style.EMPTY;
        style = style.withColor(txtcolor);
        return Text.literal(str).fillStyle(style);
    }

    public static Pair<Integer, Integer> hpRatio(LivingEntity ent) {
        float divide = ent.getHealth() / ent.getMaxHealth();
        int red = Math.round(255 * divide);
        int green = 255 - Math.round(255 * divide);
        return new Pair<>(red, green);
    }

    public static Pair<Integer, Integer> htRatio(LivingEntity ent) {
        double divide = (double) ent.hurtTime / (double) ent.maxHurtTime;
        int blue = (int) (255 - Math.round(255 * divide));
        int green = (int) (255 - Math.round(255 * divide));
        return new Pair<>(blue, green);
    }

    public static Pair<String, String> pair(String left, String right) {
        return new Pair<>(left, right);
    }

    public static Text getText(Entity entity) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (entity instanceof PlayerEntity ent) {
            HashMap<String, String> fields = new HashMap<>();

            fields.put("ping", "0");
            fields.put("gamemode", "");


            try {
                fields.put("ping", String.valueOf(mc.getNetworkHandler().getPlayerListEntry((ent).getGameProfile().getName()).getLatency()));
            } catch (Exception ignored) {
            }
            GameMode gm = null;
            try {
                gm = mc.getNetworkHandler().getPlayerListEntry((ent).getGameProfile().getName()).getGameMode();
            } catch (Exception ignored) {
            }


            if (gm != null) {
                String gammode = "";
                switch (gm) {
                    case CREATIVE -> gammode = "C";
                    case SURVIVAL -> gammode = "S";
                    case ADVENTURE -> gammode = "A";
                    case SPECTATOR -> gammode = "SP";
                }
                fields.put("gamemode", gammode);
            }


            String space = " ";

            fields.put("name", entity.getDisplayName() != null ? entity.getDisplayName().getString() : "");

            fields.put("health", "");
            StringBuilder healthSB = new StringBuilder();
            healthSB.append((double) Math.round(ent.getHealth() * 100.0) / 100.0);
            if (ent.getHealth() != ent.getMaxHealth()) {
                healthSB.append('/');
                healthSB.append((double) Math.round(ent.getMaxHealth() * 100.0) / 100.0);
            }
            if (ent.getAbsorptionAmount() != 0) {
                healthSB.append('+');
                healthSB.append((double) Math.round(ent.getAbsorptionAmount() * 100.0) / 100.0);
            }
            healthSB.append(" HP");
            fields.put("health", healthSB.toString());

            fields.put("hurttime", "HT: ".concat(String.valueOf(ent.hurtTime)));
            fields.put("distance", mc.player != null ? "Distance: ".concat(String.valueOf(Math.round(Math.sqrt(mc.player.squaredDistanceTo(ent)) * 100.0) / 100.0)) : "");
            fields.put("pitch", "Pitch: ".concat(String.valueOf((double) Math.round(entity.getPitch() * 10.0) / 10.0)));
            fields.put("yaw", "Yaw: ".concat(String.valueOf((double) Math.round(entity.getYaw() * 10.0) / 10.0)));
            fields.put("age", "Age: ".concat(String.valueOf(ent.age)));
            fields.put("arm", ent.getMainArm() == Arm.RIGHT ? "Arm: R " : ent.getMainArm() == Arm.LEFT ? " Arm: L " : "");

            StringBuilder useTimeSB = new StringBuilder();
            useTimeSB.append("ItemTime: ");
            useTimeSB.append(ent.getItemUseTime());
            if (ent.getItemUseTime() != 0) {
                useTimeSB.append('/');
                useTimeSB.append(ent.getItemUseTimeLeft());
            }

            fields.put("usetime", useTimeSB.toString());
            fields.put("armor", ent.getArmor() != 0 ? "Armor: ".concat(String.valueOf(ent.getArmor())) : "");
            fields.put("damagesource", ent.getRecentDamageSource() != null ? "RecentDamage: ".concat(ent.getRecentDamageSource().getName()) : "");
            fields.put("vehicle", ent.getVehicle() != null ? "Vehicle: ".concat(ent.getVehicle().getName().getString()) : "");
            fields.put("bps", String.valueOf(Math.round((Math.hypot(ent.getPos().x - ent.prevX, ent.getPos().z - ent.prevZ) * 20) * 100.0) / 100.0).concat(" BPS"));

            StringBuilder airSB = new StringBuilder();

            if (ent.getAir() != ent.getMaxAir()) {
                airSB.append(ent.getAir());
                airSB.append('/');
                airSB.append(ent.getMaxAir());
                airSB.append(" Air");
            }
            fields.put("air", airSB.toString());

			/*ArrayList<String> entries = new ArrayList<>();
			fields.forEach((key, value) -> entries.add(key));
			System.out.println("\n" + entries + "\n");*/


            List<Pair<String, String>> userSelection = new ArrayList<>();

            ConfigINT config = ConfigINT.getInstance();

            for (String str : config.selection) {
                str = str.toLowerCase();
                userSelection.add(pair(fields.getOrDefault(str, ""), str));
            }

            TextBuilder tb = new TextBuilder();
            for (Pair<String, String> pair : userSelection) {

                if (!pair.getLeft().isEmpty()) {
                    switch (pair.getRight()) {
                        case "health":
                            tb.add(colored(pair.getLeft(), "health", ent));
                            break;
                        case "hurttime":
                            tb.add(colored(pair.getLeft(), "hurtTime", ent));
                            break;
                        case "ping":
                            tb.add(colored(pair.getLeft(), "ping", ent));
                            break;
                        case "gamemode":
                            tb.add(colored(pair.getLeft(), "gamemode", ent));
                            break;
						case "name":
							tb.add(ent.getDisplayName());
							break;
                        default:
                            tb.add(pair.getLeft());
                    }
                    tb.add(space);
                }
            }

            return tb.get();
        } else {
            return entity.getDisplayName();
        }
    }

    public static List<String> correctedArrayList(List<String> strings) {
        ArrayList<String> corrected = new ArrayList<>();

        for (String str : strings) {
            if (str.isEmpty()) {
                continue;
            }
            options.sort(Comparator.comparing(opt -> calculate(str, opt)));
            corrected.add(options.get(0));
        }

        return corrected;
    }

    // credit: https://www.baeldung.com/java-levenshtein-distance
    static int calculate(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

    @Override
    public void onInitializeClient() {

    }
}