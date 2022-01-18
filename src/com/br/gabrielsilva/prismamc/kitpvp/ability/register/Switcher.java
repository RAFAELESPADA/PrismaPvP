package com.br.gabrielsilva.prismamc.kitpvp.ability.register;

import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Switcher extends Kit {

	public Switcher() {
		setNome(getClass().getSimpleName());
		
		ItemStack icone = new ItemBuilder().
				material(Material.SNOW_BALL).
				durability(0).
				amount(16)
				.build();
				
				setIcone(icone);
				
				setPreço(4000);
				setCooldownSegundos(0);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Troque de lugar com o inimigo");
				setDescrição(indiob);
				setItens(new ItemBuilder().material(Material.SNOW_BALL).name(
						getItemColor() + "Kit " + getNome()).amount(16).build());
	}

	@EventHandler
	public void Habilidade(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		
	    if ((e.getDamager() instanceof Snowball) && (e.getEntity() instanceof Player)) {  
	    	 Snowball s = (Snowball)e.getDamager();
	         Player shooter = (Player)s.getShooter();
	         if ((s.getShooter() instanceof Player) && (hasAbility(shooter))) {
	              Location shooterLoc = shooter.getLocation();
	              shooter.teleport(e.getEntity().getLocation());
	              
	              e.getEntity().teleport(shooterLoc);
			      
			      Random r = new Random();
			      int a = r.nextInt(100);
			      if (a <= 30) {
			          shooter.getInventory().addItem(
			        		  new ItemBuilder().material(Material.SNOW_BALL).name(getItemColor() + "Kit Switcher").amount(2).build());
			      }
	         }
	    }
	}
}