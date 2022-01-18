package com.br.gabrielsilva.prismamc.kitpvp.manager.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.scoreboard.Sidebar;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.scoreboard.animation.WaveAnimation;
import com.br.gabrielsilva.prismamc.commons.core.data.DataHandler;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.rank.PlayerRank;
import com.br.gabrielsilva.prismamc.commons.core.utils.string.StringUtils;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.manager.kit.KitManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class ScoreBoardManager {
	
	private WaveAnimation waveAnimation;
	private String text = "";
	
	public void init() {
		waveAnimation = new WaveAnimation(" PVP ", "§f§l", "§b§l", "§3§l", 3);
		text = waveAnimation.next();
		
		Bukkit.getScheduler().runTaskTimer(PrismaPvP.getInstance(), new Runnable() {
			public void run() {
				text = waveAnimation.next();
				
				for (Player onlines : Bukkit.getOnlinePlayers()) {
					 if (onlines == null) {
						 continue;
					 }
					 if (!onlines.isOnline()) {
						 continue;
					 }
					 if (onlines.isDead()) {
						 continue;
					 }
				 	 Scoreboard score = onlines.getScoreboard();
					 if (score == null) {
						 continue;
					 }
					 Objective objective = score.getObjective(DisplaySlot.SIDEBAR);
					 if (objective == null) {
						 continue;
					 }
					 objective.setDisplayName(text);
				}
			}
		}, 40, 2L);
	}
	
	public void createSideBar(Player player, Warp warpPlayer) {
		BukkitPlayer bukkitPlayer = BukkitMain.getManager().getDataManager().getBukkitPlayer(player.getUniqueId());
		
		Sidebar sideBar = bukkitPlayer.getSideBar();
		if (sideBar == null) {
			bukkitPlayer.setSideBar(sideBar = new Sidebar(player.getScoreboard()));
			sideBar.show();
		}
		if (sideBar.isHided())
			return;
		
		sideBar.hide();
		sideBar.show();
		
		sideBar.setTitle("§b§lPVP");
		
		if (warpPlayer.getName().equalsIgnoreCase("1v1")) {
			sideBar.setText(9, "");
			sideBar.setText(8, "Vitórias: §a0");
		    sideBar.setText(7, "Derrotas: §c0");
		    sideBar.setText(6, "");
		    sideBar.setText(5, "§fLiga: ...");
		    sideBar.setText(4, "");
		    sideBar.setText(3, "KillStreak: §e0");
		} else {
			sideBar.setText(11, "");
			sideBar.setText(10, "Matou: §a0");
		    sideBar.setText(9, "Morreu: §c0");
		    sideBar.setText(8, "");
		    sideBar.setText(7, "§fLiga: ...");
		    sideBar.setText(6, "");
		    sideBar.setText(5, "KillStreak: §e0");
		    sideBar.setText(4, "");
		    sideBar.setText(3, "Kit: §7Nenhum");
		}
	    sideBar.setText(2, "");
		sideBar.setText(1, "§bkombopvp.net");
		
		updateScoreboard(player, warpPlayer);
	}
	
	public void updateScoreboard(final Player player, Warp warpPlayer) {
		final BukkitPlayer bukkitPlayer = BukkitMain.getManager().getDataManager().getBukkitPlayer(player.getUniqueId());
		
		Sidebar sideBar = bukkitPlayer.getSideBar();
		
		if (sideBar == null) {
			return;
		}
		if (sideBar.isHided()) {
			return;
		}
		
		final DataHandler dataHandler = bukkitPlayer.getDataHandler();
		
		final PlayerRank playerRank = bukkitPlayer.getRank();
		
		if (warpPlayer.getName().equalsIgnoreCase("1v1")) {
			sideBar.setText(8, "Vitórias: §a" + StringUtils.reformularValor(dataHandler.getData(DataType.ONEVSONE_WINS).getInt()));
		    sideBar.setText(7, "Derrotas: §c" + StringUtils.reformularValor(dataHandler.getData(DataType.ONEVSONE_LOSES).getInt()));
		    
			sideBar.setText(5, "Liga: §7[" + playerRank.getCor() + playerRank.getSimbolo() + "§7] " + playerRank.getCor()  + playerRank.getNome());
		    
		    sideBar.setText(3, "KillStreak: §e" + StringUtils.reformularValor(dataHandler.getData(DataType.PVP_KILLSTREAK).getInt()));
		} else {
			sideBar.setText(10, "Matou: §a" + StringUtils.reformularValor(dataHandler.getData(DataType.PVP_KILLS).getInt()));
		    sideBar.setText(9, "Morreu: §c" + StringUtils.reformularValor(dataHandler.getData(DataType.PVP_DEATHS).getInt()));
			sideBar.setText(7, "Liga: §7[" + playerRank.getCor() + playerRank.getSimbolo() + "§7] " + playerRank.getCor()  + playerRank.getNome());
		    sideBar.setText(5, "KillStreak: §e" + StringUtils.reformularValor(dataHandler.getData(DataType.PVP_KILLSTREAK).getInt()));
		    sideBar.setText(3, "Kit: §7" + KitManager.getKitPlayer(player));
		}
	}
}