package com.br.gabrielsilva.prismamc.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.server.ServerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.commands.BukkitCommandFramework;
import com.br.gabrielsilva.prismamc.commons.core.Core;
import com.br.gabrielsilva.prismamc.commons.core.utils.loaders.CommandLoader;
import com.br.gabrielsilva.prismamc.commons.core.utils.loaders.ListenerLoader;
import com.br.gabrielsilva.prismamc.kitpvp.manager.hologram.HologramManager;
import com.br.gabrielsilva.prismamc.kitpvp.manager.kit.KitLoader;
import com.br.gabrielsilva.prismamc.kitpvp.manager.scoreboard.ScoreBoardManager;
import com.br.gabrielsilva.prismamc.kitpvp.manager.warp.WarpManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.Warp1v1;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.WarpArena;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.WarpFPS;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.WarpLavaChallenge;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.WarpSpawn;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PrismaPvP extends JavaPlugin {

	@Getter @Setter
	private static PrismaPvP instance;
	
	private WarpManager warpManager;
	private ScoreBoardManager scoreBoardManager;
	
	public void onLoad() {
		setInstance(this);
		saveDefaultConfig();
	}
	
	public void onEnable() {
		
		BukkitMain.getManager().enableHologram();
		Bukkit.setDefaultGameMode(GameMode.ADVENTURE);
		
		this.warpManager = new WarpManager(this);
		this.scoreBoardManager = new ScoreBoardManager();
		
		this.getScoreBoardManager().init();
		
        loadWarps();
		
		ListenerLoader.loadListenersBukkit(getInstance(), "com.br.gabrielsilva.prismamc.kitpvp.listener");
		new CommandLoader(new BukkitCommandFramework(getInstance())).loadCommandsFromPackage("com.br.gabrielsilva.prismamc.kitpvp.commands");
		
		KitLoader.init();
		ServerAPI.registerAntiAbuser();
		
		
		HologramManager.init();
	}
	
	public void onDisable() {
		Core.getServersHandler().sendUpdateNetworkServer("kitpvp", 0, Bukkit.getMaxPlayers());
	}
	
    private void loadWarps() {
        Warp warp = new WarpSpawn();
        
        this.warpManager.addWarp(warp);
        this.warpManager.setDefaultWarp(warp);
        
        warp = new WarpArena();
        this.warpManager.addWarp(warp);
        
        warp = new WarpFPS();
        this.warpManager.addWarp(warp);
        
        warp = new Warp1v1();
        this.warpManager.addWarp(warp);
        
        warp = new WarpLavaChallenge();
        this.warpManager.addWarp(warp);
    }
	
	public static void console(String msg) {
		Bukkit.getConsoleSender().sendMessage("[PrismaPvP] " + msg);
	}
	
	public static void runAsync(Runnable runnable) {
		Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);	
	}
	
	public static void runLater(Runnable runnable) {
		runLater(runnable, 5);
	}
	
	public static void runLater(Runnable runnable, long ticks) {
		Bukkit.getScheduler().runTaskLater(getInstance(), runnable, ticks);	
	}
}