package org.gestern.gringotts.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.gestern.gringotts.Language.LANG;

/**
 * Player commands.
 */
public class MoneyExecutor extends GringottsAbstractExecutor {
    /**
     * Registered commands.
     */
    private static final List<String> commands = Arrays.asList(
            "withdraw",
            "deposit",
            "pay"
    );

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
        if (!(sender instanceof Player)) {
            sender.sendMessage(LANG.playerOnly);

            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // same as balance
            sendBalanceMessage(eco.player(player.getUniqueId()));

            return true;
        }

        String command = args[0];

        double value = 0;
        if (args.length == 2) {
            try {
                value = Double.parseDouble(args[1]);
            } catch (NumberFormatException ignored) {
                return false;
            }

            if ("withdraw".equals(command)) {
                withdraw(player, value);

                return true;
            } else if ("deposit".equals(command)) {
                deposit(player, value);

                return true;
            }
        } else if (args.length == 3 && "pay".equals(command)) {
            // money pay <amount> <player>
            return pay(player, value, args);
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
        } else if (args.length == 2) {
            if ("pay".equals(args[0].toLowerCase())) {
                return Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .filter(name -> name.startsWith(args[1]))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }
}
