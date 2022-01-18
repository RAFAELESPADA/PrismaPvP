package com.br.gabrielsilva.prismamc.kitpvp.ability.register;


import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Poseidon extends Kit {

	public Poseidon() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		ItemStack icone = new ItemBuilder().
				material(Material.WATER_BUCKET).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(4000);
				setCooldownSegundos(0);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Seja forte na água");
				setDescrição(indiob);
	}
	
	@EventHandler
	public void Habilidade(PlayerMoveEvent e) {
		if (e.isCancelled())
			return;
		
		Player p = e.getPlayer();
		if (e.getTo().getBlock().getType().equals(Material.WATER) || e.getTo().getBlock().getType().equals(Material.STATIONARY_WATER)) {
		    if (hasAbility(p)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
		    }
		}
	}
}