package org.gestern.gringotts.api;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 * The interface Currency.
 */
public interface Currency {

    /**
     * Singular name of this currency.
     *
     * @return singular name of this currency
     */
    String name();

    /**
     * Plural name of this currency.
     *
     * @return plural name of this currency
     */
    String namePlural();

    /**
     * Get a formatted currency value.
     * The resulting string includes the amount as well as currency name or name of individual denominations.
     *
     * @param value value to format.
     * @return the formatted currency value.
     */
    String format(double value);

    /**
     * Format component base component [ ].
     *
     * @param vault the vault
     * @return the base component [ ]
     */
    default BaseComponent[] formatComponent(double vault) {
        return new ComponentBuilder(this.format(vault))
                .color(ChatColor.GREEN)
                .bold(true)
                .create();
    }

    /**
     * Get the amount of fractional digits supported by this currency.
     *
     * @return the amount of fractional digits supported by this currency
     */
    int fractionalDigits();
}