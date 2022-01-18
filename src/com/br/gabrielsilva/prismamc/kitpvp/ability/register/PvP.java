package com.br.gabrielsilva.prismamc.kitpvp.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class PvP extends Kit {

	public PvP() {
		setNome(getClass().getSimpleName());
		
		setUsarInvencibilidade(true);
		
		ItemStack icone = new ItemBuilder().
				material(Material.STONE_SWORD).
				durability(0).
				amount(1).addEnchant(Enchantment.DAMAGE_ALL)
				.build();
				
				setIcone(icone);
				
				setPreço(0);
				setCooldownSegundos(0);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "Kit padrao com espada afiada 1");
				setDescrição(indiob);
	}	
}