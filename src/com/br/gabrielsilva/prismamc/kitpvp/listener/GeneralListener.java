package com.br.gabrielsilva.prismamc.kitpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.VanishManager;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.PlayerGriefEvent;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.PlayerRequestEvent;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.ScoreboardChangeEvent;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.ScoreboardChangeEvent.ChangeType;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.UpdateEvent;
import com.br.gabrielsilva.prismamc.commons.bukkit.custom.events.UpdateEvent.UpdateType;
import com.br.gabrielsilva.prismamc.commons.core.Core;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.commands.ServerCommand;
import com.br.gabrielsilva.prismamc.kitpvp.events.PlayerDamagePlayerEvent;
import com.br.gabrielsilva.prismamc.kitpvp.manager.combatlog.CombatLogManager;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;
import com.br.gabrielsilva.prismamc.kitpvp.manager.warp.WarpManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class GeneralListener implements Listener {

	public static int seconds = 0;
	
	@EventHandler
	public void onFastRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		WarpManager warpManager = PrismaPvP.getInstance().getWarpManager();
		Warp warpPlayer = warpManager.getPlayerWarp(player);
		
		if (warpPlayer.getName().equalsIgnoreCase("Arena")) {
			Warp spawn = warpManager.getDefaultWarp();
			
			event.setRespawnLocation(spawn.getSpawnLocation());
			
			PrismaPvP.runLater(() -> {
				PrismaPvP.getInstance().getWarpManager().joinWarp(player, spawn);
			}, 10);
		} else {
			event.setRespawnLocation(warpPlayer.getSpawnLocation());
			
			PrismaPvP.runLater(() -> {
				PrismaPvP.getInstance().getWarpManager().respawnOnWarp(player, warpPlayer);
			}, 10);
		}
	}
	
	
	@EventHandler
	public void onGrief(PlayerGriefEvent event) {
		event.setCancelled(false);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onRequest(PlayerRequestEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (CombatLogManager.getCombatLog(player).isFighting()) {
			event.setCancelled(true);
			return;
		}
	}
	
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        
        Player damager = null;
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile pr = (Projectile) event.getDamager();
            if (pr.getShooter() != null && pr.getShooter() instanceof Player) {
                damager = (Player) pr.getShooter();
            }
        }
        
        if (damager == null)
            return;
        
        if (event.isCancelled())
            return;
        
        PlayerDamagePlayerEvent event2 = new 
        		PlayerDamagePlayerEvent(damager, (Player) event.getEntity(), event.getDamage(), event.getFinalDamage());
        
        Bukkit.getPluginManager().callEvent(event2);
        event.setCancelled(event2.isCancelled());
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        final Item drop = event.getItemDrop();
        ItemStack item = drop.getItemStack();
        if (item.hasItemMeta()) {
            event.setCancelled(true);
        } else if (item.toString().contains("SWORD") || item.toString().equalsIgnoreCase("COMPASS") || item.toString().contains("AXE")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteractListener(PlayerInteractEvent e) {
        if ((e.getPlayer().getGameMode() != GameMode.CREATIVE)
                && (e.getAction() == Action.PHYSICAL && e.getClickedBlock() != null
                && e.getClickedBlock().getType() != Material.STONE_PLATE
                && e.getClickedBlock().getType() != Material.WOOD_PLATE)
                || (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null
                && (e.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE
                || e.getClickedBlock().getType() == Material.CHEST
                || e.getClickedBlock().getType() == Material.ENDER_CHEST))) {
            e.setCancelled(true);
        }
    }
	
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        
		if (VanishManager.inAdmin(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
        
        if (GamerManager.getGamer(event.getPlayer()).isProtection()) {
        	event.setCancelled(true);
        	return;
        }
        if (item.getItemMeta().hasDisplayName()) {
            event.setCancelled(true);
            return;
        }
        if (item.getType().toString().contains("SWORD") || item.getType().toString().contains("AXE")) {
            event.setCancelled(true);
            return;
        }
        if (item.getType().toString().contains("HELMET") || item.getType().toString().contains("CHESTPLATE")
                || item.getType().toString().contains("LEGGING") || item.getType().toString().contains("BOOTS")) {
            event.setCancelled(true);
            return;
        }
    }
	
    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == UpdateEvent.UpdateType.SEGUNDO)
        	for (World world : Bukkit.getServer().getWorlds()) {
                 for (Entity e : world.getEntitiesByClass(Item.class)) {
                      if (!(e instanceof Item))
                          continue;
                      
                      if (e.getTicksLived() >= 200) {
                    	  e.remove();
                      }
                 }
            }
    }
    
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if ((player.getGameMode().equals(GameMode.CREATIVE)) && 
				(ServerCommand.autorizados.contains(player.getUniqueId()))) {
			event.setCancelled(false);
		} else {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if ((event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) && 
				(ServerCommand.autorizados.contains(event.getPlayer().getUniqueId()))) {
			event.setCancelled(false);
		} else {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void updateServer(UpdateEvent event) {
		if (event.getType() != UpdateType.SEGUNDO) {
			return;
		}
		
		if (seconds == 5) {
			Core.getServersHandler().sendUpdateNetworkServer("kitpvp", 
					Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
			seconds = 0;
		}
		seconds++;
	}
	
    @EventHandler
    public void onExplosion(ExplosionPrimeEvent event) {
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void blockPhysics(BlockPhysicsEvent event) {
    	if (event.getBlock().getType() == Material.TNT) {
    		event.setCancelled(true);
    	}
    }
	
	@EventHandler
	public void onChangeScoreboard(ScoreboardChangeEvent event) {
		if (event.getChangeType() == ChangeType.ATIVOU) {
			PrismaPvP.getInstance().getScoreBoardManager().createSideBar(event.getPlayer(), 
					PrismaPvP.getInstance().getWarpManager().getPlayerWarp(event.getPlayer()));
		}
	}
	
	@EventHandler
	public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
	    if ((event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) || 
	    		(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)) {
	         event.setCancelled(true);
	    }
	}
	
	@EventHandler
	public void leavesDecay(LeavesDecayEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
	  if (event.getEntity() instanceof Arrow) {
	       Arrow arrow = (Arrow)event.getEntity();
	       arrow.remove();
	  }
   }
	
	@EventHandler
	public void burn(BlockBurnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void spread(BlockSpreadEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onChuva(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void ignite(BlockIgniteEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e)  {
		if (e.getLine(0).equalsIgnoreCase("[sopa]")) {
		    e.setLine(0, "");
		    e.setLine(1, "§6§lKOMBO§f§lPVP");
		    e.setLine(2, "§f§lSopas");
		    e.setLine(3, "");
		    e.getPlayer().sendMessage("§aPlaca criada com sucesso.");
		} else if (e.getLine(0).equalsIgnoreCase("[recraft]")) {
		   	e.setLine(0, "");
		    e.setLine(1, "§6§lKOMBO§f§lPVP");
		    e.setLine(2, "§f§lRecraft");
		    e.setLine(3, "");
		    e.getPlayer().sendMessage("§aPlaca criada com sucesso.");
		} else if (e.getLine(0).equalsIgnoreCase("[lavachallenge]")) {
			boolean created = false;
			
			if (e.getLine(1).equalsIgnoreCase("[facil]")) {
				e.setLine(2, "§a[Facil]");
				created = true;
			} else if (e.getLine(1).equalsIgnoreCase("[medio]")) {
				e.setLine(2, "§e[Medio]");
				created = true;
			} else if (e.getLine(1).equalsIgnoreCase("[dificil]")) {
				e.setLine(2, "§c[Dificil]");
				created = true;
			} else if (e.getLine(1).equalsIgnoreCase("[extreme]")) {
				e.setLine(2, "§4[Extreme]");
				created = true;
			} else {
			    e.getPlayer().sendMessage("§cPlaca invalida.");
			}
			
			if (created) {
				e.setLine(0, "");
				e.setLine(1, "§4§lLAVACHALLENGE");
				e.getPlayer().sendMessage("§aPlaca criada com sucesso.");
				e.setLine(3, "");
			}
		}
	}
		
	@EventHandler
	public void inv(PlayerInteractEvent e)  {
		if ((e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (e.getClickedBlock() != null) &&
				((e.getClickedBlock().getType().equals(Material.WALL_SIGN)) || (e.getClickedBlock().getType().equals(Material.SIGN_POST))))  
		{
			 Sign s = (Sign)e.getClickedBlock().getState();
			 String[] lines = s.getLines();
			 if (lines[2].contains("Recraft")) {
			     Inventory inv = e.getPlayer().getServer().createInventory(null, 9 * 3, "Recraft");
			     for (int i = 0; i < 27; i++) {
			    	  inv.setItem(i, getRecraft(i));
			     }
			     e.getPlayer().openInventory(inv);
			 } else if (lines[2].contains("Sopa")) {
			  	 Inventory inv = e.getPlayer().getServer().createInventory(null, 9 * 4, "Sopas");
			 	 for (ItemStack i : inv.getContents()) {
			 		  if (i == null)
			 		      inv.setItem(inv.firstEmpty(), new ItemStack(Material.MUSHROOM_SOUP));
			 	 }
			 	 e.getPlayer().openInventory(inv);
			 }
		}
	}
	
	private ItemStack getRecraft(int valor) {
		if (valor <= 8) {
			return new ItemBuilder().material(Material.BOWL).amount(64).build();
		} else if ((valor > 8) && (valor <= 17)) {
			return new ItemBuilder().material(Material.RED_MUSHROOM).amount(64).build();
	    } else if (valor > 17) {
			return new ItemBuilder().material(Material.BROWN_MUSHROOM).amount(64).build();
	    }
	    return null;
	}
}