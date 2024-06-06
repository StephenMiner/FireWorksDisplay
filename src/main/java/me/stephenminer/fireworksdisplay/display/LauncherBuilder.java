package me.stephenminer.fireworksdisplay.display;

import me.stephenminer.fireworksdisplay.FireWorksDisplay;
import org.bukkit.Location;

import java.util.List;
import java.util.ArrayList;

public class LauncherBuilder {
    private final FireWorksDisplay plugin;
    private final String launcher;

    private List<Display> displays;
    private Location trigger;
    private int interval;

    public LauncherBuilder (FireWorksDisplay plugin, String launcher){
        this.plugin = plugin;
        this.launcher = launcher;
        loadDisplays();
        loadTrigger();
        loadInterval();
    }


    public void loadDisplays(){
        displays = new ArrayList<>();
        List<String> displayIds = plugin.launcherFile.getConfig().getStringList("launchers." + launcher + ".displays");
        for (String entry : displayIds){
            DisplayBuilder builder = new DisplayBuilder(plugin, entry);
            displays.add(builder.buildDisplay());

        }
    }

    public void loadTrigger(){
        this.trigger = plugin.fromString(plugin.launcherFile.getConfig().getString("launchers." + launcher + ".trigger"));
    }
    public void loadInterval(){
        this.interval = plugin.launcherFile.getConfig().getInt("launchers." + launcher + ".interval");
    }

    public Launcher buildLauncher(){
        Launcher launcher = new Launcher(plugin, this.launcher, interval);
        for (Display display : displays){
            launcher.addDisplay(display);
        }
        displays.clear();
        launcher.setTrigger(trigger);
        return launcher;
    }
}
