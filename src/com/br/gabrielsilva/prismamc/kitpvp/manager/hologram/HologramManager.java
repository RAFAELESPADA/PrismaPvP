package com.br.gabrielsilva.prismamc.kitpvp.manager.hologram;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.UpdateEvent;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.UpdateEvent.UpdateType;
import com.br.gabrielsilva.prismamc.commons.bukkit.hologram.types.PlayerRankingHologram;
import com.br.gabrielsilva.prismamc.commons.bukkit.hologram.types.SimpleHologram;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;

import lombok.Getter;
import lombok.Setter;

public class HologramManager {

	@Getter @Setter
	private static SimpleHologram topKills, topWins;
	
	@Getter @Setter
	private static PlayerRankingHologram playerRanking;
	
	public static void init() {
		setPlayerRanking(new PlayerRankingHologram(PrismaPvP.getInstance(), "ranking"));
		setTopKills(new SimpleHologram(PrismaPvP.getInstance(), "KILLS", "kitpvp", "kills"));
		setTopWins(new SimpleHologram(PrismaPvP.getInstance(), "WINS", "kitpvp", "1v1_wins"));
		
		getPlayerRanking().create();
		getTopKills().create();
		getTopWins().create();
		
		registerListener();
	}

	private static void registerListener() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			
			int minutos = 0;
			
			@EventHandler
			public void onUpdate(UpdateEvent event) {
				if (event.getType() == UpdateType.MINUTO) {
					minutos++;
					
					if (minutos == 10) {
						synchronized(this) {
							getPlayerRanking().updateValues();
							getTopKills().updateValues();
							getTopWins().updateValues();
						}
						
						minutos = 0;
					}
				}
			}
		}, PrismaPvP.getInstance());
	}
}