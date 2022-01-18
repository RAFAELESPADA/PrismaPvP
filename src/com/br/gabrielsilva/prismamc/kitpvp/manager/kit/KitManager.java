package com.br.gabrielsilva.prismamc.kitpvp.manager.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.core.data.type.DataType;
import com.br.gabrielsilva.prismamc.commons.core.group.Groups;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KitManager {
	
    private static Map<Player, Kit> playerKits = new HashMap<>();
	
	@Getter @Setter
	private static HashMap<String, Kit> kits;
	
	@Getter @Setter
	private static ArrayList<String> kitsDesativados, kitsFree;
	
	@Getter @Setter
	private static boolean allKitsDesativados, allKitsFREE;
	
	static {
		kits = new HashMap<>();
		kitsDesativados = new ArrayList<>();
		kitsFree = new ArrayList<>();
		
		allKitsDesativados = false;
		allKitsFREE = false;
	}
	
	public List<Kit> getAllKits() {
		List<Kit> allKits = new ArrayList<>();
		for (Kit kit : kits.values()) {
			 allKits.add(kit);
		 }
		 return allKits;
	}
	
	public static List<Kit> getPlayerKits(Player player) {
		List<Kit> playerKits = new ArrayList<>();
		for (Kit kit : kits.values()) {
			 if (player.hasPermission("kit." + kit.getNome().toLowerCase()) || player.hasPermission("kit.all")) {
				 playerKits.add(kit);
			 }
		 }
		 return playerKits;
	}
	
	public static Kit getKitInfo(String nome) {
		return kits.get(nome.toLowerCase());
	}
	
	public static boolean hasPermissionKit(Player player, String kit, boolean msg) {
		if (allKitsFREE) {
			return true;
		}
		
		if (kit.equalsIgnoreCase("PvP")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Grandpa")) {
			return true;
		}
		if (kit.equalsIgnoreCase("Kangaroo")) {
			return true;
		}
		
		if (player.hasPermission("kit.all")) {
			return true;
		}
		
		if (player.hasPermission("kit." + kit.toLowerCase())) {
			return true;
		}
		
		/**if (getKitsFree().contains(kit.toLowerCase())) {
			return true;
		}*/
		
		if (BukkitMain.getManager().getDataManager().getBukkitPlayer(
				player.getUniqueId()).getDataHandler().getData(DataType.GRUPO).getGrupo().getNivel() >= Groups.SAPPHIRE.getNivel()) {
			return true;
		}
		
		if (msg) {
			player.sendMessage("§cVoce nao possue este Kit.");
		}
		
		return false;
	}
	
    public static boolean containsKit(Player player) {
        return playerKits.containsKey(player);
    }
    
    public static String getKitPlayer(Player player) {
        return playerKits.containsKey(player) ? playerKits.get(player).getNome() : "Nenhum";
    }
    
    public static void setKit(Player player, Kit kit) {
    	playerKits.put(player, kit);
    }

    public static void giveKit(Player player) {
    	Kit kit = getKitInfo(getKitPlayer(player));
        kit.registerPlayer(player);
        
        PlayerInventory inv = player.getInventory();
        
        if (kit.getNome().equalsIgnoreCase("PvP")) {
			inv.setItem(0, 
						new ItemBuilder().material(Material.STONE_SWORD).name("§fEspada de Pedra").addEnchant(Enchantment.DAMAGE_ALL).inquebravel().build());
        } else {
			inv.setItem(0, 
					new ItemBuilder().material(Material.STONE_SWORD).name("§fEspada de Pedra").inquebravel().build());
		}
		
		inv.setItem(8, new ItemBuilder().material(Material.COMPASS).name("§bBússola").build());
			
		if (kit.getItens() != null) {
			for (ItemStack item : kit.getItens()) {
				 player.getInventory().addItem(item);
			}
		}
		
	   	inv.setItem(13, new ItemBuilder().material(Material.BOWL).amount(64).build());
	   	inv.setItem(14, new ItemBuilder().material(Material.RED_MUSHROOM).amount(64).build());
	   	inv.setItem(15, new ItemBuilder().material(Material.BROWN_MUSHROOM).amount(64).build());
        
        for (ItemStack is : inv.getContents()) {
             if (is == null)
                inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
        }
    }

    public static void removeKit(Player player) {
        player.closeInventory();
        
        Kit kit = null;
        if (playerKits.containsKey(player)) {
        	kit = playerKits.get(player);
        	playerKits.remove(player);
        }
        
        if (kit != null) {
        	kit.unregisterPlayer(player);
        }
    }
}