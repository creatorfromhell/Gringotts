package org.gestern.gringotts.dependency;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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
import static org.gestern.gringotts.Permissions.CREATEVAULT_WORLDGUARD;

/**
 * The type World guard handler.
 */
public abstract class WorldGuardHandler implements DependencyHandler, AccountHolderProvider {
    /**
     * Gets world guard handler.
     *
     * @param plugin the plugin
     * @return the world guard handler
     */
    public static WorldGuardHandler getWorldGuardHandler(Plugin plugin) {
        if (plugin instanceof WorldGuardPlugin) {
            return new ValidWorldGuardHandler((WorldGuardPlugin) plugin);
        } else {
            return new InvalidWorldGuardHandler();
        }
    }
}

/**
 * The type Invalid world guard handler.
 */
class InvalidWorldGuardHandler extends WorldGuardHandler {
    @Override
    public AccountHolder getAccountHolder(String id) {
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
 * The type Valid world guard handler.
 */
class ValidWorldGuardHandler extends WorldGuardHandler {

    private WorldGuardPlugin plugin;

    /**
     * Instantiates a new Valid world guard handler.
     *
     * @param plugin the plugin
     */
    public ValidWorldGuardHandler(WorldGuardPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new WorldGuardListener(), Gringotts.getInstance());
        Gringotts.getInstance().registerAccountHolderProvider("worldguard", this);

    }


    @Override
    public boolean enabled() {
        if (plugin != null) {
            return plugin.isEnabled();
        } else return false;
    }

    @Override
    public boolean exists() {
        return plugin != null;
    }


    @Override
    public WorldGuardAccountHolder getAccountHolder(String id) {
        // FIXME use something more robust than - as world-id delimiter
        // try explicit world+id first
        String[] parts = id.split("-", 2);
        if (parts.length == 2) {
            WorldGuardAccountHolder wgah = getAccountHolder(parts[0], parts[1]);

            if (wgah != null) {
                return wgah;
            }
        }

        // try bare id in all worlds
        WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
        for (World world : Bukkit.getWorlds()) {

            RegionManager worldManager = worldguardPlatform.getRegionContainer().get(new BukkitWorld(world));

            if (worldManager != null && worldManager.hasRegion(id)) {
                ProtectedRegion region = worldManager.getRegion(id);

                return new WorldGuardAccountHolder(world.getName(), region);
            }
        }

        return null;
    }

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     *
     * @param uuid id of account holder
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

    /**
     * Get account holder for known world and region id.
     *
     * @param world name of world
     * @param id    worldguard region id
     * @return account holder for the region
     */
    public WorldGuardAccountHolder getAccountHolder(String world, String id) {
        World w = Bukkit.getWorld(world);

        if (w == null) {
            return null;
        }

        WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
        RegionManager worldManager = worldguardPlatform.getRegionContainer().get(new BukkitWorld(w));

        if (worldManager == null) {
            return null;
        }

        if (worldManager.hasRegion(id)) {
            ProtectedRegion region = worldManager.getRegion(id);

            return new WorldGuardAccountHolder(world, region);
        }

        return null;
    }

    /**
     * The type World guard listener.
     */
    public class WorldGuardListener implements Listener {

        /**
         * Vault created.
         *
         * @param event the event
         */
        @EventHandler
        public void vaultCreated(PlayerVaultCreationEvent event) {
            // some listener already claimed this event
            if (event.isValid()) return;

            if (event.getType() == Type.REGION) {
                Player player = event.getCause().getPlayer();
                if (!CREATEVAULT_WORLDGUARD.allowed(player)) {
                    player.sendMessage(LANG.plugin_worldguard_noVaultPerm);

                    return;
                }

                String regionId = event.getCause().getLine(2);
                String[] regionComponents = regionId.split("-", 1);

                WorldGuardAccountHolder owner;
                if (regionComponents.length == 1) {
                    // try to guess the world
                    owner = getAccountHolder(regionComponents[0]);
                } else {
                    String world = regionComponents[0];
                    String id = regionComponents[1];
                    owner = getAccountHolder(world, id);
                }

                if (owner != null && (owner.region.hasMembersOrOwners() || CREATEVAULT_ADMIN.allowed(player))) {
                    DefaultDomain regionOwners = owner.region.getOwners();
                    if (regionOwners.contains(player.getName())) {
                        event.setOwner(owner);
                        event.setValid(true);
                    }
                }
            }
        }
    }
}

/**
 * The type World guard account holder.
 */
class WorldGuardAccountHolder implements AccountHolder {

    /**
     * The World.
     */
    final String world;
    /**
     * The Region.
     */
    final ProtectedRegion region;

    /**
     * Instantiates a new World guard account holder.
     *
     * @param world  the world
     * @param region the region
     */
    public WorldGuardAccountHolder(String world, ProtectedRegion region) {
        this.world = world;
        this.region = region;
    }

    @Override
    public String getName() {
        return region.getId();
    }

    @Override
    public void sendMessage(String message) {
        //Send the message to owners.
        for (UUID uuid : region.getOwners().getUniqueIds()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                player.sendMessage(message);
            }
        }

        //Send the message to members.
        for (UUID uuid : region.getMembers().getUniqueIds()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public String getType() {
        return "worldguard";
    }

    @Override
    public String getId() {
        return world + "-" + region.getId();
    }
}
