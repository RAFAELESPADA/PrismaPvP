package com.br.gabrielsilva.prismamc.kitpvp.ability.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.bukkit.api.itembuilder.ItemBuilder;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;

import net.md_5.bungee.api.ChatColor;

public class Grandpa extends Kit {

	public Grandpa() {
		setNome(getClass().getSimpleName());
		setUsarInvencibilidade(true);
			
		ItemStack icone = new ItemBuilder().
				material(Material.STICK).
				durability(0).
				amount(1)
				.build();
				
				setIcone(icone);
				
				setPreço(0);
				setCooldownSegundos(2);
		        ArrayList indiob = new ArrayList();
		/* 351 */         indiob.add(ChatColor.GRAY + "De repulsão com seu graveto");
				setDescrição(indiob);
		
		setItens(new ItemBuilder().material(Material.STICK).name(
				getItemColor() + "Kit " + getNome()).addUnsafeEnchant(Enchantment.KNOCKBACK, 3).build());
	}
}