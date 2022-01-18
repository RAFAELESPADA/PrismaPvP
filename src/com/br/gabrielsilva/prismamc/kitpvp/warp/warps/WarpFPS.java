package com.br.gabrielsilva.prismamc.kitpvp.warp.warps;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.Gamer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpJoinEvent;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class WarpFPS extends Warp {

	public WarpFPS() {
		super("FPS", Material.GLASS);
	}

	private final double PROTECTION_DISTANCE = 4.6;
	@EventHandler
	public void onRealMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!inWarp(player)) {
            return;
        }
        Gamer gamer = GamerManager.getGamer(player);
        if (!gamer.isProtection()) {
        	return;
        }
        
		if (player.getLocation().distance(getSpawnLocation()) > PROTECTION_DISTANCE) {
			player.setFallDistance(-5);
			gamer.removeProtection(player, true);
		}
	}
	
    @EventHandler
    public void onWarpJoin(PlayerWarpJoinEvent event) {
        Player player = event.getPlayer();
        
        if (!inWarp(player)) {
            return;
        }
        
        PlayerInventory inv = player.getInventory();
        
		inv.setHeldItemSlot(0);
		
		inv.setHelmet(new ItemStack(Material.IRON_HELMET));
	   	inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
	   	inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
	   	inv.setBoots(new ItemStack(Material.IRON_BOOTS));
	   	inv.setItem(0, 
	   			new ItemBuilder().material(Material.DIAMOND_SWORD).name("§fEspada de Diamante").inquebravel().addEnchant(Enchantment.DAMAGE_ALL).build());
		
	   	inv.setItem(13, new ItemBuilder().material(Material.BOWL).amount(64).build());
	   	inv.setItem(14, new ItemBuilder().material(Material.RED_MUSHROOM).amount(64).build());
	   	inv.setItem(15, new ItemBuilder().material(Material.BROWN_MUSHROOM).amount(64).build());
        
        for (ItemStack is : inv.getContents()) {
             if (is == null)
                 inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
        }
	   	
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
        player.damage(40.0D);
    }
}