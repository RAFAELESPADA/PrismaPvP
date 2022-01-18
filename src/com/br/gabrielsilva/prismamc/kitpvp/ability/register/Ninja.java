package com.br.gabrielsilva.prismamc.kitpvp.ability.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpQuitEvent;

import net.md_5.bungee.api.ChatColor;

public class Ninja extends Kit {

	public Ninja() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
			
		ItemStack icone = new ItemBuilder().
				material(Material.COAL).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(4000);
				setCooldownSegundos(7);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Teleporte ao ultimo jogador hitado");
				setDescrição(indiob);
	}
	
	HashMap<UUID, UUID> tp = new HashMap<>();
	
    @EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (tp.containsKey(event.getPlayer().getUniqueId())) {
			tp.remove(event.getPlayer().getUniqueId());
		}
	}
	
	 @EventHandler
	 public void setTarget(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
		if ((e.getEntity() instanceof Player) && (e.getDamager() instanceof Player)) {
	         Player d = (Player)e.getDamager();
	 	     if (hasAbility(d)) {
		         Player p = (Player)e.getEntity();
	             tp.put(d.getUniqueId(), p.getUniqueId());
	 	     }
		}
	 }
	 

	 @EventHandler
	 public void onPlayerWarpQuitEvent(PlayerWarpQuitEvent event) {
		 if (tp.containsKey(event.getPlayer().getUniqueId())) {
			 tp.remove(event.getPlayer().getUniqueId());
		 }
	 }

	 @EventHandler
	 public void teleport(PlayerToggleSneakEvent e) {
	     Player p = e.getPlayer();
	     if ((p.isSneaking()) && (hasAbility(p))) {
	     	  if (tp.containsKey(p.getUniqueId())) {
	     		  if (!inCooldown(p)) {
	                  Player req = Bukkit.getPlayer(tp.get(p.getUniqueId()));
	                  if (req != null) {
	                	  if (req.getWorld() == p.getWorld()) {
	        				  if (p.getLocation().distance(req.getLocation()) <= 40) {
		                 	      p.teleport(req);
		                 	      addCooldown(p, getCooldownSegundos());
							  } else {
								  p.sendMessage("§cO jogador esta muito longe.");
							  }
	                	  } else {
	                		  p.sendMessage("§cO jogador esta muito longe.");
	                	  }
	                 }
	     		  }
	     	  }
	     }
	 }
}