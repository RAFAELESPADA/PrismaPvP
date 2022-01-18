package com.br.gabrielsilva.prismamc.kitpvp.manager.gamer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import lombok.Getter;

public class GamerManager {

	@Getter
	private static ConcurrentHashMap<UUID, Gamer> gamers = new ConcurrentHashMap<>();
	
	public static Gamer getGamer(UUID uniqueId) {
		return gamers.get(uniqueId);
	}
	
	public static Gamer getGamer(Player player) {
		return gamers.get(player.getUniqueId());
	}
	
	public static void addGamer(UUID uniqueId) {
		gamers.put(uniqueId, new Gamer(uniqueId));
	}
	
	public static void removeGamer(UUID uniqueId) {
		gamers.remove(uniqueId);
	}
}