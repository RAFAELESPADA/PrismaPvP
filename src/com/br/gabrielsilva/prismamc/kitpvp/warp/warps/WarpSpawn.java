package com.br.gabrielsilva.prismamc.kitpvp.warp.warps;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.server.ServerAPI;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpJoinEvent;
import com.br.gabrielsilva.prismamc.kitpvp.menus.KitSelector;
import com.br.gabrielsilva.prismamc.kitpvp.menus.WarpSelector;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class WarpSpawn extends Warp {

	public WarpSpawn() {
		super("Spawn", null);
	}

    @EventHandler
    public void onWarpJoin(PlayerWarpJoinEvent event) {
        Player player = event.getPlayer();
        
        if (!inWarp(player)) {
            return;
        }
        
        PlayerInventory inv = player.getInventory();
        
		inv.setHeldItemSlot(3);
		
		inv.setItem(2, 
				new ItemBuilder().material(Material.COMPASS).name("§aWarps").build());
		
		inv.setItem(4, 
				new ItemBuilder().material(Material.CHEST).name("§aKits").build());
		
		inv.setItem(6, 
				new ItemBuilder().material(Material.SKULL_ITEM).durability(3).headName(player.getName()).name("§ePerfil").build());
		
		player.updateInventory();
		
		PrismaPvP.getInstance().getScoreBoardManager().createSideBar(player, this);
    }
    
    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID)
            return;
        
        Player player = (Player) event.getEntity();
        if (!inWarp(player)) {
            return;
        }
        player.teleport(getSpawnLocation());
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	
        if (!inWarp(player)) {
            return;
        }
        
        if (!event.getAction().name().contains("RIGHT")) {
        	return;
        }
        
        ItemStack itemStack = player.getInventory().getItemInHand();
        if (ServerAPI.checkItem(itemStack, "§aKits")) {
            event.setCancelled(true);
        	new KitSelector(event.getPlayer()).open(event.getPlayer());
        } else if (ServerAPI.checkItem(itemStack, "§aWarps")) {
            event.setCancelled(true);
        	new WarpSelector().open(event.getPlayer());
        } else if (ServerAPI.checkItem(itemStack, "§ePerfil")) {
            event.setCancelled(true);
        	player.performCommand("account");
        }
    }
}