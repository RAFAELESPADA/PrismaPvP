package com.br.gabrielsilva.prismamc.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.events.PlayerDamagePlayerEvent;
import com.br.gabrielsilva.prismamc.kitpvp.manager.combatlog.CombatLogManager;
import com.br.gabrielsilva.prismamc.kitpvp.manager.combatlog.CombatLogManager.CombatLog;

public class CombatLogListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(PlayerDamagePlayerEvent e) {
	    Player damager = e.getPlayer();
	    Player damaged = e.getDamaged();
	    CombatLogManager.newCombatLog(damaged, damager);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent e) {
	    CombatLogManager.removeCombatLog(e.getEntity());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
	    Player p = event.getPlayer();
	    checkCombatLog(p);
	}
	
	private void checkCombatLog(Player p) {
	    CombatLog log = CombatLogManager.getCombatLog(p);
	    if (log.isFighting()) {
	        Player combatLogger = log.getCombatLogged();
	        if (combatLogger != null)
	            if (combatLogger.isOnline()) {
	            	GameListener.handleStats(p, combatLogger, PrismaPvP.getInstance().getWarpManager().getPlayerWarp(combatLogger));
	            }
	    }
	    CombatLogManager.removeCombatLog(p);
	}
}