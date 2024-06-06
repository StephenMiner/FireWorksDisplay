package me.stephenminer.fireworksdisplay.commands;

import me.stephenminer.fireworksdisplay.FireWorksDisplay;
import me.stephenminer.fireworksdisplay.display.Launcher;
import me.stephenminer.fireworksdisplay.display.LauncherBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LauncherCmds implements CommandExecutor, TabCompleter {
    private final FireWorksDisplay plugin;

    public static HashMap<UUID, String> addTriggers = new HashMap<>();

    public LauncherCmds(FireWorksDisplay plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("launcher")){
            int size = args.length;
            if (sender instanceof Player){
                Player player = (Player) sender;
                if (!player.hasPermission("fd.commands.launcher")){
                    player.sendMessage(ChatColor.RED + "You do not haeojqgesgregehtjk I don't want to write this message again...");
                    return false;
                }

                if (size >= 3){
                    String sub = args[0];
                    if (sub.equalsIgnoreCase("create")){
                        if (launcherExists(args[1])){
                            player.sendMessage(ChatColor.RED + "Inputted launcher already exists!");
                            return false;
                        }
                        createLauncher(args[1]);
                        player.sendMessage(ChatColor.GREEN + "Created launcher " + args[1]);
                        for (int i = 2; i < size; i++){
                            if (isDisplay(args[i])){
                                addDisplay(args[1], args[i]);
                                player.sendMessage(ChatColor.GREEN + "Added display " + args[i] + " to launcher " + args[1]);
                            }else{
                                player.sendMessage(ChatColor.RED + "Inputted display " + args[i] + " doesn't exist!");
                            }
                        }
                        return true;
                    }
                    if (!launcherExists(args[1])){
                        player.sendMessage(ChatColor.RED + "Inputted launcher doesn't exist!");
                        return false;
                    }

                    if (sub.equalsIgnoreCase("addDisplays")){
                        for (int i = 2; i < size; i++){
                            if (isDisplay(args[i])){
                                addDisplay(args[1], args[i]);
                                player.sendMessage(ChatColor.GREEN + "Added display " + args[i] + " to launcher " + args[1]);
                            }else{
                                player.sendMessage(ChatColor.RED + "Inputted display " + args[i] + " doesn't exist!");
                            }
                        }
                        return true;
                    }
                    if (sub.equalsIgnoreCase("removeDisplay")){
                        for (int i = 2; i < size; i++){
                            if (hasDisplay(args[1], args[i])){
                                removeDisplay(args[1], args[i]);
                                player.sendMessage(ChatColor.GREEN + "Removed display " + args[i] + " from launcher " + args[1]);
                            }else {
                                player.sendMessage(ChatColor.RED + "Launcher already doesn't have display " + args[i]);
                            }
                        }
                        return true;
                    }
                    if (sub.equalsIgnoreCase("setInterval")){
                        int interval = 0;
                        try{
                            interval = Integer.parseInt(args[2]);
                            setInterval(args[1], interval);
                            if (interval >= 1)
                                player.sendMessage(ChatColor.GREEN + "Set interval to " + interval + " ticks!");
                            else player.sendMessage(ChatColor.GREEN + "Removed interval from launcher " + args[1]);
                            return true;
                        }catch (Exception ignored){}

                        player.sendMessage(ChatColor.RED + "The number you inputted isn't a real number");
                        player.sendMessage(ChatColor.YELLOW + "If you want to remove the interval, put a number that is less than 1");
                        return false;
                    }
                }
                if (size >= 2){
                    String sub = args[0];
                    if (!launcherExists(args[1])){
                        player.sendMessage(ChatColor.RED + "Inputted launcher doesn't exist!");
                        return false;
                    }

                    if (sub.equalsIgnoreCase("setTrigger")){
                        addTriggers.put(player.getUniqueId(), args[1]);
                        player.sendMessage(ChatColor.GREEN + "Right click the block that you want to trigger this launcher");
                        return true;
                    }

                    if (sub.equalsIgnoreCase("giveRemote")){
                        player.getInventory().addItem(createRemote(args[1]));
                        player.sendMessage(ChatColor.GREEN + "You have been given your remote!");
                        return true;
                    }

                    if (sub.equalsIgnoreCase("reload")){
                        for (int i = 0; i < plugin.launchers.size(); i++){
                            Launcher launcher = plugin.launchers.get(i);
                            if (launcher.getName().equalsIgnoreCase(args[1])){
                                launcher.kill();
                                plugin.launchers.remove(i);
                                break;
                            }
                        }
                        LauncherBuilder builder = new LauncherBuilder(plugin, args[1]);
                        builder.buildLauncher();
                        player.sendMessage(ChatColor.GREEN + "Created Launcher");
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private boolean launcherExists(String name){
        return plugin.launcherFile.getConfig().contains("launchers." + name);
    }
    private boolean isDisplay(String display){
        return plugin.displayFile.getConfig().contains("displays." + display);
    }
    private boolean hasDisplay(String name, String display){
        List<String> displays = plugin.launcherFile.getConfig().getStringList("launchers." + name + ".displays");
        return isDisplay(display) && displays.contains(display);
    }
    private void createLauncher(String name){
        plugin.launcherFile.getConfig().createSection("launchers." + name);
        plugin.launcherFile.saveConfig();
    }
    private void addDisplay(String name, String display){
        List<String> displays = plugin.launcherFile.getConfig().getStringList("launchers." + name + ".displays");
        displays.add(display);
        plugin.launcherFile.getConfig().set("launchers." + name + ".displays", displays);
        plugin.launcherFile.saveConfig();
    }
    private void setInterval(String name, int interval){
        String path = "launchers." + name + ".interval";
        if (interval >= 1) plugin.launcherFile.getConfig().set(path, interval);
        else plugin.launcherFile.getConfig().set(path, null);
        plugin.launcherFile.saveConfig();
    }
    private void removeDisplay(String name, String display){
        List<String> displays = plugin.launcherFile.getConfig().getStringList("launchers." + name + ".displays");
        displays.remove(display);
        plugin.launcherFile.getConfig().set("launchers." + name + ".displays", displays);
        plugin.launcherFile.saveConfig();
    }





    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("launcher")){
            int size = args.length;
            if (size == 1) return subCmds(args[0]);
            if (size == 2){
                if (args[0].equalsIgnoreCase("create")) return yourName();
                return launchers(args[1]);
            }
            if (size >= 3){
                if (args[0].equalsIgnoreCase("setInterval")) return interval();
                if (args[0].equalsIgnoreCase("addDisplays")) return displays(args[2]);
                if (args[0].equalsIgnoreCase("removeDisplays")) return displaysInLauncher(args[1], args[2]);
            }
        }
        return null;
    }

    private List<String> filter(Collection<String> base, String match){
        List<String> filtered = new ArrayList<>();
        for (String entry : base){
            String temp = entry.toLowerCase(Locale.ROOT);
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }

    private List<String> subCmds(String match){
        List<String> subs = new ArrayList<>();
        subs.add("create");
        subs.add("reload");
        subs.add("addDisplays");
        subs.add("setTrigger");
        subs.add("removeDisplays");
        subs.add("giveRemote");
        subs.add("setInterval");
        return filter(subs, match);
    }

    private List<String> displays(String match){
        if (plugin.displayFile.getConfig().contains("displays")) {
            Set<String> displayIds = plugin.displayFile.getConfig().getConfigurationSection("displays").getKeys(false);
            return filter(displayIds, match);
        }
        return null;
    }
    private List<String> displaysInLauncher(String launcher, String match){
        if (plugin.displayFile.getConfig().contains("launchers." + launcher + ".displays")){
            List<String> displays = plugin.launcherFile.getConfig().getStringList("launchers." + launcher + ".displays");
            return filter(displays, match);
        }
        return null;
    }
    private List<String> launchers(String match){
        if (plugin.launcherFile.getConfig().contains("launchers")) {
            Set<String> launchers = plugin.launcherFile.getConfig().getConfigurationSection("launchers").getKeys(false);
            return filter(launchers, match);
        }
        return null;
    }
    private List<String> yourName(){
        List<String> name = new ArrayList<>();
        name.add("[your-name-here]");
        return name;
    }
    private List<String> interval(){
        List<String> interval = new ArrayList<>();
        interval.add("[time-between-repeats-in-ticks]");
        return interval;
    }
    private List<String> booleans(String match){
        List<String> bools = new ArrayList<>();
        bools.add("true");
        bools.add("false");
        return filter(bools, match);
    }



    public ItemStack createRemote(String launcher){
        ItemStack remote = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = remote.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Launcher Remote");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Go off with a bang");
        lore.add(ChatColor.YELLOW + "Right click to activate fireworks display");
        lore.add(ChatColor.YELLOW + "Left click to disable it");
        lore.add(ChatColor.BLACK + launcher);
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 15, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        remote.setItemMeta(meta);
        return remote;
    }





}
