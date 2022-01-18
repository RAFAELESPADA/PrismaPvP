package com.br.gabrielsilva.prismamc.kitpvp.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.ClickType;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.MenuClickHandler;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.menu.MenuInventory;
import com.br.gabrielsilva.prismamc.kitpvp.utils.PvPMessages;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.Warp1v1;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight.CustomManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight.Fight1v1;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight.InviteManager;
import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight.InviteManager.InviteType;

public class CustomChallenge extends MenuInventory {

	public CustomChallenge(String nick) {
		super(nick, 4);
		
        setItem(9, new ItemBuilder().material(Material.DIAMOND_SWORD).name("§eEspada").glow().build());
        setItem(11, new ItemBuilder().material(Material.BOWL).name("§eRefill").build());
        setItem(13, new ItemBuilder().material(Material.GLASS).name("§eArmadura").build());
        setItem(15, new ItemBuilder().material(Material.POTION).name("§eEfeitos").lore(new String[] {"", "§fEfeitos:§7 Nenhum", ""}).build());
        setItem(17, new ItemBuilder().material(Material.THIN_GLASS).name("§eRecraft").lore(new String[] {"", "§fRecraft:§7 N§o", ""}).build());
        
        setItem(18, new ItemBuilder().material(Material.INK_SACK).durability(8).name("§eConfigurar espada").build(), new ChooseSwordHandler(this));
        setItem(20, new ItemBuilder().material(Material.INK_SACK).durability(8).name("§eConfigurar refill").build(), new ChooseRefill(this));
        setItem(22, new ItemBuilder().material(Material.INK_SACK).durability(8).name("§eConfigurar armadura").build(), new ChooseArmor(this));
        setItem(24, new ItemBuilder().material(Material.INK_SACK).durability(8).name("§eConfigurar efeitos").build(), new ChooseEffects(this));
        setItem(26, new ItemBuilder().material(Material.INK_SACK).durability(8).name("§eConfigurar recraft").build(), new ChooseRecraft(this));
        setItem(31, new ItemBuilder().material(Material.INK_SACK).durability(10).name("§aDesafiar").build(), new MenuClickHandler() {
			
			@Override
	        public void onClick(Player player, Inventory inventory, ClickType clickType, ItemStack item, int slot) {
				 String nick = inventory.getTitle().replace("- ", "");
				 
				 player.closeInventory();
				 
				 Player target = Bukkit.getPlayer(nick);
				 if (target == null) {
					 player.sendMessage("§cJogador offline.");
		             player.closeInventory();
		             return;
				 }
				 
				 if (Warp1v1.in1v1(target)) {
					 player.sendMessage(PvPMessages.JOGADOR_EM_BATALHA);
				 } else {
					 Warp1v1.invites.put(player.getUniqueId(), 
							 new InviteManager(target.getUniqueId(), InviteType.CUSTOM, Warp1v1.customs.get(player.getUniqueId())));
					 
					 player.sendMessage(PvPMessages.VOCÊ_CONVIDOU_CUSTOM.replace("%nick%", target.getName()));
		             target.sendMessage(PvPMessages.VOCÊ_FOI_DESAFIADO_CUSTOM.replace("%nick%", player.getName()));
				 }
			}
		});
	}
	
