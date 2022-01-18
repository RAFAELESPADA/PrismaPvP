package com.br.gabrielsilva.prismamc.kitpvp.ability.register;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Thor extends Kit {

	public Thor() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		
		ItemStack icone = new ItemBuilder().
				material(Material.WOOD_AXE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(5000);
				setCooldownSegundos(5);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Invoque raios!");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.WOOD_AXE).name(
				getItemColor() + "Kit " + getNome()).build());
	}
	
	@EventHandler
	public void Habilidade(PlayerInteractEvent e) {
		if ((e.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE)) && (e.getAction().name().contains("RIGHT")) && 
				(hasAbility(e.getPlayer()))) {
			
			if (inCooldown(e.getPlayer())) {
				sendMessageCooldown(e.getPlayer());
				return;
			}
			Location loc = e.getPlayer().getTargetBlock((HashSet<Byte>) null, 25).getLocation();
			if (!loc.getBlock().getType().equals(Material.AIR)) {
				addCooldown(e.getPlayer(), getCooldownSegundos());
				
				LightningStrike strike = loc.getWorld().strikeLightning(loc);
				
				for (Entity nearby : strike.getNearbyEntities(4.0D, 4.0D, 4.0D)) {
					 if ((nearby instanceof Player)) {
						 nearby.setFireTicks(100);
					 }
					 e.getPlayer().setFireTicks(0);
				}
			}
		}
	}
}