package org.gestern.gringotts;

import org.bukkit.entity.Player;

/**
 * The enum Permissions.
 */
public enum Permissions {
    /**
     * Usevault inventory permissions.
     */
    USEVAULT_INVENTORY("gringotts.usevault.inventory"),
    /**
     * Usevault enderchest permissions.
     */
    USEVAULT_ENDERCHEST("gringotts.usevault.enderchest"),

    /**
     * Createvault admin permissions.
     */
    CREATEVAULT_ADMIN("gringotts.createvault.admin"),
    /**
     * Createvault player permissions.
     */
    CREATEVAULT_PLAYER("gringotts.createvault.player"),
    /**
     * Createvault faction permissions.
     */
    CREATEVAULT_FACTION("gringotts.createvault.faction"),
    /**
     * Createvault town permissions.
     */
    CREATEVAULT_TOWN("gringotts.createvault.town"),
    /**
     * Createvault nation permissions.
     */
    CREATEVAULT_NATION("gringotts.createvault.nation"),
    /**
     * Createvault worldguard permissions.
     */
    CREATEVAULT_WORLDGUARD("gringotts.createvault.worldguard"),

    /**
     * Transfer permissions.
     */
    TRANSFER("gringotts.transfer"),
    /**
     * Command withdraw permissions.
     */
    COMMAND_WITHDRAW("gringotts.command.withdraw"),
    /**
     * Command deposit permissions.
     */
    COMMAND_DEPOSIT("gringotts.command.deposit");

    /**
     * The Node.
     */
    public final String node;

    Permissions(String node) {
        this.node = node;
    }

    /**
     * Check if a player has this permission.
     *
     * @param player player to check
     * @return whether given player has this permission
     */
    public boolean allowed(Player player) {
        return player.hasPermission(this.node);
    }
}