	public CustomChallenge(String nick, CustomManager customManager) {
		super(nick, 4);
		
        setItem(9, new ItemBuilder().material(customManager.getEspada()).name("§eEspada").glow().build());
        setItem(11, new ItemBuilder().material(customManager.isFullSopa() ? Material.MUSHROOM_SOUP : Material.BOWL).name("§eRefill").build());
        setItem(13, new ItemBuilder().material(customManager.getArmadura()).name("§eArmadura").build());
        
	    if (customManager.getEfeitos() == 0) {
	    	setItem(15, new ItemBuilder().material(Material.POTION).name("§eEfeitos").lore(new String[] {"", "§fEfeitos:§7 Nenhum", ""}).build());
		} else if (customManager.getEfeitos() == 1) {
			setItem(15, new ItemBuilder().material(Material.POTION).name("§eEfeitos").durability(8194).lore(new String[] {"", "§fEfeitos:§7 Velocidade", ""}).build());
		} else if (customManager.getEfeitos() == 2) {
			setItem(15, new ItemBuilder().material(Material.POTION).name("§eEfeitos").durability(8201).lore(new String[] {"", "§fEfeitos:§7 For§a", ""}).build());
		} else if (customManager.getEfeitos() == 3) {
			setItem(15, new ItemBuilder().material(Material.POTION).name("§eEfeitos").durability(8227).lore(new String[] {"", "§fEfeitos:§7 Velocidade e for§a", ""}).build());
		}
	    setItem(17, new ItemBuilder().material(customManager.isRecraft() ? Material.BROWN_MUSHROOM : Material.THIN_GLASS).name("§eRecraft").lore(new String[] {"", "§fRecraft: §7" + (customManager.isRecraft() ? "Sim" : "N§o"), ""}).build());
		setItem(31, new ItemBuilder().material(Material.INK_SACK).durability(10).name("§aAceitar").build(), new MenuClickHandler() {
			
			@Override
	        public void onClick(Player player, Inventory inventory, ClickType clickType, ItemStack item, int slot) {
				player.closeInventory();
				
				String nick = inventory.getTitle().replace("-- ", "");
				Player target = Bukkit.getPlayer(nick);
				if (target == null) {
					player.sendMessage("§cJogador offline.");
		            player.closeInventory();
		            return;
				}
				if (Warp1v1.in1v1(target)) {
					player.sendMessage(PvPMessages.JOGADOR_EM_BATALHA);
				} else {
					new Fight1v1(player, target, Warp1v1.customs.get(target.getUniqueId()));
				}
			}
		});
	}

	private static class ChooseArmor implements MenuClickHandler {
		
		private MenuInventory menuInventory;
		
        public ChooseArmor(MenuInventory menuInventory) {
        	this.menuInventory = menuInventory;
		}

		@Override
        public void onClick(Player player, Inventory inventory, ClickType clickType, ItemStack item, int slot) {
        	if (clickType != ClickType.LEFT) {
        		return;
        	}
        	
        	CustomManager customManager = Warp1v1.customs.get(player.getUniqueId());
			if (customManager.getArmadura().equals(Material.GLASS)) {
				customManager.setArmadura(Material.LEATHER_CHESTPLATE);
		        menuInventory.setItem(13, new ItemBuilder().material(Material.LEATHER_CHESTPLATE).name("§eArmadura").build(), new ChooseArmor(menuInventory));
			} else if (customManager.getArmadura().equals(Material.LEATHER_CHESTPLATE)) {
				customManager.setArmadura(Material.IRON_CHESTPLATE);
		        menuInventory.setItem(13, new ItemBuilder().material(Material.IRON_CHESTPLATE).name("§eArmadura").build(), new ChooseArmor(menuInventory));
			} else if (customManager.getArmadura().equals(Material.IRON_CHESTPLATE)) {
				customManager.setArmadura(Material.DIAMOND_CHESTPLATE);
		        menuInventory.setItem(13, new ItemBuilder().material(Material.DIAMOND_CHESTPLATE).name("§eArmadura").build(), new ChooseArmor(menuInventory));
			} else if (customManager.getArmadura().equals(Material.DIAMOND_CHESTPLATE)) {
				customManager.setArmadura(Material.GLASS);
		        menuInventory.setItem(13, new ItemBuilder().material(Material.GLASS).name("§eArmadura").build(), new ChooseArmor(menuInventory));
			}
		}
	}
	
	private static class ChooseEffects implements MenuClickHandler {
		
		private MenuInventory menuInventory;
		
        public ChooseEffects(MenuInventory menuInventory) {
        	this.menuInventory = menuInventory;
		}

		@Override
        public void onClick(Player player, Inventory inventory, ClickType clickType, ItemStack item, int slot) {
        	if (clickType != ClickType.LEFT) {
        		return;
        	}
        	
        	CustomManager customManager = Warp1v1.customs.get(player.getUniqueId());
        	if (customManager.getEfeitos() == 0) {
				customManager.setEfeitos(1);
				menuInventory.setItem(15, new ItemBuilder().material(Material.POTION).name("§eEfeitos").durability(8194).lore(new String[] {"", "§fEfeitos:§7 Velocidade", ""}).build(),
						new ChooseEffects(menuInventory));
			} else if (customManager.getEfeitos() == 1) {
				customManager.setEfeitos(2);
				menuInventory.setItem(15, new ItemBuilder().material(Material.POTION).name("§eEfeitos").durability(8201).lore(new String[] {"", "§fEfeitos:§7 For§a", ""}).build(),
						new ChooseEffects(menuInventory));
			} else if (customManager.getEfeitos() == 2) {
				customManager.setEfeitos(3);
				menuInventory.setItem(15, new ItemBuilder().material(Material.POTION).name("§eEfeitos").durability(8227).lore(new String[] {"", "§fEfeitos:§7 Velocidade e for§a", ""}).build(),
						new ChooseEffects(menuInventory));
			} else if (customManager.getEfeitos() == 3) {
				customManager.setEfeitos(0);
				menuInventory.setItem(15, 
						new ItemBuilder().material(Material.POTION).name("§eEfeitos").lore(new String[] {"", "§fEfeitos:§7 Nenhum", ""}).build(),
						new ChooseEffects(menuInventory));
			}
		}
	}
	
