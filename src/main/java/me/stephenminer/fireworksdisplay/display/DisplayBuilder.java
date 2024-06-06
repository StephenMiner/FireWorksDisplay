package me.stephenminer.fireworksdisplay.display;

import me.stephenminer.fireworksdisplay.FireWorksDisplay;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;
import java.util.Set;

public class DisplayBuilder {
    private final FireWorksDisplay plugin;
    private final String name;

    private Location launchFrom;
    private FireworkMeta meta;
    private int launchAmount;
    private int launchDelay;

    public DisplayBuilder(FireWorksDisplay plugin, String name){
        this.plugin = plugin;
        this.name = name;
        buildIntegerVals();
        buildLocation();
        buildMeta();
    }




    public void buildMeta(){
        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) item.getItemMeta();
        Set<String> types = plugin.displayFile.getConfig().getConfigurationSection("displays." + name + ".effects").getKeys(false);
        for (String effect : types){
            try{
                FireworkEffect.Type type = FireworkEffect.Type.valueOf(effect);
                meta.addEffect(buildEffect(type));
            }catch (Exception e){
                Bukkit.broadcastMessage("booo");
            }
        }
        int power = plugin.displayFile.getConfig().getInt("displays." + name + ".power");
        if (power > 0) meta.setPower(power);
        this.meta = meta;
    }

    public void buildIntegerVals(){
        int launchAmount = plugin.displayFile.getConfig().getInt("displays." + name + ".launch-amount");
        int launchDelay = plugin.displayFile.getConfig().getInt("displays." + name + ".launch-delay");
        this.launchAmount = launchAmount;
        this.launchDelay = launchDelay;
    }

    public void buildLocation(){
        Location launchFrom = plugin.fromString(plugin.displayFile.getConfig().getString("displays." + name + ".launch-from"));
        this.launchFrom = launchFrom;
    }

    public FireworkEffect buildEffect(FireworkEffect.Type type){
        String path = "displays." + name + ".effects." + type.name();
        FireworkEffect.Builder effectBuilder = FireworkEffect.builder();
        effectBuilder.with(type);
        List<String> colors = plugin.displayFile.getConfig().getStringList(path + ".colors");
        for (String color : colors){
            effectBuilder.withColor(fromString(color));
        }
        List<String> fadeColors = plugin.displayFile.getConfig().getStringList(path + "fade-colors");
        for (String color : fadeColors){
            effectBuilder.withFade(fromString(color));
        }
        boolean trail = plugin.displayFile.getConfig().getBoolean(path + ".trail");
        boolean flicker = plugin.displayFile.getConfig().getBoolean(path + ".flicker");
        effectBuilder.flicker(flicker);
        effectBuilder.trail(trail);
        return effectBuilder.build();
    }


    private Color fromString(String str){
        String[] unwrap = str.split(",");
        int r = Integer.parseInt(unwrap[0]);
        int g = Integer.parseInt(unwrap[1]);
        int b = Integer.parseInt(unwrap[2]);
        return Color.fromRGB(r,g,b);
    }

    public Display buildDisplay(){
        Display display = new Display(plugin, name, launchFrom, meta, launchAmount, launchDelay);
        return display;
    }

}
