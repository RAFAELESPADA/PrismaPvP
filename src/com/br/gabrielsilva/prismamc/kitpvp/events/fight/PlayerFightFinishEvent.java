package com.br.gabrielsilva.prismamc.kitpvp.events.fight;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PlayerFightFinishEvent extends Event {
	
    @Getter
    public static final HandlerList handlerList = new HandlerList();

    private Player winner, loser;

    public PlayerFightFinishEvent(Player winner, Player loser) {
        this.winner = winner;
        this.loser = loser;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}