package me.stephenminer.fireworksdisplay;

import me.stephenminer.fireworksdisplay.commands.DisplayCmds;
import me.stephenminer.fireworksdisplay.commands.LauncherCmds;
import me.stephenminer.fireworksdisplay.display.Launcher;
import me.stephenminer.fireworksdisplay.display.LauncherBuilder;
import me.stephenminer.fireworksdisplay.events.SetUpEvents;
import org.bukkit.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public final class FireWorksDisplay extends JavaPlugin {
    public List<Launcher> launchers;
    public ConfigFile displayFile;
    public ConfigFile launcherFile;
    @Override
    public void onEnable() {
        displayFile = new ConfigFile(this, "displays");
        launcherFile = new ConfigFile(this, "launchers");
        launchers = new ArrayList<>();
        registerCommands();
        registerEvents();
        loadLaunchers();
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        for (Launcher launcher : launchers){
            launcher.kill();
        }
        launchers.clear();
        displayFile.saveConfig();
        launcherFile.saveConfig();
        // Plugin shutdown logic
    }

    private void registerCommands(){
        LauncherCmds launcherCmds = new LauncherCmds(this);
        DisplayCmds displayCmds = new DisplayCmds(this);
        getCommand("launcher").setExecutor(launcherCmds);
        getCommand("launcher").setTabCompleter(launcherCmds);
        getCommand("display").setExecutor(displayCmds);
        getCommand("display").setTabCompleter(displayCmds);
    }

    private void registerEvents(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SetUpEvents(this), this);
    }

    private void loadLaunchers(){
        if (launcherFile.getConfig().contains("launchers")){
            Set<String> launcherIds = launcherFile.getConfig().getConfigurationSection("launchers").getKeys(false);
            for (String launcherId : launcherIds){
                try {
                    LauncherBuilder builder = new LauncherBuilder(this, launcherId);
                    builder.buildLauncher();
                }catch (Exception e){
                    getLogger().log(Level.WARNING, "Something went wrong loading launcher " + launcherId);
                }
            }
        }
    }

    public String fromBlockLoc(Location loc){
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return world.getName() + "," + x + "," + y + "," + z;
    }

    public Location fromString(String str){
        String[] contents = str.split(",");
        World world = null;
        try {
            world = Bukkit.getWorld(contents[0]);
        }catch (Exception ignored){}
        if (world == null){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Force Loading world " + contents[0]);
            world = Bukkit.createWorld(new WorldCreator(contents[0]));
        }
        int x = Integer.parseInt(contents[1]);
        int y = Integer.parseInt(contents[2]);
        int z = Integer.parseInt(contents[3]);
        return new Location(world, x, y, z);
    }


}
