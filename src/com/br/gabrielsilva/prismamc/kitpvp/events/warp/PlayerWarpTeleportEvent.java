package com.br.gabrielsilva.prismamc.kitpvp.events.warp;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

public class PlayerWarpTeleportEvent extends PlayerWarpEvent {
	
    @Getter
    public static final HandlerList handlerList = new HandlerList();

    public PlayerWarpTeleportEvent(Player who, Warp warp) {
        super(who, warp);
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}