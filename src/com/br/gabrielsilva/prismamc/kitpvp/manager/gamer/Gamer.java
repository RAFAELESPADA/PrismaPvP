package com.br.gabrielsilva.prismamc.kitpvp.manager.gamer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.br.gabrielsilva.prismamc.kitpvp.utils.PvPMessages;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Gamer {

	private UUID uniqueId;
	private boolean protection;
	private Warp warp;
	
	public Gamer(UUID uniqueId) {
		setUniqueId(uniqueId);
		setProtection(true);
		setWarp(null);
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uniqueId);
	}
	
    public void removeProtection(Player player, boolean message) {
    	if (!isProtection()) {
    		return;
    	}
    	
    	setProtection(false);
    	
        if (message) {
        	player.sendMessage(PvPMessages.PERDEU_A_PROTEÇÃO);
        }
    }
}