package com.br.gabrielsilva.prismamc.kitpvp.events.fight;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.br.gabrielsilva.prismamc.kitpvp.warp.warps.fight.CustomManager;

@Getter
public class PlayerFightStartEvent extends Event {

    @Getter
    public static final HandlerList handlerList = new HandlerList();

    private Player[] players;
    private CustomManager customManager;
    
    public PlayerFightStartEvent(Player player1, Player player2) {
        this.players = new Player[] { player1, player2 };
        this.customManager = null;
    }
    
    public PlayerFightStartEvent(Player player1, Player player2, CustomManager customManager) {
        this.players = new Player[] { player1, player2 };
        this.customManager = customManager;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}