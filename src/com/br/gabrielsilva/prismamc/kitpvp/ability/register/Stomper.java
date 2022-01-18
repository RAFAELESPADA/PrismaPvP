package com.br.gabrielsilva.prismamc.kitpvp.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Stomper extends Kit {
	
	public Stomper() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(false);
		
		ItemStack icone = new ItemBuilder().
		material(Material.IRON_BOOTS).
		durability(0).
		amount(1)
		.build();
		
		setIcone(icone);
		
		setPreço(7000);
		setCooldownSegundos(0);
        ArrayList indiob = new ArrayList();
/* 351 */         indiob.add(ChatColor.GRAY + "Esmague seus inimigos");
		setDescrição(indiob);
		
		setItens(new ItemStack(Material.AIR));
	}

@EventHandler(priority=EventPriority.HIGH)
/*     */   public void onPlayerStomp(EntityDamageEvent e) {
/*  41 */     if (!(e.getEntity() instanceof Player)) {
/*  42 */       return;
/*     */     }
/*  44 */     Player p = (Player)e.getEntity();
/*  45 */     if (e.getCause() == EntityDamageEvent.DamageCause.FALL)
/*     */     {
/*  47 */       if (hasAbility(p))
/*     */       {
/*  49 */         for (Entity ent : p.getNearbyEntities(3.0D, 3.0D, 3.0D)) {
/*  50 */           if ((ent instanceof Player))
/*     */           {
/*  52 */             Player plr = (Player)ent;
/*  58 */             if (plr.isSneaking())
/*     */             {
/*  60 */               plr.damage(6.0D, p);
/*  61 */               plr.sendMessage(ChatColor.GRAY + "Você foi stompado por : " + ChatColor.AQUA + p.getName());
/*     */             }
/*     */             else
/*     */             {
	plr.damage(e.getDamage(), p);
   plr.damage(p.getFallDistance() * 1.5);
/*  66 */               plr.sendMessage(ChatColor.GRAY + "Voce foi stompado por: " + ChatColor.AQUA + p.getName());
/*     */             }
/*     */           }
/*     */         }
/*  71 */         p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
/*  72 */         e.setDamage(4.0D);
/*  73 */         return;
/*     */       }
/*     */     }
/*     */   }
}