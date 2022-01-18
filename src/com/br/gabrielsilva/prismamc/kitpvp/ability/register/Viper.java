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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Viper extends Kit {

	public Viper() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
			
		ItemStack icone = new ItemBuilder().
				material(Material.SPIDER_EYE).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(4000);
				setCooldownSegundos(0);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "De veneno nos inimigos");
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
					d.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
				}
			}
		}
	}
}