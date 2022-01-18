package com.br.gabrielsilva.prismamc.kitpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.title.TitleAPI;
import com.br.gabrielsilva.prismamc.commons.core.data.category.DataCategory;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;
import com.br.gabrielsilva.prismamc.kitpvp.manager.warp.WarpManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class AccountListener implements Listener {

	private final int MEMBERS_SLOTS = 100;
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (Bukkit.getOnlinePlayers().size() >= MEMBERS_SLOTS && !player.hasPermission("kitpvp.entrar")) {
			event.disallow(Result.KICK_OTHER, "§cOs Slots para membros acabaram, compre VIP e tenha slot reservado.");
			BukkitMain.getManager().getDataManager().removeBukkitPlayerIfExists(player.getUniqueId());
			return;
		}
		
		BukkitPlayer bukkitPlayer = BukkitMain.getManager().getDataManager().getBukkitPlayer(player.getUniqueId());
		
		if (!bukkitPlayer.getDataHandler().load(DataCategory.KITPVP)) {
			event.disallow(Result.KICK_OTHER, "§cOcorreu um erro ao tentar carregar suas informa§§es...");
			BukkitMain.getManager().getDataManager().removeBukkitPlayerIfExists(player.getUniqueId());
			return;
		}
		
		bukkitPlayer.getDataHandler().sendCache(DataCategory.KITPVP);
		
		GamerManager.addGamer(player.getUniqueId());
	}
	
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        WarpManager manager = PrismaPvP.getInstance().getWarpManager();
        Warp defaultWarp = manager.getDefaultWarp();
        manager.joinWarp(player, defaultWarp);
        
        TitleAPI.enviarTitulos(player, "§b§lKOMBO", "§f§lKitPvP", 1, 1, 5);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
    	PrismaPvP.getInstance().getWarpManager().leaveWarp(event.getPlayer());
    	
        GamerManager.removeGamer(event.getPlayer().getUniqueId());
    }
}