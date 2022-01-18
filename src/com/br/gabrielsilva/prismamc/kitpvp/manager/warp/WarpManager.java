package com.br.gabrielsilva.prismamc.kitpvp.manager.warp;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpJoinEvent;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpQuitEvent;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class WarpManager {

    private Plugin plugin;

    private HashMap<String, Warp> warps;
    private Warp defaultWarp;

    public WarpManager(Plugin plugin) {
        this.plugin = plugin;
        this.warps = new HashMap<>();
    }

    public Warp getWarp(String id) {
        return warps.getOrDefault(id.toLowerCase(), null);
    }
    
    public Collection<Warp> getWarps() {
    	return warps.values();
    }

    public Warp getPlayerWarp(Player player) {
    	return GamerManager.getGamer(player).getWarp();
    }
    
    public void respawnOnWarp(Player player, Warp warp) {
    	refreshPlayer(player);
    	
    	PlayerWarpJoinEvent event = new PlayerWarpJoinEvent(player, warp);
    	player.teleport(warp.getSpawnLocation());
    	plugin.getServer().getPluginManager().callEvent(event);
    }

    public void joinWarp(Player player, Warp warp) {
        leaveWarp(player);
    	
        if (warp.getAmountInWarp().incrementAndGet() == 1) {
            plugin.getServer().getPluginManager().registerEvents(warp, plugin);
        }
        
        GamerManager.getGamer(player).setWarp(warp);
        refreshPlayer(player);
           
        PlayerWarpJoinEvent event = new PlayerWarpJoinEvent(player, warp);
            
        if ((!warp.getName().equalsIgnoreCase("Arena")) && (!warp.getName().equalsIgnoreCase("Fisherman"))) {
            player.teleport(warp.getSpawnLocation());
        }
        
        plugin.getServer().getPluginManager().callEvent(event);
    }

    public void leaveWarp(Player p) {
        Warp lastWarp = getPlayerWarp(p);
        
        if (lastWarp == null)
            return;
        
        PlayerWarpQuitEvent event = new PlayerWarpQuitEvent(p, lastWarp);
        plugin.getServer().getPluginManager().callEvent(event);
    }

    public void addWarp(Warp warp) {
        warps.put(warp.getId(), warp);
    }

    public Warp getDefaultWarp() {
        return defaultWarp;
    }

    public void setDefaultWarp(Warp warp) {
        this.defaultWarp = warp;
    }
    
	public void refreshPlayer(Player player) {
		GamerManager.getGamer(player).setProtection(true);
		player.setNoDamageTicks(20);
		
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		
        player.setHealth(20.0D);
		player.setFireTicks(0);
		player.setLevel(0);
		player.setExp((float)0);
		PlayerAPI.clearEffects(player);
		
		if (!player.getGameMode().equals(GameMode.ADVENTURE)) {
		    player.setGameMode(GameMode.ADVENTURE);
		}
	}
}