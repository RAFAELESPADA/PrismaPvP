package com.br.gabrielsilva.prismamc.kitpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.gabrielsilva.prismamc.commons.core.data.category.DataCategory;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.utils.string.PluginMessages;
import com.br.gabrielsilva.prismamc.commons.custompackets.BukkitClient;
import com.br.gabrielsilva.prismamc.commons.custompackets.bungee.packets.PacketBungeeUpdateField;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.manager.combatlog.CombatLogManager;
import com.br.gabrielsilva.prismamc.kitpvp.manager.combatlog.CombatLogManager.CombatLog;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;
import com.br.gabrielsilva.prismamc.kitpvp.utils.PvPMessages;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class GameListener implements Listener {

	@EventHandler(priority=EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		event.getDrops().clear();
		
		Player morreu = event.getEntity(),
				matou = getKiller(morreu);
	
		final Warp warpPlayer = GamerManager.getGamer(morreu.getUniqueId()).getWarp();
		
		if (!warpPlayer.getName().equalsIgnoreCase("1v1")) {
			PlayerAPI.dropItemsPvP(morreu, morreu.getLocation());
			morreu.spigot().respawn();
			
			if (matou == morreu) {
				matou.sendMessage("§cVocê não pode se matar.");
				handleStats(morreu, null, warpPlayer);
				morreu.spigot().respawn();
				return;
			}
			
			if (matou == null) {
				morreu.sendMessage(PvPMessages.VOCÊ_MORREU);
			} else {
				morreu.sendMessage(PvPMessages.VOCÊ_MORREU_PARA.replace("%nick%", matou.getName()));
				
				if (warpPlayer.getName().equalsIgnoreCase("FPS")) {
				   	matou.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
				   	matou.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
				   	matou.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
				   	matou.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
				}
			}
			
			handleStats(morreu, matou, warpPlayer);
		} else {
			morreu.spigot().respawn();
		}
	}

	public Player getKiller(Player morreu) {
		if (morreu.getKiller() instanceof Player) {
			morreu.getKiller().playSound(morreu.getKiller().getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            CombatLogManager.removeCombatLog(morreu);
            CombatLogManager.removeCombatLog(morreu.getKiller());
			return morreu.getKiller();
		}
		
        CombatLog log = CombatLogManager.getCombatLog(morreu);
        if (log.isFighting()) {
            Player killer = log.getCombatLogged();
            
            CombatLogManager.removeCombatLog(morreu);
            CombatLogManager.removeCombatLog(killer);

            if (killer != null)
                if (killer.isOnline()) {
                	killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                	return killer;
                }
        }	
		return null;
	}
	
	public static void handleStats(Player loser, Player winner, Warp warpPlayer) {
		BukkitPlayer bukkitPlayerLoser = BukkitMain.getManager().getDataManager().getBukkitPlayer(loser.getUniqueId());
		bukkitPlayerLoser.getData(DataType.PVP_DEATHS).add();
		
		if (winner != null) {
		    checkKillStreakLose(bukkitPlayerLoser.getInt(DataType.PVP_KILLSTREAK), winner.getName(), loser.getName());
		}
		
		bukkitPlayerLoser.getData(DataType.PVP_KILLSTREAK).setValue(0);
		bukkitPlayerLoser.getData(DataType.COINS).remove(PlayerAPI.DEATH_COINS);
		bukkitPlayerLoser.removeXP(PlayerAPI.DEATH_XP);
		
		bukkitPlayerLoser.getDataHandler().updateValues(DataCategory.KITPVP, DataType.PVP_KILLSTREAK, DataType.PVP_DEATHS);
		bukkitPlayerLoser.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, DataType.COINS, DataType.XP);
		
		if (winner == null) {
			return;
		}
		
		//	
		BukkitPlayer bukkitPlayerWinner = BukkitMain.getManager().getDataManager().getBukkitPlayer(winner.getUniqueId());
		
		winner.sendMessage("§aVocê matou " + loser.getName());
		
		final int xp = PlayerAPI.getXPKill(winner, loser);
		winner.sendMessage("§6+ " + PlayerAPI.KILL_COINS + " moedas");
	
		bukkitPlayerWinner.getData(DataType.COINS).add(PlayerAPI.KILL_COINS);
		bukkitPlayerWinner.addXP(xp);
		bukkitPlayerWinner.getData(DataType.PVP_KILLS).add();
		
		int atualKillStreak = bukkitPlayerWinner.getData(DataType.PVP_KILLSTREAK).getInt() + 1;
		bukkitPlayerWinner.getData(DataType.PVP_KILLSTREAK).setValue(atualKillStreak);
		if (atualKillStreak > bukkitPlayerWinner.getData(DataType.PVP_MAXKILLSTREAK).getInt()) {
			bukkitPlayerWinner.getData(DataType.PVP_MAXKILLSTREAK).setValue(atualKillStreak);
		}
		
		bukkitPlayerWinner.getDataHandler().updateValues(DataCategory.KITPVP, DataType.PVP_KILLSTREAK, DataType.PVP_MAXKILLSTREAK, DataType.PVP_KILLS);
		bukkitPlayerWinner.getDataHandler().updateValues(DataCategory.PRISMA_PLAYER, DataType.COINS, DataType.XP);
		
		checkKillStreakWin(winner.getName(), atualKillStreak);
		
		PrismaPvP.getInstance().getScoreBoardManager().updateScoreboard(winner, warpPlayer);
		
		handleClanElo(winner, loser, 
				bukkitPlayerWinner.getString(DataType.CLAN), bukkitPlayerLoser.getString(DataType.CLAN), bukkitPlayerWinner.getNick());
	}
	
	public static void handleClanElo(Player winner, Player loser, String clanWin, String clanLoser, String nick) {
		if (!clanWin.equalsIgnoreCase("Nenhum")) {
			if (!clanLoser.equalsIgnoreCase(clanWin)) {
				BukkitClient.sendPacket(winner, new PacketBungeeUpdateField(nick, "Clan", "AddElo", clanWin, "10"));

				winner.sendMessage(PluginMessages.CLAN_WIN_ELO.replace("%quantia%", "10"));
			}
		}
		
		if (!clanLoser.equalsIgnoreCase("Nenhum")) {
			if (!clanLoser.equalsIgnoreCase(clanWin)) {
				BukkitClient.sendPacket(winner, new PacketBungeeUpdateField(nick, "Clan", "RemoveElo", clanLoser, "5"));
				if (loser != null && loser.isOnline()) {
					loser.sendMessage(PluginMessages.CLAN_LOSE_ELO.replace("%quantia%", "5"));
				}
			}
		}
	}

	public static void checkKillStreakWin(String nick, int killstreak) {
        if (killstreak >= 10 && killstreak % 10 == 0) {
        	Bukkit.broadcastMessage(PluginMessages.PLAYER_ALCANÇOU_KILLSTREAK.replace("%nick%", nick).replace("%valor%", "" + killstreak));
        }
    }
	
	public static void checkKillStreakLose(int winstreak, String winner, String loser) {
		if (winstreak >= 10) {
			Bukkit.broadcastMessage(PluginMessages.PLAYER_PERDEU_KILLSTREAK.replace("%perdedor%", loser).
					replace("%matou%", winner).replace("%valor%", "" + winstreak));
		}
	}
}