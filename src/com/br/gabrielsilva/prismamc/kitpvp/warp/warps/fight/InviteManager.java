package com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InviteManager {
	
	private UUID uuid;
	private InviteType tipo;
	private CustomManager customInv;
	private Long invited;
	
	public InviteManager(UUID uuid, InviteType convite) {
		this.uuid = uuid;
		this.tipo = convite;
		this.customInv = null;
		this.invited = System.currentTimeMillis();
	}
	
	public InviteManager(UUID uuid, InviteType convite, CustomManager CM) {
		this.uuid = uuid;
		this.tipo = convite;
		this.customInv = CM;
		this.invited = System.currentTimeMillis();
	}
	
	public boolean isValid() {
		if (invited + TimeUnit.SECONDS.toMillis(7) > System.currentTimeMillis()) {
			return true;
		}
		return false;
	}
	
	public enum InviteType {
		NORMAL, CUSTOM, SERIES;
	}
}