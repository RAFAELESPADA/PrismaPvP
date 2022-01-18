package com.br.gabrielsilva.prismamc.kitpvp.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;
import com.br.gabrielsilva.prismamc.kitpvp.events.PlayerDamagePlayerEvent;

import net.md_5.bungee.api.ChatColor;

public class Hulk extends Kit {

	public Hulk() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		ItemStack icone = new ItemBuilder().
		material(Material.SADDLE).
		durability(0).
		amount(1)
		.build();
		
		setIcone(icone);
		
		setPreço(4000);
		setCooldownSegundos(10);
        ArrayList indiob = new ArrayList();
/* 351 */         indiob.add(ChatColor.GRAY + "Pegue jogadores nas costas");
		setDescrição(indiob);
		setItens(new ItemStack(Material.AIR));
	}
	
	@EventHandler
	public void hulk(PlayerInteractEntityEvent e) {
		if (e.getPlayer() instanceof Player && e.getRightClicked() instanceof Player) {
			Player p = (Player) e.getPlayer();
			if (p.getInventory().getItemInHand().getType().equals(Material.AIR)) {
				if (hasAbility(p)) {
					if (inCooldown(p)) {
						sendMessageCooldown(p);
						return;
					}
					Player d = (Player) e.getRightClicked();
					p.setPassenger(d);
					addCooldown(p, getCooldownSegundos());
				}
			}
		}
	}

	@EventHandler
	public void hulkmatar(EntityDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (p.isInsideVehicle()) {
				p.leaveVehicle();
			}
		}
	}

	@EventHandler
	public void onDamage(PlayerDamagePlayerEvent event) {
		if (event.isCancelled())
			return;
		
		Player damaged = event.getDamaged(),
				damager = event.getPlayer();
		
		if ((damaged.isInsideVehicle()) && (hasAbility(damager))) {
			 event.setCancelled(true);
			 damaged.leaveVehicle();
			 
			 
			 damaged.setVelocity(new Vector(damager.getLocation().getDirection().getX() + 0.3, 1.5, damager.getLocation().getDirection().getZ() + 0.3));
		}
	}
}