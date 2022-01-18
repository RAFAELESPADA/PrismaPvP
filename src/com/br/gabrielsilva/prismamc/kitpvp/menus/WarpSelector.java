package com.br.gabrielsilva.prismamc.kitpvp.menus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.MenuInventory;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.MenuItem;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.manager.warp.WarpManager;
import com.br.gabrielsilva.prismamc.kitpvp.utils.PvPMessages;
import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class WarpSelector extends MenuInventory {

	public WarpSelector() {
		super("Warps", 1);
		
		WarpManager manager = PrismaPvP.getInstance().getWarpManager();
		
	    int slot = 0;
	    List<Warp> warpList = new ArrayList<>(manager.getWarps());
	    
	    warpList.sort(Comparator.comparing(Warp::getId));
	    
	    MenuItem menuItem;
	    ItemBuilder itemBuilder;
	    
	    for (Warp warp : manager.getWarps()) {
	    	 if (warp.getMaterial() == null) {
	    		 continue;
	    	 }
	    	 itemBuilder = new ItemBuilder().material(warp.getMaterial()).name("§a" + warp.getName()).lore(new String[] {"§e" + warp.getPlayers().size() + " jogando agora"});
	    	
	         menuItem = new MenuItem(itemBuilder.build(), (player, inventory, clickType, itemStack, i) -> {
	        	 player.closeInventory();
	        	 
	        	 player.sendMessage(PvPMessages.VOCÊ_FOI_PARA_A_WARP.replace("%warp%", warp.getName().toUpperCase()));
	        	 
	        	 PrismaPvP.getInstance().getWarpManager().joinWarp(player, warp);
	         });
	         
	    	 setItem(slot++, menuItem);
	    }
	}
}
