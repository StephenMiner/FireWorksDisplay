package me.stephenminer.fireworksdisplay.display;

import me.stephenminer.fireworksdisplay.FireWorksDisplay;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Display {
    private final FireWorksDisplay plugin;
    private String name;
    private Location launchFrom;
    private int amount;
    private FireworkMeta settings;
    private int delay;
    private boolean launching;


    public Display(FireWorksDisplay plugin, String name, Location launchFrom, FireworkMeta meta, int amount, int delay){
        this.plugin = plugin;
        this.settings = meta;
        this.launchFrom = launchFrom;
        this.delay = delay;
        this.amount = amount;
    }

    public void launch(){
        World world = launchFrom.getWorld();
        launching = true;
        new BukkitRunnable(){
            int launched = 0;
            int wait = 0;
            @Override
            public void run(){
                if (!launching){
                    this.cancel();
                    return;
                }
                if (wait >= delay) {
                    if (launched <= amount) {
                        Firework firework = (Firework) world.spawnEntity(launchFrom, EntityType.FIREWORK);
                        firework.setFireworkMeta(settings);
                        launched++;
                    }else{
                        this.cancel();
                        launching = false;
                    }
                    wait = 0;
                }
                wait++;
            }
        }.runTaskTimer(plugin, 5, 1);
    }

    public FireworkMeta getSettings(){
        return settings;
    }
    public void setSettings(FireworkMeta meta){
        this.settings = meta;
    }
    public int getDelay(){
        return delay;
    }
    public int getToLaunch(){
        return amount;
    }
    public boolean isLaunching(){
        return launching;
    }
    public void setLaunching(boolean launching){
        this.launching = launching;
    }
    public String getName(){
        return name;
    }
}
