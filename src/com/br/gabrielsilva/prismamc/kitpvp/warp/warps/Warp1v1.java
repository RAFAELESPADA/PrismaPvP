package com.br.gabrielsilva.prismamc.kitpvp.warp.warps;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.VanishManager;
import com.br.gabrielsilva.prismamc.commons.bukkit.manager.config.PluginConfig;
import com.br.gabrielsilva.prismamc.commons.core.data.category.DataCategory;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.events.fight.PlayerFightFinishEvent;
import com.br.gabrielsilva.prismamc.kitpvp.events.fight.PlayerFightStartEvent;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpJoinEvent;
import com.br.gabrielsilva.prismamc.kitpvp.events.warp.PlayerWarpQuitEvent;
import com.br.gabrielsilva.prismamc.kitpvp.listener.GameListener;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;
import com.br.gabrielsilva.prismamc.kitpvp.menus.CustomChallenge;
import com.br.gabrielsilva.prismamc.kitpvp.utils.PvPMessages;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight.CustomManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight.Fight1v1;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight.InviteManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight.InviteManager.InviteType;

public class Warp1v1 extends Warp {

	private Location firstLocation;
    private Location secondLocation;
    private static Set<Player> playersIn1v1;
    
    public static HashMap<UUID, InviteManager> invites;
	public static HashMap<UUID, CustomManager> customs;
	private ArrayList<Player> customizing;
	private HashMap<UUID, Long> inviteCooldown, delayCooldown;
	private ArrayList<Player> x1rapido;
    
    private final static String LAST_FIGHT = "lastfight.player";
	
	public Warp1v1() {
		super("1v1", Material.BLAZE_ROD);
		
		playersIn1v1 = new HashSet<>();
		invites = new HashMap<>();
		customs = new HashMap<>();
		customizing = new ArrayList<>(); 
		inviteCooldown = new HashMap<>();
		x1rapido = new ArrayList<>();
		delayCooldown = new HashMap<>();
		
		PluginConfig.createLoc(PrismaPvP.getInstance(), "1v1.pos1");
		PluginConfig.createLoc(PrismaPvP.getInstance(), "1v1.pos2");
		
		firstLocation = PluginConfig.getNewLoc(PrismaPvP.getInstance(), "1v1.pos1");
		secondLocation = PluginConfig.getNewLoc(PrismaPvP.getInstance(), "1v1.pos2");
	}
	
    @EventHandler
    public void onWarpJoin(PlayerWarpJoinEvent event) {
        Player player = event.getPlayer();
        
        if (!inWarp(player)) {
            return;
        }
        
        PlayerInventory inv = player.getInventory();
        
        inv.setHeldItemSlot(2);
		
		inv.setItem(2, 
				new ItemBuilder().material(Material.BLAZE_ROD).name("§aDesafiar jogador").build());
		
		inv.setItem(4, 
				new ItemBuilder().material(Material.INK_SACK).durability(8).name("§a1v1 Random").build());
		
		inv.setItem(6, new ItemBuilder().material(Material.ITEM_FRAME).name("§a1v1 Customizada").build());
        
		player.updateInventory();
		
		PrismaPvP.getInstance().getScoreBoardManager().createSideBar(player, this);
		
		PrismaPvP.runLater(() -> {
			if (!player.isOnline()) {
				return;
			}
			updateVanishForPlayer(player);
		}, 5);
    }
    
    private void updateVanishForPlayer(Player player) {
		for (Player onlines : Bukkit.getOnlinePlayers()) {
			 if (!VanishManager.isInvisivel(onlines)) {
	             player.showPlayer(onlines);
			 }
		}
    	
		VanishManager.updateInvisibles(player);
    }
    
    @EventHandler
    public void onWarpQuit(PlayerWarpQuitEvent event) {
        Player player = event.getPlayer();
        
        if (!inWarp(player)) {
            return;
        }
        
        cleanPlayer(player);
        if (customs.containsKey(player.getUniqueId())) {
        	customs.remove(player.getUniqueId());
        }
    }
    
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        
        Player p = (Player) event.getWhoClicked();
        
