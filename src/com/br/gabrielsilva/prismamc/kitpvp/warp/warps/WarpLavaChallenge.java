package com.br.gabrielsilva.prismamc.kitpvp.warp.warps;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.core.data.category.DataCategory;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.events.PlayerDamagePlayerEvent;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpJoinEvent;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;
import com.br.gabrielsilva.prismamc.kitpvp.utils.PvPMessages;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class WarpLavaChallenge extends Warp {

	public WarpLavaChallenge() {
		super("LavaChallenge", Material.LAVA_BUCKET);
	}

	private HashMap<UUID, Integer> lavaDamages = new HashMap<>();
	
    @EventHandler
    public void onWarpJoin(PlayerWarpJoinEvent event) {
    	Player player = event.getPlayer();
        
        if (!inWarp(player)) {
            return;
        }
        
        PlayerInventory inv = player.getInventory();
        
	   	inv.setItem(13, new ItemBuilder().material(Material.BOWL).amount(64).build());
	   	inv.setItem(14, new ItemBuilder().material(Material.RED_MUSHROOM).amount(64).build());
	   	inv.setItem(15, new ItemBuilder().material(Material.BROWN_MUSHROOM).amount(64).build());
        
        for (ItemStack is : inv.getContents()) {
             if (is == null) {
                 inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
             }
        }
       
        player.updateInventory();
      
        GamerManager.getGamer(player).removeProtection(player, false);
       
		PrismaPvP.getInstance().getScoreBoardManager().createSideBar(player, this);
        lavaDamages.put(player.getUniqueId(), 0);
    }
    
    @EventHandler
    public void onDamage(PlayerDamagePlayerEvent event) {
    	Player damaged = event.getDamaged();
    	
    	if (inWarp(damaged)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onVelocity(PlayerVelocityEvent event) {
    	if (inWarp(event.getPlayer())) {
    		event.setCancelled(true);
    	}
    }
    
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {   
	    if (!(event.getEntity() instanceof Player))
	    	return;
	        
	    Player player = (Player) event.getEntity();
	    if (inWarp(player)) {
			if (event.getCause() != DamageCause.LAVA) {
				event.setCancelled(true);
			}
	    }
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void addDamage(EntityDamageEvent event) {   
	    if (!(event.getEntity() instanceof Player))
	    	return;
	        
	    Player player = (Player) event.getEntity();
		if (event.getCause() != DamageCause.LAVA) {
			return;
		}
	    if (inWarp(player)) {
	    	if (!event.isCancelled()) {
				addLavaDamage(player);
	    	}
	    }
	}
	
	private void addLavaDamage(Player player) {
		int amount = lavaDamages.containsKey(player.getUniqueId()) ? 
				lavaDamages.get(player.getUniqueId()) : 0;
		
		lavaDamages.put(player.getUniqueId(), amount + 1);
		//Bukkit.broadcastMessage(player.getName() + " +1 hit (" + (amount + 1) +")");
	}

	@EventHandler
	public void inv(PlayerInteractEvent event)  {
		if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (event.getClickedBlock() != null) &&
				((event.getClickedBlock().getType().equals(Material.WALL_SIGN)) || (event.getClickedBlock().getType().equals(Material.SIGN_POST)))) {
			Sign s = (Sign)event.getClickedBlock().getState();
			String[] lines = s.getLines();
			
			Player player = event.getPlayer();
			if (lines[1].contains("LAVACHALLENGE")) {
				if (!inWarp(player)) {
					return;
				}
				
				int lavaDmg = lavaDamages.containsKey(player.getUniqueId()) ? lavaDamages.get(player.getUniqueId()) : 0;
				
				if (lines[2].contains("Fácil")) {
					handleCompleteLava(player, LavaDifficulty.FACIL);
					PrismaPvP.console("[LavaChallenge] " + player.getName() + 
							" completou o Nivel Facil tomando " + lavaDmg + " hits para a Lava.");
				} else if (lines[2].contains("Médio")) {
					handleCompleteLava(player, LavaDifficulty.MEDIO);
					PrismaPvP.console("[LavaChallenge] " + player.getName() + 
							" completou o Medio Facil tomando " + lavaDmg + " hits para a Lava.");
				} else if (lines[2].contains("Difícil")) {
					handleCompleteLava(player, LavaDifficulty.DIFICIL);
					PrismaPvP.console("[LavaChallenge] " + player.getName() + 
							" completou o Nivel Dificil tomando " + lavaDmg + " hits para a Lava.");
				} else if (lines[2].contains("Extreme")) {
					handleCompleteLava(player, LavaDifficulty.EXTREMO);
					PrismaPvP.console("[LavaChallenge] " + player.getName() + 
							" completou o Extremo Facil tomando " + lavaDmg + " hits para a Lava.");
				}
				
				PrismaPvP.getInstance().getWarpManager().respawnOnWarp(player, this);
			}
		}
	}
	
	public void handleCompleteLava(Player player, LavaDifficulty difficulty) {
		BukkitPlayer bukkitPlayer = BukkitMain.getManager().getDataManager().getBukkitPlayer(player.getUniqueId());
		
		switch (difficulty) {
		case FACIL:
			bukkitPlayer.getDataHandler().getData(DataType.COINS).add(10);
			bukkitPlayer.addXP(20);
			
			player.sendMessage(PvPMessages.VOCÊ_COMPLETOU_LAVA_CHALLENGE.replace("%dificuldade%", "§a§lFACIL"));
			bukkitPlayer.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, DataType.COINS, DataType.XP);
			break;
		case MEDIO:
			bukkitPlayer.getDataHandler().getData(DataType.COINS).add(20);
			bukkitPlayer.addXP(40);
			
			player.sendMessage(PvPMessages.VOCÊ_COMPLETOU_LAVA_CHALLENGE.replace("%dificuldade%", "§e§lMEDIO"));
			bukkitPlayer.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, DataType.COINS, DataType.XP);
			break;
		case DIFICIL:
			bukkitPlayer.getDataHandler().getData(DataType.COINS).add(30);
			bukkitPlayer.addXP(60);
			
			player.sendMessage(PvPMessages.VOCÊ_COMPLETOU_LAVA_CHALLENGE.replace("%dificuldade%", "§c§lDIFICIL"));
			bukkitPlayer.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, DataType.COINS, DataType.XP);
			break;
		case EXTREMO:
			bukkitPlayer.getDataHandler().getData(DataType.COINS).add(40);
			bukkitPlayer.addXP(100);
			
			player.sendMessage(PvPMessages.VOCÊ_COMPLETOU_LAVA_CHALLENGE.replace("%dificuldade%", "§4§lEXTREMO"));
			bukkitPlayer.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, DataType.COINS, DataType.XP);
			break;
		default:
			break;
		}
	}

	public enum LavaDifficulty {
		FACIL, MEDIO, DIFICIL, EXTREMO;
	}
	
	public void cleanPlayer(Player player) {
		if (lavaDamages.containsKey(player.getUniqueId())) {
			lavaDamages.remove(player.getUniqueId());
		}
	}
}