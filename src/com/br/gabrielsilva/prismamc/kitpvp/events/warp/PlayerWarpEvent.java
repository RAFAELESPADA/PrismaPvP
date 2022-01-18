package com.br.gabrielsilva.prismamc.kitpvp.events.warp;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import com.br.gabrielsilva.prismamc.kitpvp.warp.Warp;

@Getter
public abstract class PlayerWarpEvent extends PlayerEvent {

    private Warp warp;

    public PlayerWarpEvent(Player who, Warp warp) {
        super(who);
        this.warp = warp;
    }
}