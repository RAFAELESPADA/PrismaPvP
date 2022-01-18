package com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.PlayerRequestEvent;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.events.fight.PlayerFightFinishEvent;
import com.br.gabrielsilva.prismamc.kitpvp.events.fight.PlayerFightStartEvent;
import com.br.gabrielsilva.prismamc.kitpvp.utils.PvPMessages;

public class Fight1v1 implements Listener {

    private Player player;
    private Player target;

    public Fight1v1(Player player, Player target, CustomManager customManager) {
        this.player = player;
        this.target = target;

        if (customManager == null) {
        	setupInventario(this.player);
        	setupInventario(this.target);
        } else {
        	setupCustomInv(this.player, customManager);
        	setupCustomInv(this.target, customManager);
        }
        
        Bukkit.getPluginManager().callEvent(new PlayerFightStartEvent(player, target));

        Bukkit.getPluginManager().registerEvents(this, PrismaPvP.getInstance());
        
        for (Player online : Bukkit.getOnlinePlayers()) {
             this.player.hidePlayer(online);
             this.target.hidePlayer(online);
        }
        
        PrismaPvP.runLater(() -> {
        	this.player.showPlayer(target);
        	this.target.showPlayer(player);
        }, 6L);
    }
    
	private void setupInventario(Player player) {
		PlayerInventory inv = player.getInventory();
		
		inv.clear();
		inv.setArmorContents(null);
	   	
		inv.setHelmet(new ItemStack(Material.IRON_HELMET));
		inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
		inv.setBoots(new ItemStack(Material.IRON_BOOTS));
		inv.setItem(0, new ItemBuilder().material(Material.DIAMOND_SWORD).inquebravel().addEnchant(Enchantment.DAMAGE_ALL).build());
		
		for (int i = 0; i < 8; i++) {
			 inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
		}
		
	    player.updateInventory();
	}
	
	private void setupCustomInv(Player p, CustomManager CM) {
		if (CM.getEfeitos() == 1) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0));
		} else if (CM.getEfeitos() == 2) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 0));
		} else if (CM.getEfeitos() == 3) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 0));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0));
		}
		
		PlayerInventory inv = p.getInventory();
		
		inv.clear();
		inv.setArmorContents(null);
		
	    if (CM.getEspada().equals(Material.WOOD_SWORD)) {
	    	inv.setItem(0, new ItemBuilder().material(Material.WOOD_SWORD).name("§fEspada de Madeira").addEnchant(Enchantment.DAMAGE_ALL).inquebravel().build());
		} else if (CM.getEspada().equals(Material.STONE_SWORD)) {
		  	inv.setItem(0, new ItemBuilder().material(Material.STONE_SWORD).name("§fEspada de Pedra").addEnchant(Enchantment.DAMAGE_ALL).inquebravel().build());
		} else if (CM.getEspada().equals(Material.IRON_SWORD)) {
			inv.setItem(0, new ItemBuilder().material(Material.IRON_SWORD).name("§fEspada de Ferro").addEnchant(Enchantment.DAMAGE_ALL).inquebravel().build());
		} else {
			inv.setItem(0, new ItemBuilder().material(Material.DIAMOND_SWORD).name("§fEspada de Diamante").addEnchant(Enchantment.DAMAGE_ALL).inquebravel().build());
		}
		if (!CM.getArmadura().equals(Material.GLASS)) {
			if (CM.getArmadura().equals(Material.LEATHER_CHESTPLATE)) {
				inv.setHelmet(new ItemStack(Material.LEATHER_HELMET));
				inv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
				inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
				inv.setBoots(new ItemStack(Material.LEATHER_BOOTS));
			} else if (CM.getArmadura().equals(Material.IRON_CHESTPLATE)) {
				inv.setHelmet(new ItemStack(Material.IRON_HELMET));
				inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
				inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
				inv.setBoots(new ItemStack(Material.IRON_BOOTS));
			} else if (CM.getArmadura().equals(Material.DIAMOND_CHESTPLATE)) {
				inv.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
				inv.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
				inv.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
				inv.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
			}
		}
		if (CM.isFullSopa()) {
	        for (ItemStack is : inv.getContents()) {
	             if (is == null)
	                inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
	        }
		} else {
	    	for (int i = 1; i < 9; i++) {
	    		 inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
	    	}
		}
		if (CM.isRecraft()) {
		   	inv.setItem(13, new ItemBuilder().material(Material.BOWL).amount(64).build());
		   	inv.setItem(14, new ItemBuilder().material(Material.RED_MUSHROOM).amount(64).build());
		   	inv.setItem(15, new ItemBuilder().material(Material.BROWN_MUSHROOM).amount(64).build());
		}
		
		p.updateInventory();
	}
	
	@EventHandler
	public void onChangeNick(PlayerRequestEvent event) {
		if (inPvP(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player recebe = (Player) event.getEntity();
            Player faz = (Player) event.getDamager();
            
            if (inPvP(faz) && inPvP(recebe))
                return;
            
            if (inPvP(faz) && !inPvP(recebe)) {
                event.setCancelled(true);
            } else if (!inPvP(faz) && inPvP(recebe)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        if (!inPvP(event.getPlayer()))
            return;
        
        Player winner;
        if (event.getPlayer() == player)
            winner = target;
        else
            winner = player;
        
        winner.sendMessage(PvPMessages.PLAYER_SAIU.replace("%nick%", event.getPlayer().getName()));
        handleDeath(event.getPlayer());
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
    	if (!inPvP(event.getEntity()))
    		return;
    	
        handleDeath(event.getEntity());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.target.hidePlayer(event.getPlayer());
        this.player.hidePlayer(event.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (inPvP(event.getPlayer())) {
            event.getItemDrop().remove();
        }
    }

    public void handleDeath(Player dead) {
        if (!inPvP(dead))
            return;
        
        Player killer;
        
        if (dead == this.player)
            killer = this.target;
        else
            killer = this.player;
        
        destroy();

        Bukkit.getPluginManager().callEvent(new PlayerFightFinishEvent(killer, dead));
        
        PrismaPvP.getInstance().getWarpManager().respawnOnWarp(killer, PrismaPvP.getInstance().getWarpManager().getWarp("1v1"));
    }

    public boolean inPvP(Player player) {
        return player == this.player || player == this.target;
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
    }
}