package me.stephenminer.fireworksdisplay.events;

import me.stephenminer.fireworksdisplay.FireWorksDisplay;
import me.stephenminer.fireworksdisplay.commands.LauncherCmds;
import me.stephenminer.fireworksdisplay.display.Launcher;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SetUpEvents implements Listener {
    private final FireWorksDisplay plugin;

    public SetUpEvents(FireWorksDisplay plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void setTrigger(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (LauncherCmds.addTriggers.containsKey(player.getUniqueId())){
                setTrigger(LauncherCmds.addTriggers.get(player.getUniqueId()), block.getLocation());
                LauncherCmds.addTriggers.remove(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Set trigger loc for launcher");
            }
        }
    }

    @EventHandler
    public void trigger(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.hasItem()){
            ItemStack item = event.getItem();
            for (Launcher launcher : plugin.launchers){
                if (hasLore(item, launcher.getName())){
                    if (!launcher.isActivated()) {
                        if (launcher.repeating())
                            launcher.activate(true);
                        else launcher.activate();
                        player.sendMessage(ChatColor.GREEN + "Activated Launcher");
                        return;
                    }else{
                        launcher.kill();
                        player.sendMessage(ChatColor.GREEN + "Deactivated Launcher");
                    }
                }
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            String base = plugin.fromBlockLoc(event.getClickedBlock().getLocation());
            for (Launcher launcher : plugin.launchers){
                String sLoc = plugin.fromBlockLoc(launcher.getTrigger());
                if (base.equals(sLoc)){
                    if (!launcher.isActivated()) {
                        if (launcher.repeating())
                            launcher.activate(true);
                        else launcher.activate();
                        player.sendMessage(ChatColor.GREEN + "Activated Launcher");
                        return;
                    }else{
                        launcher.kill();
                        player.sendMessage(ChatColor.GREEN + "Deactivated Launcher");
                    }
                }
            }
        }
    }

    @EventHandler
    public void checkTriggerRedstone(BlockRedstoneEvent event){
        Block block = event.getBlock();
        String base = plugin.fromBlockLoc(block.getLocation());
        for (Launcher launcher : plugin.launchers){
            String sLoc = plugin.fromBlockLoc(launcher.getTrigger());
            if (base.equals(sLoc)){
                if (!launcher.isActivated()) {
                    launcher.activate();
                    //player.sendMessage(ChatColor.GREEN + "Activated Launcher");
                    return;
                }
            }
        }

    }

    @EventHandler
    public void listCleanup(PlayerQuitEvent event){
        Player player = event.getPlayer();
        LauncherCmds.addTriggers.remove(player.getUniqueId());
    }

    private void setTrigger(String launcher, Location loc){
        plugin.launcherFile.getConfig().set("launchers." + launcher + ".trigger", plugin.fromBlockLoc(loc));
        plugin.launcherFile.saveConfig();
    }

    private boolean hasLore(ItemStack item, String target){
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
        List<String> lore = item.getItemMeta().getLore();
        for (String entry : lore){
            String temp = ChatColor.stripColor(entry);
            if (temp.equals(target)) return true;
        }
        return false;
    }
}