        if (!inWarp(p) || in1v1(p))
            return;
        
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onFightStart(PlayerFightStartEvent event) {
        playersIn1v1.addAll(Arrays.asList(event.getPlayers()));

        event.getPlayers()[0].sendMessage(PvPMessages.VOCÊ_ESTA_BATALHANDO_CONTRA.replace("%nick%", event.getPlayers()[1].getName()));
        event.getPlayers()[1].sendMessage(PvPMessages.VOCÊ_ESTA_BATALHANDO_CONTRA.replace("%nick%", event.getPlayers()[0].getName()));
        
        for (Player player : event.getPlayers()) {
        	 GamerManager.getGamer(player).removeProtection(player, false);
        }
        
        event.getPlayers()[0].teleport(this.firstLocation);
        event.getPlayers()[1].teleport(this.secondLocation);
        
        if (event.getPlayers()[0].hasMetadata(LAST_FIGHT)) {
        	String nick = event.getPlayers()[0].getMetadata(LAST_FIGHT).get(0).asString();
        	if (nick.equalsIgnoreCase(event.getPlayers()[1].getName())) {
        		event.getPlayers()[0].sendMessage("§cSuas estat§sticas n§o ser§o contadas, porque voc§ ja foi 1v1 com este jogador.");
        	}
        }
        
        if (event.getPlayers()[1].hasMetadata(LAST_FIGHT)) {
        	String nick = event.getPlayers()[1].getMetadata(LAST_FIGHT).get(0).asString();
        	if (nick.equalsIgnoreCase(event.getPlayers()[0].getName())) {
        		event.getPlayers()[1].sendMessage("§cSuas estat§sticas n§o ser§o contadas, porque voc§ ja foi 1v1 com este jogador.");
        	}
        }
    }

    @EventHandler
    public void onFightFinish(PlayerFightFinishEvent event) {
    	Player winner = event.getWinner();
        Player loser = event.getLoser();
        
        playersIn1v1.remove(winner);
        playersIn1v1.remove(loser);
        
        int winnerSoups = 0;
        for (ItemStack sopa : winner.getInventory().getContents()) {
             if (sopa != null && sopa.getType() != Material.AIR && sopa.getType() == Material.MUSHROOM_SOUP) {
                 winnerSoups += sopa.getAmount();
             }
        }
        
        DecimalFormat dm = new DecimalFormat("##.#");
        String health = dm.format(winner.getHealth() / 2);
        
	   	winner.sendMessage(PvPMessages.VOCÊ_GANHOU_A_BATALHA_CONTRA.replace("%nick%", loser.getName()).
		   	 replace("%coracoes%", health).replace("%sopas%", "" + winnerSoups));
			
		loser.sendMessage(PvPMessages.VOCÊ_PERDEU_A_BATALHA_CONTRA.replace("%nick%", winner.getName()).
		   	replace("%coracoes%", health).replace("%sopas%", "" + winnerSoups));
        
        if (winner.hasMetadata(LAST_FIGHT)) {
        	String nick = winner.getMetadata(LAST_FIGHT).get(0).asString();
        	if (!nick.equalsIgnoreCase(loser.getName())) {
                handleStatsOneVsOne(loser, winner);
        	}
        } else {
            handleStatsOneVsOne(loser, winner);
        }
        
        winner.setMetadata(LAST_FIGHT, new FixedMetadataValue(PrismaPvP.getInstance(), loser.getName()));
        loser.setMetadata(LAST_FIGHT, new FixedMetadataValue(PrismaPvP.getInstance(), winner.getName()));
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
        
        if (!in1v1(player)) {
        	player.teleport(getSpawnLocation());
        }
    }
    
