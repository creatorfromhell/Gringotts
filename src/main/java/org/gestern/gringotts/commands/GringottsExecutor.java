package org.gestern.gringotts.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.gestern.gringotts.Language.LANG;

/**
 * Administrative commands not related to ingame money.
 */
public class GringottsExecutor extends GringottsAbstractExecutor {
    /**
     * Registered commands.
     */
    private static final List<String> commands = Collections.singletonList("reload");

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender       Source of the command
     * @param cmd          Command which was executed
     * @param commandLabel Alias of the command which was used
     * @param args         Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command cmd,
                             @NotNull String commandLabel,
                             @NotNull String[] args) {
        if (args.length >= 1 && "reload".equalsIgnoreCase(args[0])) {
            plugin.reloadConfig();

            sender.sendMessage(LANG.reload);

            return true;
        }

        return false;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {
        if (args.length == 1) {
            return commands.stream().filter(cmd -> cmd.startsWith(args[0])).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