	private static class ChooseRecraft implements MenuClickHandler {
		
		private MenuInventory menuInventory;
		
        public ChooseRecraft(MenuInventory menuInventory) {
        	this.menuInventory = menuInventory;
		}

		@Override
        public void onClick(Player player, Inventory inventory, ClickType clickType, ItemStack item, int slot) {
        	if (clickType != ClickType.LEFT) {
        		return;
        	}
        	
        	CustomManager customManager = Warp1v1.customs.get(player.getUniqueId());
			if (customManager.isRecraft()) {
				customManager.setRecraft(false);
				menuInventory.setItem(17, 
						new ItemBuilder().material(Material.THIN_GLASS).name("§eRecraft").lore(new String[]{"", "§fRecraft:§7 N§o", ""}).build(),
						new ChooseRecraft(menuInventory));
			} else {
				customManager.setRecraft(true);
				menuInventory.setItem(17, 
						new ItemBuilder().material(Material.BROWN_MUSHROOM).name("§eRecraft").lore(new String[]{"", "§fRecraft:§7 Sim", ""}).build(),
						new ChooseRecraft(menuInventory));
			}
		}
	}
	
	private static class ChooseRefill implements MenuClickHandler {
		
		private MenuInventory menuInventory;
		
        public ChooseRefill(MenuInventory menuInventory) {
        	this.menuInventory = menuInventory;
		}

		@Override
        public void onClick(Player player, Inventory inventory, ClickType clickType, ItemStack item, int slot) {
        	if (clickType != ClickType.LEFT) {
        		return;
        	}
        	
        	CustomManager customManager = Warp1v1.customs.get(player.getUniqueId());
        	
        	if (customManager.isFullSopa()) {
        		customManager.setFullSopa(false);
        		menuInventory.setItem(11, new ItemBuilder().material(Material.BOWL).name("§eRefill").build(), new ChooseRefill(menuInventory));
        	} else {
        		customManager.setFullSopa(true);
        		menuInventory.setItem(11, new ItemBuilder().material(Material.MUSHROOM_SOUP).name("§eRefill").build(), new ChooseRefill(menuInventory));
        	}
		}
	}
	
	private static class ChooseSwordHandler implements MenuClickHandler {
		
		private MenuInventory menuInventory;
		
        public ChooseSwordHandler(MenuInventory menuInventory) {
        	this.menuInventory = menuInventory;
		}

		@Override
        public void onClick(Player player, Inventory inventory, ClickType clickType, ItemStack item, int slot) {
        	if (clickType != ClickType.LEFT) {
        		return;
        	}
			
        	CustomManager customManager = Warp1v1.customs.get(player.getUniqueId());
			switch (customManager.getEspada()) {
			case DIAMOND_SWORD:
				customManager.setEspada(Material.WOOD_SWORD);
				menuInventory.setItem(9, new ItemBuilder().material(Material.WOOD_SWORD).name("§eEspada").glow().build(), 
						new ChooseSwordHandler(menuInventory));
				break;
			case WOOD_SWORD:
				customManager.setEspada(Material.STONE_SWORD);
				menuInventory.setItem(9, new ItemBuilder().material(Material.STONE_SWORD).name("§eEspada").glow().build(), 
						new ChooseSwordHandler(menuInventory));
				break;
			case STONE_SWORD:
				customManager.setEspada(Material.IRON_SWORD);
				menuInventory.setItem(9, new ItemBuilder().material(Material.IRON_SWORD).name("§eEspada").glow().build(), 
						new ChooseSwordHandler(menuInventory));
				break;
			case IRON_SWORD:
				customManager.setEspada(Material.DIAMOND_SWORD);
				menuInventory.setItem(9, new ItemBuilder().material(Material.DIAMOND_SWORD).name("§eEspada").glow().build(), 
						new ChooseSwordHandler(menuInventory));
				break;
				default:
					break;
			}
        }
	}
}