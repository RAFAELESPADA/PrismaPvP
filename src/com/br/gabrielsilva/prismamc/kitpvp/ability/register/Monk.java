package com.br.gabrielsilva.prismamc.kitpvp.ability.register;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Monk extends Kit {

	public Monk() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
			
		ItemStack icone = new ItemBuilder().
				material(Material.BLAZE_ROD).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(2000);
				setCooldownSegundos(20);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Bagunce o inv do inimigo");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.BLAZE_ROD).name(
				getItemColor() + "Kit " + getNome()).build());
	}
	
	@EventHandler
	public void Habilidade(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			Player p = e.getPlayer();
			if ((p.getItemInHand().getType().equals(Material.BLAZE_ROD)) && 
					(hasAbility(p))) {
				
				 
				 if (inCooldown(p)) {
					 sendMessageCooldown(p);
					 return;
				 }
				
				 Player d = (Player) e.getRightClicked();
				 final ItemStack item = d.getItemInHand();
				 final int r = new Random().nextInt(35);
				 final ItemStack i = d.getInventory().getItem(r);
				 d.getInventory().setItem(r, item);
				 d.setItemInHand(i);
				 addCooldown(p, getCooldownSegundos());
				 p.sendMessage("§aMonkado!");
				 d.sendMessage("§aVoce foi monkado!");
			}
		}
	}
}