    @EventHandler
	public void desafiar(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {	
	        Player player = event.getPlayer(),
	        		clicado = (Player) event.getRightClicked();
	        
	        if (!inWarp(player)) {
	        	return;
	        }
	        
	        if (!inWarp(clicado)) {
	        	return;
	        }
	        
        	if ((VanishManager.inAdmin(player)) || (in1v1(player))) {
        		return;
        	}
	        
			event.setCancelled(true);
        	
	        if (player.getItemInHand().getType() == Material.BLAZE_ROD) {
	        	if (in1v1(clicado)) {
	        		player.sendMessage(PvPMessages.JOGADOR_EM_BATALHA);
	        		return;
	        	}
	        	
	    		if ((inviteCooldown.containsKey(player.getUniqueId())) &&
	    				(inviteCooldown.get(player.getUniqueId()) > System.currentTimeMillis())) {
	    			return;
	    		}
	    		
	        	if (recebeuConvite(player, clicado, InviteType.NORMAL)) {
	        		cleanPlayer(player);
	        		cleanPlayer(clicado);
	        		
	    	        new Fight1v1(player, clicado, null);
	        	} else {
	        		invites.put(player.getUniqueId(), new InviteManager(clicado.getUniqueId(), InviteType.NORMAL));
	        		inviteCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 3000L);
	        		player.sendMessage(PvPMessages.VOCÊ_CONVIDOU_NORMAL.replace("%nick%", clicado.getName()));
	            	clicado.sendMessage(PvPMessages.VOCÊ_FOI_DESAFIADO_NORMAL.replace("%nick%", player.getName()));
	        	}
	        } else if (player.getItemInHand().getType() == Material.ITEM_FRAME) {
	        	if (in1v1(clicado)) {
	        		player.sendMessage(PvPMessages.JOGADOR_EM_BATALHA);
	        		return;
	        	}
	        	
	    		if ((inviteCooldown.containsKey(player.getUniqueId())) &&
	    				(inviteCooldown.get(player.getUniqueId()) > System.currentTimeMillis())) {
	    			return;
	    		}
	    		
				if (customizing.contains(clicado)) {
					player.sendMessage("§cO jogador est§ customizando uma batalha.");
					return;
				}
				
	        	customInv(player, clicado);
	        	inviteCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 3000L);
	        }
		}
    }
    
    @EventHandler
    public void fastX1(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	
    	if (!inWarp(player)) {
    		return;
    	}
    	
    	if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR && player.getItemInHand().hasItemMeta()) {
    		if (player.getItemInHand().getType() == Material.ITEM_FRAME) {
    			event.setCancelled(true);
    			return;
    		}
    		if (player.getItemInHand().getType() != Material.INK_SACK) {
    			return;
    		}
    		if (player.getItemInHand().getDurability() == 8) {
        		event.setCancelled(true);
	    		if ((delayCooldown.containsKey(player.getUniqueId())) &&
	    				(delayCooldown.get(player.getUniqueId()) > System.currentTimeMillis())) {
    				player.sendMessage(PvPMessages.AGUARDE_PARA_ENTRAR_1V1_RAPIDO);
    				return;
    			}
    			player.sendMessage(PvPMessages.PROCURANDO_PLAYER_1V1);
    			player.getInventory().setItemInHand(new ItemBuilder().material(Material.INK_SACK).name("§a1v1 Random")
    					.durability(10).build());
    			x1rapido.add(player);
    			checkX1Rapido();
    			delayCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1500L);
    		} else if (player.getItemInHand().getDurability() == 10) {
        		event.setCancelled(true);
	    		if ((delayCooldown.containsKey(player.getUniqueId())) &&
	    				(delayCooldown.get(player.getUniqueId()) > System.currentTimeMillis())) {
    				player.sendMessage(PvPMessages.AGUARDE_PARA_SAIR_1V1_RAPIDO);
    				return;
    			}
    			player.getInventory().setItemInHand(new ItemBuilder().material(Material.INK_SACK).name("§a1v1 Random")
    					.durability(8).build());
    			x1rapido.remove(player);
    			delayCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1500L);
    		}
    	}
    }
    
	public void customInv(Player player1, Player player2) {
		if (!recebeuConvite(player1, player2, InviteType.CUSTOM)) {
			new CustomChallenge("- " + player2.getName()).open(player1);
			
	        CustomManager customManager = new CustomManager(player1.getUniqueId());
	        customManager.setEfeitos(0);
	        
	        customs.put(player1.getUniqueId(), customManager);
	        customizing.add(player1);
		} else {
			new CustomChallenge("-- " + player2.getName(), customs.get(player2.getUniqueId())).open(player1);
		}
	}
    
    public static boolean in1v1(Player player) {
        return playersIn1v1.contains(player);
    }
    
	@EventHandler
	public void close(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
	    if (customizing.contains(player)) {
	    	customizing.remove(player);
	    }
	}
	
	public boolean recebeuConvite(Player p, Player d, InviteType tipo) {
		return invites.containsKey(d.getUniqueId()) &&
				invites.get(d.getUniqueId()).getUuid().equals(p.getUniqueId()) && 
				invites.get(d.getUniqueId()).getTipo().equals(tipo) &&
				invites.get(d.getUniqueId()).isValid();
	}
	
	public void checkX1Rapido() {
		if (x1rapido.size() != 1) {
			
	        Player player1 = (Player)x1rapido.get(0),
	        		player2 = (Player)x1rapido.get(1);
	        
	        x1rapido.clear();
	        new Fight1v1(player1, player2, null);
		}
	}
	
	public void cleanPlayer(Player player) {
		if (x1rapido.contains(player)) {
			x1rapido.remove(player);
	    }
		if (invites.containsKey(player.getUniqueId())) {
			invites.remove(player.getUniqueId());
		}
		if (inviteCooldown.containsKey(player.getUniqueId())) {
			inviteCooldown.remove(player.getUniqueId());
		}
		if (delayCooldown.containsKey(player.getUniqueId())) {
			delayCooldown.remove(player.getUniqueId());
		}
	}
    
	public void handleStatsOneVsOne(Player loser, Player winner) {
		BukkitPlayer bukkitPlayerLoser = BukkitMain.getManager().getDataManager().getBukkitPlayer(loser.getUniqueId());
		bukkitPlayerLoser.getData(DataType.ONEVSONE_LOSES).add();
		
		GameListener.checkKillStreakLose(bukkitPlayerLoser.getInt(DataType.PVP_KILLSTREAK), winner.getName(), loser.getName());
		
		bukkitPlayerLoser.getData(DataType.PVP_KILLSTREAK).setValue(0);
		bukkitPlayerLoser.getData(DataType.COINS).remove(PlayerAPI.DEATH_COINS);
		bukkitPlayerLoser.removeXP(PlayerAPI.DEATH_XP);

		bukkitPlayerLoser.getDataHandler().updateValues(DataCategory.KITPVP, DataType.PVP_KILLSTREAK, DataType.ONEVSONE_LOSES);
		
		bukkitPlayerLoser.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER,DataType.COINS, DataType.XP);
		
		BukkitPlayer bukkitPlayerWinner = BukkitMain.getManager().getDataManager().getBukkitPlayer(winner.getUniqueId());
		final int xp = PlayerAPI.getXPKill(winner, loser);
		winner.sendMessage("§6+ " + PlayerAPI.KILL_COINS + " moedas");
	
		bukkitPlayerWinner.getData(DataType.COINS).add(PlayerAPI.KILL_COINS);
		bukkitPlayerWinner.addXP(xp);
		bukkitPlayerWinner.getData(DataType.ONEVSONE_WINS).add();
		
		int atualKillStreak = bukkitPlayerWinner.getData(DataType.PVP_KILLSTREAK).getInt() + 1;
		bukkitPlayerWinner.getData(DataType.PVP_KILLSTREAK).setValue(atualKillStreak);
		if (atualKillStreak > bukkitPlayerWinner.getData(DataType.PVP_MAXKILLSTREAK).getInt()) {
			bukkitPlayerWinner.getData(DataType.PVP_MAXKILLSTREAK).setValue(atualKillStreak);
		}
		
		bukkitPlayerWinner.getDataHandler().updateValues(DataCategory.KITPVP, DataType.PVP_KILLSTREAK, DataType.PVP_MAXKILLSTREAK, DataType.ONEVSONE_WINS);
		bukkitPlayerWinner.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, DataType.COINS, DataType.XP);
		
		GameListener.checkKillStreakWin(winner.getName(), atualKillStreak);
		PrismaPvP.getInstance().getScoreBoardManager().updateScoreboard(winner, this);
		
		GameListener.handleClanElo(winner, loser,
				bukkitPlayerWinner.getString(DataType.CLAN), bukkitPlayerLoser.getString(DataType.CLAN), bukkitPlayerWinner.getNick());
	}
}