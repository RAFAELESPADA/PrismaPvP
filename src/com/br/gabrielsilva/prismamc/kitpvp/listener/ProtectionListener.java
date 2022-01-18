package com.br.gabrielsilva.prismamc.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;
import com.br.gabrielsilva.prismamc.kitpvp.events.PlayerDamagePlayerEvent;

public class ProtectionListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        
        Player player = (Player) event.getEntity();
        if (GamerManager.getGamer(player).isProtection()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(PlayerDamagePlayerEvent event) {
    	if ((GamerManager.getGamer(event.getDamaged()).isProtection()) || (GamerManager.getGamer(event.getPlayer()).isProtection())) {
    		event.setCancelled(true);
    	}
    }
}