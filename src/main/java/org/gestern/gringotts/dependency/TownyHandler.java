package org.gestern.gringotts.dependency;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.event.PlayerVaultCreationEvent;

import java.util.UUID;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.*;
import static org.gestern.gringotts.dependency.Dependency.DEP;
import static org.gestern.gringotts.event.VaultCreationEvent.Type;

/**
 * The type Towny handler.
 */
public abstract class TownyHandler implements DependencyHandler {
    /**
     * Get a valid towny handler if the plugin instance is valid. Otherwise get a fake one.
     * Apparently Towny needs this special treatment, or it will throw exceptions with unavailable classes.
     *
     * @param towny Towny plugin instance
     * @return a Towny handler
     */
    public static TownyHandler getTownyHandler(Plugin towny) {
        if (towny instanceof Towny) {
            return new ValidTownyHandler((Towny) towny);
        } else {
            Gringotts.getInstance().getLogger().warning("Unable to load Towny handler. Towny support will not work");
            return new InvalidTownyHandler();
        }
    }

    /**
     * Gets town account holder.
     *
     * @param player the player
     * @return the town account holder
     */
    public abstract TownyAccountHolder getTownAccountHolder(Player player);

    /**
     * Gets nation account holder.
     *
     * @param player the player
     * @return the nation account holder
     */
    public abstract TownyAccountHolder getNationAccountHolder(Player player);

    /**
     * Gets account holder by account name.
     *
     * @param name the name
     * @return the account holder by account name
     */
    public abstract TownyAccountHolder getAccountHolderByAccountName(String name);
}

/**
 * Dummy implementation of towny handler, if the plugin cannot be loaded.
 *
 * @author jast
 */
class InvalidTownyHandler extends TownyHandler {

    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public TownyAccountHolder getTownAccountHolder(Player player) {
        return null;
    }

    @Override
    public TownyAccountHolder getNationAccountHolder(Player player) {
        return null;
    }

    @Override
    public TownyAccountHolder getAccountHolderByAccountName(String name) {
        return null;
    }

}

/**
 * The type Valid towny handler.
 */
class ValidTownyHandler extends TownyHandler implements AccountHolderProvider {

    private static final String TAG_TOWN = "town";
    private static final String TAG_NATION = "nation";
    private final Towny plugin;

    /**
     * Instantiates a new Valid towny handler.
     *
     * @param plugin the plugin
     */
    public ValidTownyHandler(Towny plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new TownyListener(), Gringotts.getInstance());
        Gringotts.getInstance().registerAccountHolderProvider(TAG_TOWN, this);
        Gringotts.getInstance().registerAccountHolderProvider(TAG_NATION, this);
    }

    /**
     * Get a TownyAccountHolder for the town of which player is a resident, if any.
     *
     * @param player player to get town for
     * @return TownyAccountHolder for the town of which player is a resident, if any. null otherwise.
     */
    @Override
    public TownyAccountHolder getTownAccountHolder(Player player) {
        try {
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
            Town town = resident.getTown();

            return new TownyAccountHolder(town, TAG_TOWN);
        } catch (NotRegisteredException ignored) {
        }

        return null;
    }

    /**
     * Get a TownyAccountHolder for the nation of which player is a resident, if any.
     *
     * @param player player to get nation for
     * @return TownyAccountHolder for the nation of which player is a resident, if any. null otherwise.
     */
    @Override
    public TownyAccountHolder getNationAccountHolder(Player player) {
        try {
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
            Town town = resident.getTown();
            Nation nation = town.getNation();

            return new TownyAccountHolder(nation, TAG_NATION);
        } catch (NotRegisteredException ignored) {
        }

        return null;
    }

    /**
     * Get a TownyAccountHolder based on the name of the account.
     * Names beginning with "town-" will beget a town account holder and names beginning with "nation-"
     * a nation account holder.
     *
     * @param name Name of the account.
     * @return a TownyAccountHolder based on the name of the account
     */
    @Override
    public TownyAccountHolder getAccountHolderByAccountName(String name) {

        if (name.startsWith("town-")) {
            try {
                Town teo = TownyUniverse.getDataSource().getTown(name.substring(5));

                return new TownyAccountHolder(teo, TAG_TOWN);
            } catch (NotRegisteredException ignored) {
            }
        }

        if (name.startsWith("nation-")) {
            try {
                Nation teo = TownyUniverse.getDataSource().getNation(name.substring(7));

                return new TownyAccountHolder(teo, TAG_NATION);
            } catch (NotRegisteredException ignored) {
            }
        }

        return null;
    }


    @Override
    public boolean enabled() {
        return plugin != null;
    }

    @Override
    public boolean exists() {
        return plugin != null;
    }

    @Override
    public TownyAccountHolder getAccountHolder(String id) {
        return getAccountHolderByAccountName(id);
    }

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param uuid
     * @return account holder for id
     */
    @Override
    public AccountHolder getAccountHolder(UUID uuid) {
        return null;
    }

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param player the target player
     * @return account holder for id
     */
    @Override
    public AccountHolder getAccountHolder(OfflinePlayer player) {
        return null;
    }
}

