package com.br.gabrielsilva.prismamc.kitpvp.manager.kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import com.br.gabrielsilva.prismamc.commons.bukkit.BukkitMain;
import com.br.gabrielsilva.prismamc.commons.core.utils.ClassGetter;
import com.br.gabrielsilva.prismamc.kitpvp.PrismaPvP;
import com.br.gabrielsilva.prismamc.kitpvp.ability.Kit;

public class KitLoader {

	private static List<String> kitsAtivados = new ArrayList<>();
	
	public static void init() {
		List<String> kitsExistentes = new ArrayList<>();
		
		for (Class<?> classes : ClassGetter.getClassesForPackage(PrismaPvP.getInstance(),
				"com.br.gabrielsilva.prismamc.kitpvp.ability.register")) {
		
			try {
				 if (Kit.class.isAssignableFrom(classes) && (classes != Kit.class)) {
				     kitsExistentes.add(classes.getSimpleName());
				 }
			 } catch (Exception e) {}
		}
		
		for (String kit : kitsExistentes) {
			
			 boolean add = true;
			 
		     if (!BukkitMain.getManager().getConfigManager().getKitsConfig().contains("Kits." + kit + ".Preco")) {
		    	 BukkitMain.getManager().getConfigManager().getKitsConfig().set("Kits." + kit + ".Preco", 1000);
		    	 BukkitMain.getManager().getConfigManager().getKitsConfig().set("Kits." + kit + ".Cooldown", 20);
		    	 BukkitMain.getManager().getConfigManager().getKitsConfig().set("Kits." + kit + ".Icone.Material", "CHEST");
		    	 BukkitMain.getManager().getConfigManager().getKitsConfig().set("Kits." + kit + ".Icone.Durabilidade", 0);
		    	 BukkitMain.getManager().getConfigManager().getKitsConfig().set("Kits." + kit + ".Icone.Quantidade", 1);
		    	 BukkitMain.getManager().getConfigManager().getKitsConfig().set("Kits." + kit + ".Icone.Descricao", Arrays.asList("&bEdite a desc na", "&cConfig"));
		 		 BukkitMain.getManager().getConfigManager().saveKitsConfig();
		     } else {
		    	 add = true;
		    	 
		    	 Material material = null;
		    	 try {
		    		 material = 
		    			 Material.getMaterial(BukkitMain.getManager().getConfigManager().getKitsConfig().getString("Kits." + kit + ".Icone.Material"));
		    	 } catch (NullPointerException ex) {
		    		 PrismaPvP.console("Material do Kit '" + kit + "' está incorreto.");
		    		 add = false;
		    	 } finally {
		    		 if (material == null) {
		    			 add = false;
		    		 }
		    	 }
		     }
		     
		     if (add) {
		    	 kitsAtivados.add(kit);
		     } else {
		    	 PrismaPvP.console("Não foi possivel adicionar o Kit -> " + kit);
		     }
		}
		
		kitsExistentes.clear();
		PrismaPvP.console("Total de: ("+kitsAtivados.size()+") kits ativados.");
		
		addKits();
	}
	
	private static void addKits() {
		for (Class<?> classes : ClassGetter.getClassesForPackage(PrismaPvP.getInstance(), "com.br.gabrielsilva.prismamc.kitpvp.ability.register")) {
			 if (!kitsAtivados.contains(classes.getSimpleName())) {
				 continue;
			 }
			 
			 try {
				 if (Kit.class.isAssignableFrom(classes) && (classes != Kit.class)) {
					 Kit kit = (Kit) classes.newInstance();
					 
					 KitManager.getKits().put(kit.getNome().toLowerCase(), kit);
				 }
			 } catch (Exception ex) {
				 PrismaPvP.console("Ocorreu um erro ao tentar adicionar um Kit. ->" + ex.getLocalizedMessage());
			 }
		}
	}
}