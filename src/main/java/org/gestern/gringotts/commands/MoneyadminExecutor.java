package org.gestern.gringotts.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.GringottsAccount;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.TransactionResult;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.gestern.gringotts.Language.LANG;
import static org.gestern.gringotts.api.TransactionResult.SUCCESS;

/**
 * Admin commands for managing ingame aspects.
 */
public class MoneyadminExecutor extends GringottsAbstractExecutor {
    /**
     * Registered commands.
     */
    private static final List<String> commands = Arrays.asList(
            "economy",
            "eco",
            "vaults",
            "balance",
            "b",
            "money",
            "m",
            "add",
            "remove",
            "rm"
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
        if (args.length == 0) {
            return false;
        }

        String command = args[0];

        switch (command.toLowerCase()) {
            case "economy":
            case "eco": {
                double returned = 0;

                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    double balance = this.eco.player(player.getUniqueId()).balance();

                    returned += balance;
                }

                sender.sendMessage(LANG.economy_command.replace(TAG_BALANCE, eco.currency().format(returned)));

                return true;
            }
            case "vaults": {
                int returned = 0;

                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    AccountHolder holder = this.plugin.getAccountHolderFactory().get(player);
                    GringottsAccount account = this.plugin.getAccounting().getAccount(holder);

                    returned += Gringotts.getInstance().getDao().getChests(account).size();
                }

                sender.sendMessage(LANG.vaults_command.replace(TAG_BALANCE, Integer.toString(returned)));

                return true;
            }
            case "balance":
            case "b":
            case "money":
            case "m": {
                if (args.length < 2) {
                    return false;
                }

                String targetAccountHolderStr = args[1];

                // explicit or automatic account type
                Account target;
                if (args.length == 3) {
                    target = eco.custom(args[2], targetAccountHolderStr);
                } else {
                    target = eco.account(targetAccountHolderStr);
                }

                if (!target.exists()) {
                    sendInvalidAccountMessage(sender, targetAccountHolderStr);
                    return false;
                }

                String formattedBalance = eco.currency().format(target.balance());
                String senderMessage = LANG.moneyadmin_b
                        .replace(TAG_BALANCE, formattedBalance)
                        .replace(TAG_PLAYER, targetAccountHolderStr);

                sender.sendMessage(senderMessage);

                return true;
            }
            case "add":
            case "remove":
            case "rm": {
                if (args.length >= 3) {

                    String targetAccountHolderStr = args[1];
                    Account target;
                    String amountStr;

                    if (args.length == 4) {
                        target = eco.custom(args[2], targetAccountHolderStr);
                        amountStr = args[3];
                    } else {
                        target = eco.account(targetAccountHolderStr);
                        amountStr = args[2];
                    }

                    double value;

                    try {
                        value = Double.parseDouble(amountStr);
                    } catch (NumberFormatException ignored) {
                        return false;
                    }

                    if (!target.exists()) {
                        sendInvalidAccountMessage(sender, targetAccountHolderStr);
                        return false;
                    }

                    String formatValue = eco.currency().format(value);

                    if ("add".equalsIgnoreCase(command)) {
                        TransactionResult added = target.add(value);
                        if (added == SUCCESS) {
                            String senderMessage = LANG.moneyadmin_add_sender.replace(TAG_VALUE, formatValue).replace
                                    (TAG_PLAYER, target.id());

                            sender.sendMessage(senderMessage);

                            String targetMessage = LANG.moneyadmin_add_target
                                    .replace(TAG_VALUE, formatValue);

                            target.message(targetMessage);
                        } else {
                            String errorMessage = LANG.moneyadmin_add_error.replace(TAG_VALUE, formatValue).replace
                                    (TAG_PLAYER, target.id());
                            sender.sendMessage(errorMessage);
                        }
                        return true;

                    } else if ("rm".equalsIgnoreCase(command)) {
                        TransactionResult removed = target.remove(value);
                        if (removed == SUCCESS) {
                            String senderMessage = LANG.moneyadmin_rm_sender
                                    .replace(TAG_VALUE, formatValue)
                                    .replace(TAG_PLAYER, target.id());

                            sender.sendMessage(senderMessage);

                            String targetMessage = LANG.moneyadmin_rm_target
                                    .replace(TAG_VALUE, formatValue);

                            target.message(targetMessage);
                        } else {
                            String errorMessage = LANG.moneyadmin_rm_error
                                    .replace(TAG_VALUE, formatValue)
                                    .replace(TAG_PLAYER, target.id());

                            sender.sendMessage(errorMessage);
                        }
                        return true;
                    }
                }
            }
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
            String cmd = args[0].toLowerCase();

            switch (cmd) {
                case "add":
                case "remove":
                case "rm":
                case "balance":
                case "b":
                case "money":
                case "m": {
                    return Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName)
                            .filter(name -> name.startsWith(args[1]))
                            .collect(Collectors.toList());
                }
            }
        }

        return Collections.emptyList();
    }
}