/**
 * The type Towny listener.
 */
class TownyListener implements Listener {

    /**
     * Vault created.
     *
     * @param event the event
     */
    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid()) return;

        if (!DEP.towny.enabled()) return;

        String ownername = event.getCause().getLine(2);
        Player player = event.getCause().getPlayer();
        boolean forOther = ownername != null && ownername.length() > 0 && CREATEVAULT_ADMIN.allowed(player);

        AccountHolder owner;
        if (event.getType() == Type.TOWN) {
            if (!CREATEVAULT_TOWN.allowed(player)) {
                player.sendMessage(LANG.plugin_towny_noTownVaultPerm);

                return;
            }

            if (forOther) {
                owner = DEP.towny.getAccountHolderByAccountName("town-" + ownername);

                if (owner == null) {
                    return;
                }
            } else {
                owner = DEP.towny.getTownAccountHolder(player);
            }

            if (owner == null) {
                player.sendMessage(LANG.plugin_towny_noTownResident);

                return;
            }

            event.setOwner(owner);
            event.setValid(true);

        } else if (event.getType() == Type.NATION) {
            if (!CREATEVAULT_NATION.allowed(player)) {
                player.sendMessage(LANG.plugin_towny_noNationVaultPerm);

                return;
            }

            if (forOther) {
                owner = DEP.towny.getAccountHolderByAccountName("nation-" + ownername);

                if (owner == null) {
                    return;
                }
            } else {
                owner = DEP.towny.getNationAccountHolder(player);
            }

            if (owner == null) {
                player.sendMessage(LANG.plugin_towny_notInNation);

                return;
            }

            event.setOwner(owner);
            event.setValid(true);
        }
    }
}

/**
 * The type Towny account holder.
 */
class TownyAccountHolder implements AccountHolder {

    /**
     * The Owner.
     */
    public final TownyEconomyObject owner;
    /**
     * The Type.
     */
    public final String type;

    /**
     * Instantiates a new Towny account holder.
     *
     * @param owner the owner
     * @param type  the type
     */
    public TownyAccountHolder(TownyEconomyObject owner, String type) {
        this.owner = owner;
        this.type = type;
    }

    @Override
    public String getName() {
        return owner.getName();
    }

    /**
     * Send a message to all online players of the specified {@link Town} or {@link Nation}
     *
     * @param message to send
     */
    @Override
    public void sendMessage(String message) {
        if (owner instanceof ResidentList) {
            TownyUniverse.getOnlinePlayers((ResidentList) owner)
                    .forEach(player -> player.sendMessage(message));
        }
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getId() {
        return owner.getEconomyName();
    }

    @Override
    public String toString() {
        return "TownyAccountHolder(" + owner.getName() + ")";
    }

}

