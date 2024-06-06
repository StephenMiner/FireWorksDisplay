package me.stephenminer.fireworksdisplay.commands;

import me.stephenminer.fireworksdisplay.FireWorksDisplay;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class DisplayCmds implements CommandExecutor, TabCompleter {
    private final FireWorksDisplay plugin;

    public DisplayCmds(FireWorksDisplay plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("display")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("fd.commands.display")){
                    player.sendMessage(ChatColor.RED + "Not today thankyou! (No perms???)");
                    return false;
                }
                int size = args.length;
                if (size >= 2){
                    String sub = args[0];
                    String displayName = args[1];
                    if (sub.equalsIgnoreCase("launchFrom")){
                        if (!displayExists(displayName)){
                            player.sendMessage(ChatColor.RED + "Inputted display " + displayName + " doesn't exist!");
                            return false;
                        }
                        setLaunchFrom(displayName, player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Set launch from location for display " + displayName);
                        return true;
                    }
                    if (sub.equalsIgnoreCase("create")) {
                        if (displayExists(displayName)){
                            player.sendMessage(ChatColor.RED + "Display already exists!");
                            return false;
                        }
                        plugin.displayFile.getConfig().createSection("displays." + displayName);
                        plugin.displayFile.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Created display " + displayName + "!");
                        player.sendMessage(ChatColor.GREEN + "Check out the other sub cmds to see what you can do!");
                        return true;
                    }
                    if (size >= 3){
                        if (sub.equalsIgnoreCase("setLaunchPower")){
                            try{
                                int power = Integer.parseInt(args[2]);
                                setLaunchPower(displayName, power);
                                player.sendMessage(ChatColor.GREEN + "Set launch power to " + power);
                                return true;
                            }catch (Exception ignored){}
                            player.sendMessage(ChatColor.RED + args[2] + " isn't a real number!");
                            return false;
                        }
                        if (sub.equalsIgnoreCase("launchAmount")){
                            try{
                                int launch = Integer.parseInt(args[2]);
                                setLaunchAmount(displayName, launch);
                                player.sendMessage(ChatColor.GREEN + "Display will now launch " + launch + " rockets!");
                                return true;
                            }catch (Exception e){
                                player.sendMessage(ChatColor.RED + args[2] + " isn't a real number!");
                            }
                            return false;
                        }
                        if (sub.equalsIgnoreCase("launchDelay")){
                            try{
                                int delay = Integer.parseInt(args[2]);
                                setLaunchDelay(displayName, delay);
                                player.sendMessage(ChatColor.GREEN + "Display will now launch rockets ever " + delay + " ticks!");
                                return true;
                            }catch (Exception e){
                                player.sendMessage(ChatColor.RED + args[2] + " isn't a real number!");
                            }
                            return false;
                        }

                        String effect = args[2];
                        FireworkEffect.Type type = null;
                        try{
                            type = FireworkEffect.Type.valueOf(effect);
                        }catch (Exception ignored){}
                        if (type == null){
                            player.sendMessage(ChatColor.RED + "Inputted firework type doesn't exist!");
                            return false;
                        }
                        if (!displayExists(displayName)){
                            player.sendMessage(ChatColor.RED + "Fire work display " + args[1] + " doesn't exist!");
                            return false;
                        }

                        if (sub.equalsIgnoreCase("addEffect")){
                            if (hasEffect(displayName, type)){
                                player.sendMessage(ChatColor.RED + "This display already has this effect!");
                                return false;
                            }
                            addEffect(displayName, type);
                            player.sendMessage(ChatColor.GREEN + "Added effect to display " + displayName);
                            return true;
                        }
                        if (sub.equalsIgnoreCase("removeEffect")){
                            if (!hasEffect(displayName, type)){
                                player.sendMessage(ChatColor.RED + "This display already doesn't have this effect!");
                                return false;
                            }
                            removeEffect(displayName, type);
                            player.sendMessage(ChatColor.GREEN + "Removed effect " + type.name() + " in display " + displayName);
                            return true;
                        }

                        if (size >= 4){
                            if (!hasEffect(displayName, type)) {
                                player.sendMessage(ChatColor.RED + "This display does not have the inputted effect: " + type.name());
                                return false;
                            }

                            if (sub.equalsIgnoreCase("addColor")){
                                if (hasColor(displayName, type, args[3])){
                                    sender.sendMessage(ChatColor.RED + "Effect already has this color!");
                                    return false;
                                }
                                String[] split = args[3].split(",");
                                int r = Integer.parseInt(split[0]);
                                int g = Integer.parseInt(split[1]);
                                int b = Integer.parseInt(split[2]);
                                Color color = Color.fromRGB(r, g, b);
                                addColor(displayName, type, color);
                                player.sendMessage(ChatColor.GREEN + "Added color " + args[3] + " to effect " + type.name());
                                return true;
                            }
                            if (sub.equalsIgnoreCase("removeColor")){
                                if (!hasColor(displayName, type, args[3])){
                                    sender.sendMessage(ChatColor.RED + "This effect already doesn't have this color!");
                                    return false;
                                }
                                removeColor(displayName, type, args[3]);
                                player.sendMessage(ChatColor.GREEN + "Removed color " + args[3] + " from effect " + type.name() + " in display " + displayName);
                            }
                            if (sub.equalsIgnoreCase("addFadeColor")){
                                if (hasFadeColor(displayName, type, args[3])){
                                    sender.sendMessage(ChatColor.RED + "Effect already has this color!");
                                    return false;
                                }
                                String[] split = args[3].split(",");
                                int r = Integer.parseInt(split[0]);
                                int g = Integer.parseInt(split[1]);
                                int b = Integer.parseInt(split[2]);
                                Color color = Color.fromRGB(r, g, b);
                                addFadeColor(displayName, type, color);
                                player.sendMessage(ChatColor.GREEN + "Added fade color " + args[3] + " to effect " + type.name());
                                return true;
                            }

                            if (sub.equalsIgnoreCase("withTrail")){
                                boolean on = Boolean.parseBoolean(args[3]);
                                addTrail(displayName, type, on);
                                player.sendMessage(ChatColor.GREEN + "set withTrail for " + type.name() + " to " + on);
                                return true;
                            }
                            if (sub.equalsIgnoreCase("withFlicker")){
                                boolean on = Boolean.parseBoolean(args[3]);
                                addFlicker(displayName, type, on);
                                player.sendMessage(ChatColor.GREEN + "set withFlicker for " + type.name() + " to " + on);
                                return true;
                            }
                        }
                    }
                }
            }else sender.sendMessage(ChatColor.RED + "For my sanity, only players can use this command!");
        }
        return false;
    }

    private boolean displayExists(String display){
        return plugin.displayFile.getConfig().contains("displays." + display);
    }
    private boolean hasEffect(String display, FireworkEffect.Type effect){
        return plugin.displayFile.getConfig().contains("displays." + display + ".effects." + effect.name());
    }
    private boolean hasColor(String display, FireworkEffect.Type type, String colorString){
        String path = "displays." + display + ".effects." + type.name();
        List<String> colors = plugin.displayFile.getConfig().getStringList(path + ".colors");
        return colors.contains(colorString);

    }
    private boolean hasFadeColor(String display, FireworkEffect.Type type, String colorString){
        String path = "displays." + display + ".effects." + type.name();
        if (plugin.displayFile.getConfig().contains(path + ".fade-colors")){
            List<String> colors = plugin.displayFile.getConfig().getStringList(path + ".fade-colors");
            return colors.contains(colorString);
        }
        return false;
    }

    private void addEffect(String name, FireworkEffect.Type type){
        plugin.displayFile.getConfig().createSection("displays." + name + ".effects." + type.name());
        plugin.displayFile.saveConfig();
    }
    private void addColor(String name, FireworkEffect.Type type, Color color){
        List<String> colors = plugin.displayFile.getConfig().getStringList("displays." + name + ".effects." + type.name() + ".colors");
        colors.add(color.getRed() + "," + color.getGreen() + "," + color.getBlue());
        plugin.displayFile.getConfig().set("displays." + name + ".effects." + type.name() + ".colors", colors);
        plugin.displayFile.saveConfig();
    }
    private void addFadeColor(String name, FireworkEffect.Type type, Color color){
        List<String> colors = plugin.displayFile.getConfig().getStringList("displays." + name + ".effects." + type.name() + ".fade-colors");
        colors.add(color.getRed() + "," + color.getGreen() + "," + color.getBlue());
        plugin.displayFile.getConfig().set("displays." + name + ".effects." + type.name() + ".fade-colors", colors);
        plugin.displayFile.saveConfig();
    }
    private void addTrail(String name, FireworkEffect.Type type, boolean trail){
        plugin.displayFile.getConfig().set("displays." + name + ".effects." + type.name() + ".trail", trail);
        plugin.displayFile.saveConfig();
    }
    private void addFlicker(String name, FireworkEffect.Type type, boolean flicker){
        plugin.displayFile.getConfig().set("displays." + name + ".effects." + type.name() + ".flicker", flicker);
        plugin.displayFile.saveConfig();
    }

    private void setLaunchAmount(String name, int amount){
        plugin.displayFile.getConfig().set("displays." + name + ".launch-amount", amount);
        plugin.displayFile.saveConfig();
    }

    private void setLaunchDelay(String name, int delay){
        plugin.displayFile.getConfig().set("displays." + name + ".launch-delay", delay);
        plugin.displayFile.saveConfig();
    }
    private void setLaunchPower(String name, int power){
        plugin.displayFile.getConfig().set("displays." + name + ".power", power);
        plugin.displayFile.saveConfig();
    }

    private void setLaunchFrom(String name, Location loc){
        plugin.displayFile.getConfig().set("displays." + name + ".launch-from", plugin.fromBlockLoc(loc));
        plugin.displayFile.saveConfig();
    }

    private void removeColor(String name, FireworkEffect.Type type, String stringColor){
        String path = "displays." + name + ".effects." + type.name() + ".colors";
        List<String> colors = plugin.displayFile.getConfig().getStringList(path);
        colors.remove(stringColor);
        plugin.displayFile.getConfig().set(path, colors);
        plugin.displayFile.saveConfig();
    }
    private void removeEffect(String name, FireworkEffect.Type type){
        plugin.displayFile.getConfig().set("displays." + name + ".effects." + type.name(), null);
        plugin.displayFile.saveDefaultConfig();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("display")){
            int size = args.length;
            if (size == 1) return subCmds(args[0]);
            if (size == 2 && !args[0].equalsIgnoreCase("create")) return displays(args[1]);
            String sub = args[0];
            if (size == 3){
                if (sub.equalsIgnoreCase("addEffect")) return effects(args[2]);
                if (sub.equalsIgnoreCase("launchFrom")) return launchAmount();
                if (sub.equalsIgnoreCase("launchDelay")) return delay();
                if (sub.equalsIgnoreCase("launchAmount")) return launchAmount();
                if (sub.equalsIgnoreCase("setLaunchPower")) return powers();
                return effectsInDisplay(args[1], args[2]);
            }
            if (size == 4){
                if (sub.equalsIgnoreCase("removeColor")) return colorsInDisplay(args[2], args[1], args[3]);
                if (sub.equalsIgnoreCase("addColor")) return colors();
                if (sub.equalsIgnoreCase("addFadeColor")) return colors();
                if (sub.equalsIgnoreCase("withTrail")) return booleans(args[3]);
                if (sub.equalsIgnoreCase("withFlicker")) return booleans(args[3]);
            }
        }
        return null;
    }

    private List<String> filter(Collection<String> base, String match){
        List<String> filtered = new ArrayList<>();
        match = match.toLowerCase(Locale.ROOT);
        for (String entry : base){
            String temp = entry.toLowerCase(Locale.ROOT);
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }

    private List<String> subCmds(String match){
        List<String> subs = new ArrayList<>();
        subs.add("create");
        subs.add("launchFrom");
        subs.add("setLaunchPower");
        subs.add("launchAmount");
        subs.add("launchDelay");
        subs.add("addEffect");
        subs.add("addColor");
        subs.add("addFadeColor");
        subs.add("withTrail");
        subs.add("withFlicker");
        subs.add("removeEffect");
        subs.add("removeColor");
        return filter(subs, match);
    }

    private List<String> launchAmount(){
        List<String> launch = new ArrayList<>();
        launch.add("[amount of fireworks to launch]");
        return launch;
    }
    private List<String> delay(){
        List<String> delay = new ArrayList<>();
        delay.add("[time in between firework shots in ticks]");
        return delay;
    }

    private List<String> displays(String match){
        if (plugin.displayFile.getConfig().contains("displays")){
            Set<String> displayIds = plugin.displayFile.getConfig().getConfigurationSection("displays").getKeys(false);
            return filter(displayIds, match);
        }
        return null;
    }

    private List<String> effects(String match){
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        Set<String> typeNames = new HashSet<>();
        for (FireworkEffect.Type type : types){
            typeNames.add(type.name());
        }
        return filter(typeNames, match);
    }


    private List<String> effectsInDisplay(String display, String match){
        if (plugin.displayFile.getConfig().contains("displays." + display + ".effects")){
            Set<String> effects = plugin.displayFile.getConfig().getConfigurationSection("displays." + display + ".effects").getKeys(false);
            return filter(effects, match);
        }
        return null;
    }
    private List<String> colorsInDisplay(String type, String display, String match){
        List<String> colors = plugin.displayFile.getConfig().getStringList("displays." + display + ".effects." + type + ".colors");
        return filter(colors, match);
    }

    private List<String> colors(){
        List<String> rgb = new ArrayList<>();
        rgb.add("[red,green,blue]");
        return rgb;
    }
    private List<String> booleans(String match){
        List<String> bools = new ArrayList<>();
        bools.add("true");
        bools.add("false");
        return filter(bools, match);
    }
    private List<String> powers(){
        List<String> power = new ArrayList<>();
        power.add("[number-determining-how-high-the-rocket-will-fly]");
        return power;
    }

}
