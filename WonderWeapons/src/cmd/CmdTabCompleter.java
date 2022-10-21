package cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import wonderweapon.WonderWeapon;

public class CmdTabCompleter implements TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args.length) {
			case 1: {
				List<String> sol = new ArrayList<>(0);
				sol.add("help");
				sol.add("list");
				sol.add("recipe");
				if (sender.hasPermission("wonderweapons.useop"))
					sol.add("give");
				return sol;
			}
			case 2: {
				switch (args[0].toLowerCase()) {
				case "recipe": {
					List<String> sol = new ArrayList<>(0);
					String writenName = args.length>=4?args[3].toLowerCase().trim():"";
					for (WonderWeapon ww : WonderWeapon.LIST) {
						if (writenName.isEmpty()||ww.getName().startsWith(writenName)) {
							sol.add(ww.getName());
						}
					}
					return sol;
				}
				case "give": {
					return null;
				}
				}
			}
			case 3: {
				switch (args[0].toLowerCase()) {
				case "give": {
					List<String> sol = new ArrayList<>(0);
					String writenName = args.length>=4?args[3].toLowerCase().trim():"";
					for (WonderWeapon ww : WonderWeapon.LIST) {
						if (writenName.isEmpty()||ww.getName().startsWith(writenName)) {
							sol.add(ww.getName());
						}
					}
					return sol;
				}
				}
			}
		}
		return new ArrayList<>(0);
	}

}
