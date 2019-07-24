package org.gestern.gringotts.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Vault creation event triggered by a player.
 *
 * @author jast
 */
public class PlayerVaultCreationEvent extends VaultCreationEvent {

    private final SignChangeEvent cause;

    /**
     * Instantiates a new Player vault creation event.
     *
     * @param type  the type
     * @param cause the cause
     */
    public PlayerVaultCreationEvent(Type type, SignChangeEvent cause) {
        super(type);

        this.cause = cause;
    }

    /**
     * Gets handler list.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return VaultCreationEvent.handlers;
        // TODO ensure we can actually have superclass handle these safely
        // TODO find out what I meant by that?
    }

    /**
     * Get the player involved in creating the vault.
     *
     * @return the player involved in creating the vault
     */
    public SignChangeEvent getCause() {
        return cause;
    }

}
