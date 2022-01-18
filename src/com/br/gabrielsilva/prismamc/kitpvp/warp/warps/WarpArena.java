package com.br.gabrielsilva.prismamc.kitpvp.warp.warps;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.bossbar.BossBarAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.VanishManager;
import com.br.gabrielsilva.prismamc.commons.bukkit.manager.config.PluginConfig;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.events.PlayerDamagePlayerEvent;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpJoinEvent;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpQuitEvent;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;
import com.br.gabrielsilva.prismamc.kitpvp.manager.kit.KitManager;
import com.br.gabrielsilva.prismamc.kitpvp.utils.PvPMessages;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class WarpArena extends Warp {

	private List<Location> spawns;
	private int lastSpawn;
	
	private static final String NOFALL_TAG = "nofall",
			NOFALL_TAG_TIME = "nofall.time";
	
	public WarpArena() {
		super("Arena", null);
		
		this.lastSpawn = -1;
		this.spawns = new ArrayList<>();
		
		for (int i = 1; i <= 10; i++) {
			 PluginConfig.createLoc(PrismaPvP.getInstance(), "arena." + i);
			 
			 spawns.add(PluginConfig.getNewLoc(PrismaPvP.getInstance(), "arena." + i));
		}
	}
	
    private Location getRandomSpawn() {
    	lastSpawn++;
    	if (lastSpawn > 9) {
    		lastSpawn = 0;
    	}
    	return spawns.get(lastSpawn);
	}
    
	@EventHandler
	public void boss(PlayerDamagePlayerEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Player hitado = event.getDamaged(),
				hitou = event.getPlayer();
		
		if (!inWarp(hitado)) {
			return;
		}
		
		if (!inWarp(hitou)) {
			return;
		}
		
		BossBarAPI.sendBossBar(hitou, hitado.getName() + " - " + KitManager.getKitPlayer(hitado), 3);
	}
	
    @EventHandler
    public void onWarpJoin(PlayerWarpJoinEvent event) {
        Player player = event.getPlayer();
        
        if (!inWarp(player)) {
            return;
        }
        
        player.teleport(getRandomSpawn());
        GamerManager.getGamer(player).removeProtection(player, false);
        
        KitManager.giveKit(player);
    }
    
    @EventHandler
    public void onWarpLeave(PlayerWarpQuitEvent event) {
        Player player = event.getPlayer();
        if (!inWarp(player)) {
            return;
        }
        KitManager.removeKit(player);
    }
    
	@EventHandler
    public void onCompass(PlayerInteractEvent event) {
		if ((event.hasItem()) && (event.getItem().getType() == Material.COMPASS) && (event.getAction() != Action.PHYSICAL)) {
	         event.setCancelled(true);
			 
	         Player player = event.getPlayer();
	         if (!inWarp(player)) {
	             return;
	         }
	         
             Player alvo = getRandomPlayer(player);
             if (alvo == null) {
            	 player.sendMessage(PvPMessages.BUSSOLA_NOT_FINDED);
            	 player.setCompassTarget(player.getWorld().getSpawnLocation());
             } else {
            	 player.sendMessage(PvPMessages.BUSSOLA_FINDED.replace("%nick%", alvo.getName()));
            	 player.setCompassTarget(alvo.getLocation());
             }
		}
	}
	
	public Player getRandomPlayer(final Player player) {
		Player target = null;
		
		for (Player inWarp : getPlayers()) {
			 if (inWarp == player) {
				 continue;
			 }
			 if (VanishManager.isInvisivel(inWarp)) {
				 continue;
			 }
			 
			 if (!inWarp(inWarp)) {
				 continue;
			 }
			 
			 if (inWarp.getLocation().distance(player.getLocation()) >= 15.0D) {
  	    		 if (target == null) {
  					 target = inWarp;
  	    		 } else {
  					 double distanciaAtual = target.getLocation().distance(player.getLocation());
  					 double novaDistancia = inWarp.getLocation().distance(player.getLocation());
  					 if (novaDistancia < distanciaAtual) {
  					     target = inWarp;
  						 if (novaDistancia <= 30) {
  							 break;
  						 }
  					 }
  	    		 }
			 }
		}
		return target;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.FALL)
			return;
	        
	    if (!(event.getEntity() instanceof Player))
	    	return;
	        
	    Player p = (Player) event.getEntity();
	    if (p.hasMetadata(NOFALL_TAG)) {
	        p.removeMetadata(NOFALL_TAG, PrismaPvP.getInstance());
	        
	        if (!p.hasMetadata(NOFALL_TAG_TIME)) {
	        	event.setCancelled(true);
	        	return;
	        }
	        
	        Long time = p.getMetadata(NOFALL_TAG_TIME).get(0).asLong();
	        if (time + 6200 > System.currentTimeMillis()) {
		        event.setCancelled(true);
	        }
	        
	        p.removeMetadata(NOFALL_TAG_TIME, PrismaPvP.getInstance());
	    }
	}
	
	@EventHandler
	public void onRealMove(PlayerMoveEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!inWarp(event.getPlayer())) {
			return;
		}
		
		Material material = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
		if (material == Material.ENDER_PORTAL_FRAME) {
			Player player = event.getPlayer();
			
            double xvel = 0.0D;
            double yvel = 3.75D;
            double zvel = 0.0D;
            player.setVelocity(new Vector(xvel, yvel, zvel));
			
			player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 6.0F, 1.0F);
			
			player.setMetadata(NOFALL_TAG, new FixedMetadataValue(PrismaPvP.getInstance(), true));
			player.setMetadata(NOFALL_TAG_TIME, new FixedMetadataValue(PrismaPvP.getInstance(), System.currentTimeMillis()));
		} else if (material == Material.IRON_BLOCK) {
			Player player = event.getPlayer();

			Vector vec = player.getLocation().getDirection().multiply(2.5).setY(1.5);
			player.setVelocity(vec);
			
			player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 6.0F, 1.0F);
			
			player.setMetadata(NOFALL_TAG, new FixedMetadataValue(PrismaPvP.getInstance(), true));
			player.setMetadata(NOFALL_TAG_TIME, new FixedMetadataValue(PrismaPvP.getInstance(), System.currentTimeMillis()));
		}
		
		material = null;
	}
}