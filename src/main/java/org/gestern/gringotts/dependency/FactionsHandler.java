package org.gestern.gringotts.dependency;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
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
import org.gestern.gringotts.event.VaultCreationEvent.Type;

import java.util.UUID;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.Permissions.CREATEVAULT_ADMIN;
import static org.gestern.gringotts.Permissions.CREATEVAULT_FACTION;
import static org.gestern.gringotts.dependency.Dependency.DEP;

/**
 * The type Factions handler.
 */
public abstract class FactionsHandler implements DependencyHandler, AccountHolderProvider {
    /**
     * Get a valid Factions handler if the plugin instance is valid. Otherwise get a fake one.
     * This is needed because HCFactions is a fork of Factions that uses some of the same classes,
     * but trying to load it causes errors.
     *
     * @param factions Factions plugin instance
     * @return a Factions handler
     */
    public static FactionsHandler getFactionsHandler(Plugin factions) {
        if (factions instanceof Factions) {
            return new ValidFactionsHandler((Factions) factions);
        } else {
            Gringotts.getInstance().getLogger().warning(
                    "Unable to load Factions handler because your version of Factions " +
                            "is not compatible with Gringotts. Factions support will not work");

            return new InvalidFactionsHandler();
        }
    }

    /**
     * Gets faction account holder.
     *
     * @param player the player
     * @return the faction account holder
     */
    public abstract FactionAccountHolder getFactionAccountHolder(Player player);

    /**
     * Gets account holder by id.
     *
     * @param id the id
     * @return the account holder by id
     */
    public abstract FactionAccountHolder getAccountHolderById(String id);
}

/**
 * The type Invalid factions handler.
 */
class InvalidFactionsHandler extends FactionsHandler {

    @Override
    public FactionAccountHolder getFactionAccountHolder(Player player) {
        return null;
    }

    @Override
    public FactionAccountHolder getAccountHolderById(String id) {
        return null;
    }

    @Override
    public AccountHolder getAccountHolder(String id) {
        return null;
    }

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param uuid@return account holder for id
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

    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public boolean exists() {
        return false;
    }
}

/**
 * The type Valid factions handler.
 */
class ValidFactionsHandler extends FactionsHandler {

    private final Factions plugin;

    /**
     * Instantiates a new Valid factions handler.
     *
     * @param plugin the plugin
     */
    public ValidFactionsHandler(Factions plugin) {
        this.plugin = plugin;

        if (plugin != null) {
            Bukkit.getPluginManager().registerEvents(new FactionsListener(), Gringotts.getInstance());
            Gringotts.getInstance().registerAccountHolderProvider("faction", this);
        }
    }

    /**
     * Get a FactionAccountHolder for the faction of which player is a member, if any.
     *
     * @param player player to get the faction for
     * @return FactionAccountHolder for the faction of which player is a member, if any. null otherwise.
     */
    @Override
    public FactionAccountHolder getFactionAccountHolder(Player player) {
        Faction playerFaction = MPlayer.get(player).getFaction();

        return playerFaction != null ? new FactionAccountHolder(playerFaction) : null;
    }

    /**
     * Get a FactionAccountHolder by id of the faction.
     *
     * @param id id to get the faction for
     * @return faction account holder for given id
     */
    @Override
    public FactionAccountHolder getAccountHolderById(String id) {
        Faction faction = FactionColl.get().getFixed(id);

        return faction != null ? new FactionAccountHolder(faction) : null;
    }

    @Override
    public boolean enabled() {
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean exists() {
        return plugin != null;
    }

    /**
     * Get a FactionAccountHolder based on the name of the account.
     * Valid ids for this method are either raw faction ids, or faction ids or tags prefixed with "faction-"
     * Only names beginning with "faction-" will be considered, and the rest of the string
     * can be either a faction id or a faction tag.
     *
     * @param id Name of the account.
     * @return a FactionAccountHolder based on the name of the account, if a valid faction could be found. null
     * otherwise.
     */
    @Override
    public FactionAccountHolder getAccountHolder(String id) {

        String factionId = id;
        if (id.startsWith("faction-")) {
            // requested a prefixed id, cut off the prefix!
            factionId = id.substring(8);
        }

        // first try raw id
        FactionAccountHolder owner = getAccountHolderById(factionId);
        if (owner != null) {
            return owner;
        }

        // just in case, also try the tag
        Faction faction = FactionColl.get().getFixed(id);

        if (faction != null) {
            return new FactionAccountHolder(faction);
        }

        return null;
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
 * The type Factions listener.
 */
class FactionsListener implements Listener {

    /**
     * Vault created.
     *
     * @param event the event
     */
    @EventHandler
    public void vaultCreated(PlayerVaultCreationEvent event) {
        // some listener already claimed this event
        if (event.isValid()) return;

        if (!DEP.factions.enabled()) return;

        if (event.getType() == Type.FACTION) {
            Player player = event.getCause().getPlayer();

            if (!CREATEVAULT_FACTION.allowed(player)) {
                player.sendMessage(LANG.plugin_faction_noVaultPerm);

                return;
            }

            AccountHolder owner;

            String ownername = event.getCause().getLine(2);

            if (ownername != null && ownername.length() > 0 && CREATEVAULT_ADMIN.allowed(player)) {
                // attempting to create account for named faction
                owner = Gringotts.getInstance().getAccountHolderFactory().get("faction", ownername);

                if (owner == null) {
                    return;
                }
            } else {
                owner = DEP.factions.getFactionAccountHolder(player);
            }

            if (owner == null) {
                player.sendMessage(LANG.plugin_faction_notInFaction);

                return;
            }

            event.setOwner(owner);
            event.setValid(true);
        }
    }
}

/**
 * The type Faction account holder.
 */
class FactionAccountHolder implements AccountHolder {

    private static final String TAG_FACTION = "faction";
    private final Faction owner;

    /**
     * Default ctor.
     *
     * @param owner the owner
     */
    public FactionAccountHolder(Faction owner) {
        this.owner = owner;
    }

    /**
     * Instantiates a new Faction account holder.
     *
     * @param id the id
     */
    public FactionAccountHolder(String id) {
        Faction faction = FactionColl.get().getFixed(id);

        if (faction != null) {
            this.owner = faction;
        } else {
            throw new NullPointerException("Attempted to create account holder with null faction.");
        }
    }

    @Override
    public String getName() {
        return owner.getName();
    }

    @Override
    public void sendMessage(String message) {
        owner.sendMessage(message);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((owner == null) ? 0 : owner.getId().hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        FactionAccountHolder other = (FactionAccountHolder) obj;

        if (this.owner == null) {
            return other.owner == null;
        } else {
            return this.owner.getId().equals(other.owner.getId());
        }
    }

    @Override
    public String getType() {
        return TAG_FACTION;
    }

    @Override
    public String toString() {
        return "FactionAccountHolder(" + getName() + ")";
    }

    @Override
    public String getId() {
        return owner.getId();
    }
}
