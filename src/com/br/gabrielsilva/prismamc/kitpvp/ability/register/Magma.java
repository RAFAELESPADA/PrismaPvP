package com.br.gabrielsilva.prismamc.kitpvp.ability.register;

import java.util.ArrayList;
import java.util.Random;
import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Magma extends Kit {

	public Magma() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
			
		ItemStack icone = new ItemBuilder().
				material(Material.LAVA_BUCKET).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(6000);
				setCooldownSegundos(0);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Bote fogo e seja imune a ele");
				setDescrição(indiob);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void Dano(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			if (hasAbility(p)) {
				if (new Random().nextInt(100) <= 35) {
					LivingEntity d = (LivingEntity) e.getEntity();
					d.setFireTicks(d.getFireTicks() + 120);
				}
			}
		}
	}
	
	@EventHandler
	public void Habilidade(EntityDamageEvent e) {
		if (e.isCancelled())
			return;
		
		if (e.getEntity() instanceof Player) {
			if (e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.LAVA) {
				if (hasAbility((Player) e.getEntity())) {
				    e.setCancelled(true);
				    e.getEntity().setFireTicks(0);
				}
			}
		}
	}
	
	@EventHandler
	public void Habilidade(PlayerMoveEvent e) {
		if (e.isCancelled())
			return;
		
		Player p = e.getPlayer();
		if (e.getTo().getBlock().getType().equals(Material.WATER) || e.getTo().getBlock().getType().equals(Material.STATIONARY_WATER)) {
			if (hasAbility(p)) {
		    	p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 140, 6));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30, 0));
		    }
		}
	}
}