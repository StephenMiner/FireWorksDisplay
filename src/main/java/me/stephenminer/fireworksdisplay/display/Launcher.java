package me.stephenminer.fireworksdisplay.display;

import me.stephenminer.fireworksdisplay.FireWorksDisplay;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Launcher {
    private final FireWorksDisplay plugin;
    private List<Display> displays;
    private Location trigger;
    private String name;
    private ItemStack remote;
    private int interval;
    private boolean activated;

    public Launcher(FireWorksDisplay plugin, String name){
        this.plugin = plugin;
        this.name = name;
        displays = new ArrayList<>();
        remote = createRemote();
        plugin.launchers.add(this);
    }
    public Launcher(FireWorksDisplay plugin, String name, int interval){
        this.plugin = plugin;
        this.name = name;
        displays = new ArrayList<>();
        remote = createRemote();
        this.interval = interval;
        plugin.launchers.add(this);
    }


    public void addDisplay(Display display){
        displays.add(display);
    }

    public void setTrigger(Location trigger){
        this.trigger = trigger;
    }

    public void activate(){
        for (Display display : displays){
            display.launch();
        }
        new BukkitRunnable(){
            @Override
            public void run(){
                int done = 0;
                if (!activated) this.cancel();
                for (Display display : displays){
                    if (!display.isLaunching()) done++;
                }
                if (done >= displays.size()){
                    this.cancel();
                    activated = false;
                    return;
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }
    public void activate(boolean repeat){
        if (interval < 1) activate();
        activated = true;
        new BukkitRunnable(){
            int count = interval;
            @Override
            public void run(){
                if (!activated){
                    this.cancel();
                    return;
                }
                if (count >= interval){
                    for (Display display : displays){
                        display.launch();
                    }
                    count = 0;
                }
                int finished = 0;
                for (Display display : displays){
                    if (!display.isLaunching()) finished++;
                }
                if (finished == displays.size()){
                    count++;
                }
            }
        }.runTaskTimer(plugin, 5, 1);
    }

    public void kill(){
        activated = false;
        for (Display display : displays){
            display.setLaunching(false);
        }
    }

    public String getName(){ return name; }
    public Location getTrigger(){ return trigger; }
    public boolean isActivated(){ return activated; }
    public boolean repeating(){ return interval >= 1; }

    public ItemStack createRemote(){
        ItemStack remote = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = remote.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Launcher Remote");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Go off with a bang");
        lore.add(ChatColor.YELLOW + "Right click to activate fireworks display");
        lore.add(ChatColor.YELLOW + "Left click to disable it");
        lore.add(ChatColor.BLACK + "firework remote");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 15, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        remote.setItemMeta(meta);
        return remote;
    }


}
