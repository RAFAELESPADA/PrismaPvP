package com.br.gabrielsilva.prismamc.kitpvp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.br.gabrielsilva.prismamc.kitpvp.manager.gamer.GamerManager;
import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.gabrielsilva.prismamc.commons.bukkit.manager.config.PluginConfig;
import com.br.gabrielsilva.prismamc.commons.core.command.CommandClass;
import com.br.gabrielsilva.prismamc.commons.core.command.CommandFramework.Command;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.group.Groups;
import com.br.gabrielsilva.prismamc.commons.core.utils.string.StringUtils;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.manager.combatlog.CombatLogManager;
import com.br.gabrielsilva.prismamc.kitpvp.manager.warp.WarpManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.Warp1v1;

public class ServerCommand implements CommandClass {

	public static ArrayList<UUID> autorizados = new ArrayList<>();
	
	@Command(name = "pvpinfo", groupsToUse = {Groups.DONO})
	public void pvpinfo(BukkitCommandSender commandSender, String label, String[] args) {
		commandSender.sendMessage("");
		commandSender.sendMessage("§eJogadores em Warps:");
		commandSender.sendMessage("");
		
		int protecteds = 0;
		for (Player onlines : Bukkit.getOnlinePlayers()) {
			 if (GamerManager.getGamer(onlines).isProtection()) {
				 protecteds++;
			 }
		}
		
		for (Warp warps : PrismaPvP.getInstance().getWarpManager().getWarps()) {
	         List<Player> playersInWarp = warps.getPlayers();
			 List<String> newList = new ArrayList<>();
	         
	         for (Player target : playersInWarp) {
				  if (GamerManager.getGamer(target).isProtection()) {
	        	      newList.add(
	        	    		  BukkitMain.getManager().getDataManager().getBukkitPlayer(target.getUniqueId()).getData(DataType.GRUPO).getGrupo().getCor() + target.getName() + "§a(P)");
	        	 } else {
	        		 newList.add(
	        	    		  BukkitMain.getManager().getDataManager().getBukkitPlayer(target.getUniqueId()).getData(DataType.GRUPO).getGrupo().getCor() + target.getName());
	        	 }
	         }
 
	         String string = StringUtils.formatArrayToString(newList);
	         if (string.contains(",")) {
	        	 string = string.replaceAll(",", "§f,");
	         }
	         
	         commandSender.sendMessage("§e" + warps.getName() + " [" + newList.size() + "] " + string);
	     
	         newList.clear();
	         newList = null;
		}
		
		commandSender.sendMessage("");
		commandSender.sendMessage("Jogadores protegidos: §a" + protecteds);
		commandSender.sendMessage("Jogadores online: §a" + Bukkit.getOnlinePlayers().size());
		commandSender.sendMessage("");
	}
	
	@Command(name = "spawn")
	public void spawn(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		Player player = commandSender.getPlayer();
		
		if (CombatLogManager.getCombatLog(player).isFighting()) {
			player.sendMessage("§cVoce nao pode ir para o spawn em PvP!");
			return;
		}
		if (Warp1v1.in1v1(player)) {
			player.sendMessage("§cVoce nao pode ir para o spawn agora!");
			return;
		}
		
		WarpManager warpManager = PrismaPvP.getInstance().getWarpManager();
		
		if (warpManager.getPlayerWarp(player) == warpManager.getDefaultWarp()) {
			player.sendMessage("§cVocê ja está no spawn!");
			return;
		}
		
		PrismaPvP.getInstance().getWarpManager().joinWarp(player, warpManager.getDefaultWarp());
		player.sendMessage("§eVocê foi para o spawn!");
	}
	
	@Command(name = "setloc", groupsToUse= {Groups.DONO})
	public void setloc(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		if (args.length == 0) {
			sendHelp(commandSender);
			return;
		}
		Player player = commandSender.getPlayer();
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("hologramas")) { 
				if (isValidHologram(args[1])) {
					String name = args[1];
					PluginConfig.putNewLoc(PrismaPvP.getInstance(), "hologramas." + name.toLowerCase(), player);
					player.sendMessage("§aHolograma setado, e preciso reiniciar o servidor para aplicar o novo local.");
				} else {
					commandSender.sendMessage("§cHolograma inv§lido.");
				}
			} else if (args[0].equalsIgnoreCase("warp")) {
				if (isValidWarp(args[1])) {
					String name = args[1];
					if ((name.equalsIgnoreCase("pos1")) || (name.equalsIgnoreCase("pos2"))) {
						PluginConfig.putNewLoc(PrismaPvP.getInstance(), "1v1." + name.toLowerCase(), player);
						player.sendMessage("§aWarp setada.");
						return;
					}
					
					PluginConfig.putNewLoc(
							PrismaPvP.getInstance(), name.toLowerCase(), player);
					player.sendMessage("§aWarp setada.");
				} else {
					commandSender.sendMessage("§cWarp inv§lida.");
				}
			} else if (args[0].equalsIgnoreCase("arena")) {
				if (!StringUtils.isInteger(args[1])) {
					commandSender.sendMessage("§cUse: /setloc arena <1-10>");
					commandSender.sendMessage("§cUse: /setloc warp <FPS/LavaChallenge/Spawn/1v1/Pos1/Pos2>");
					return;
				}
				int spawn = Integer.valueOf(args[1]);
				
				PluginConfig.putNewLoc(
						PrismaPvP.getInstance(), "arena." + spawn, player);
				
				player.sendMessage("§aPosição do Spawn da Arena #" + spawn + " setado.");
			} else {
				sendHelp(commandSender);
			}
		} else {
			sendHelp(commandSender);
		}
	}
	
	public boolean isValidHologram(String name) {
		if (name.equalsIgnoreCase("ranking")) {
			return true;
		}
		if (name.equalsIgnoreCase("Kills")) {
			return true;
		}
		if (name.equalsIgnoreCase("Wins")) {
			return true;
		}
		return false;
	}
	
	private void sendHelp(BukkitCommandSender commandSender) {
		commandSender.sendMessage("");
		commandSender.sendMessage("§cUse: /setloc arena <1-10>");
		commandSender.sendMessage("§cUse: /setloc warp <FPS/LavaChallenge/Spawn/1v1/Pos1/Pos2>");
		commandSender.sendMessage("§cUse: /setloc hologramas <Ranking/Kills/Wins>");
		commandSender.sendMessage("");
	}
	
	@Command(name = "build", aliases= {"b"}, groupsToUse= {Groups.ADMIN})
	public void build(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		Player player = commandSender.getPlayer();
		if (autorizados.contains(player.getUniqueId())) {
			autorizados.remove(player.getUniqueId());
			player.sendMessage("§aVocê desativou o modo construção.");
		} else {
			autorizados.add(player.getUniqueId());
			player.sendMessage("§aVocê ativou o modo construção.");
		}
	}
	
	public boolean isValidWarp(String nome) {
		if (nome.equalsIgnoreCase("fps")) {
			return true;
		}
		if (nome.equalsIgnoreCase("1v1")) {
			return true;
		}
		if (nome.equalsIgnoreCase("lavachallenge")) {
			return true;
		}
		if (nome.equalsIgnoreCase("spawn")) {
			return true;
		}
		if (nome.equalsIgnoreCase("pos1")) {
			return true;
		}
		if (nome.equalsIgnoreCase("pos2")) {
			return true;
		}
		return false;
	}
}