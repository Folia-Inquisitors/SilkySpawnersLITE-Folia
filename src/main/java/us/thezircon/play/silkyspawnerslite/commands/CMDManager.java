package us.thezircon.play.silkyspawnerslite.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class CMDManager {

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract void perform(CommandSender sender, String args[]);

    public abstract List<String> getSubcommandArguments(Player player, String[] args);


}
