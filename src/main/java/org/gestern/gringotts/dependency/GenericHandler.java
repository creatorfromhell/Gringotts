package org.gestern.gringotts.dependency;

import org.bukkit.plugin.Plugin;

/**
 * The type Generic handler.
 */
public class GenericHandler implements DependencyHandler {

    private final Plugin plugin;

    /**
     * Instantiates a new Generic handler.
     *
     * @param plugin the plugin
     */
    public GenericHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean enabled() {
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean exists() {
        return plugin != null;
    }
}
