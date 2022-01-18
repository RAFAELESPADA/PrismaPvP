package com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight;

import java.util.UUID;

import org.bukkit.Material;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomManager {

	private UUID uniqueId;
	private boolean fullSopa, recraft;
	private Material armadura, espada;
	private int efeitos;
	
	public CustomManager(UUID uuid) {
		this.uniqueId = uuid;
		this.fullSopa = false;
		this.recraft = false;
		this.armadura = Material.GLASS;
		this.espada = Material.DIAMOND_SWORD;
		this.efeitos = 0;
	}
}
