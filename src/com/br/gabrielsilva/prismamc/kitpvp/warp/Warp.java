package com.br.gabrielsilva.prismamc.kitpvp.warp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import com.br.gabrielsilva.prismamc.commons.bukkit.manager.config.PluginConfig;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpQuitEvent;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.Gamer;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Warp implements Listener {

	private String name;
	
	@Getter @Setter
	private Location spawnLocation;
	
	@Getter
	private Material material;
	
	private AtomicInteger amountInWarp;
	
	public Warp(String name, Material material) {
		this.name = name;
		this.material = material;
		this.amountInWarp = new AtomicInteger();
		
        PluginConfig.createLoc(PrismaPvP.getInstance(), name.toLowerCase());
        setSpawnLocation(PluginConfig.getNewLoc(PrismaPvP.getInstance(), name.toLowerCase()));
	}
	
	public List<Player> getPlayers() {
		List<Player> lista = new ArrayList<>();
		
		for (Gamer gamers : GamerManager.getGamers().values()) {
			 if (gamers.getWarp() == this) {
				 lista.add(gamers.getPlayer());
			 }
		}
		return lista;
	}
	
    public String getId() {
        return this.name.toLowerCase().trim().replace(" ", ".");
    }

    public boolean inWarp(Player player) {
        return GamerManager.getGamer(player).getWarp() == this;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void removePlayer(PlayerWarpQuitEvent event) {
        if (inWarp(event.getPlayer())) {
        	if (amountInWarp.decrementAndGet() == 0) {
        		HandlerList.unregisterAll(this);
        	}
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Warp)) {
            return false;
        }
        Warp compare = (Warp) obj;
        return compare.getId().equals(this.getId());
    }
}