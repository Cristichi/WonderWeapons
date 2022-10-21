package cmd;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import wonderweapon.WonderWeapon;

import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryCustom;

public class CmdExecutor implements CommandExecutor {

	private String header, version;
	private String accent, text, error;

	public CmdExecutor(String header, String accent, String text, String error, String version) {
		this.header = header;
		this.accent = accent;
		this.text = text;
		this.error = error;
		this.version = version;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			args = new String[] { "help" };
		}
		if (sender instanceof Player) {
			//Cmds for players only
			Player p = (Player) sender;
			switch (args[0]) {
			case "recipe":
				cmdRecipe(label, args, p);
				return true;
			}
		} else {
			//Cmds for the console only

		}

		//Cmds for everyone
		switch (args[0]) {
		case "version": {
			cmdVersion(sender, version);
			return true;
		}
		case "help": {
			cmdHelp(label, sender);
			return true;
		}
		case "list": {
			cmdList(sender);
			return true;
		}
		case "give":{
			cmdGive(label, args, sender);
			return true;
		}
		}

		return false;
	}

	private void cmdVersion(CommandSender sender, String version2) {
		sender.sendMessage(header + " v" + version);
	}

	private void cmdHelp(String label, CommandSender sender) {
		sender.sendMessage(header+"With Wonder Weapons you can create chaos and mass destruction. No refunds.",
				accent+"Commands:",
				accent+"/"+label+" list"+text+": Lists all available weapons.",
				accent+"/"+label+" recipe <weapon>"+text+": It shows you how to craft it. In Creative you can pick the weapon.",
				accent+"/"+label+" give <player> <weapon>"+text+": Gives a specific weapon to that player."
				);
	}

	private void cmdList(CommandSender sender) {
		String msg = header;
		for (WonderWeapon weapon : WonderWeapon.LIST) {
			msg += "\n  " + accent + weapon.getName() + text + ".";
		}
		sender.sendMessage(msg);
	}

	private void cmdGive(String label, String[] args, CommandSender sender) {
		if (!sender.hasPermission("wonderweapons.admin")) {
			sender.sendMessage(header+error+"You don't have permission to use that command. But nice try.");
			return;
		}
		if (args.length < 3) {
			sender.sendMessage(header + "Uso: " + accent + "/" + label + " " + args[0] + " <Jugador> <Item>");
			return;
		}

		Player p = null;
		Collection<? extends Player> ps = sender.getServer().getOnlinePlayers();
		for (Player player : ps) {
			if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
				p = player;
				break;
			}
		}
		if (p == null) {
			sender.sendMessage(header + "Player " + accent + p + text + " was not found online.");
			return;
		}
		String weaponName = args[2];
		for (int i = 3; i < args.length; i++) {
			weaponName += " " + args[i];
		}

		boolean ok = false;
		for (WonderWeapon weapon : WonderWeapon.LIST) {
			if (weapon.getName().equalsIgnoreCase(weaponName)) {
				p.getInventory().addItem(weapon);
				ok = true;
				weaponName = weapon.getName();
				break;
			}
		}
		if (ok) {
			if (sender.equals(p)) {
				sender.sendMessage(header + "You gave yourself one " + accent + weaponName + text + ".");
			} else {
				sender.sendMessage(header + "You gave " + accent + p.getName() + text + " one " + accent + weaponName
						+ text + ".");
				p.sendMessage(header + "You were given one " + accent + weaponName + text + " by " + accent + sender.getName() + text
						+ ".");
			}
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.7f, 0.7f);
		} else {
			sender.sendMessage(header + accent + weaponName + text + " is not an available weapon.");
		}

	}

	private void cmdRecipe(String label, String[] args, Player p) {
		if (args.length < 2) {
			p.sendMessage(new String[] {
					header + "Usage: " + accent + "/" + label + " recipe <Wonder Weapon>" + text + ".",
					header + "You can see a list of weapons using " + accent + "/" + label + " list" + text + "." });
			return;
		}
		for (int i = 2; i < args.length; i++) {
			args[1] += " " + args[i];
		}

		boolean ok = false;
		for (WonderWeapon weapon : WonderWeapon.LIST) {
			if (weapon.getName().equalsIgnoreCase(args[1])) {
				ok = true;
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.7f, 0.7f);

				CraftInventoryCustom inv = (CraftInventoryCustom) Bukkit.createInventory(p, InventoryType.WORKBENCH,
						header+weapon.getItemMeta().getDisplayName());
				Recipe recipe = weapon.getRecipe();
				if (recipe instanceof ShapedRecipe) {
					ShapedRecipe sr = (ShapedRecipe) recipe;
					String[] shape = sr.getShape();
					Map<Character, ItemStack> map = sr.getIngredientMap();

					for (int i = 0; i < shape.length; i++) {
						char[] cs = shape[i].toCharArray();
						for (int j = 0; j < cs.length; j++) {
							ItemStack is = map.getOrDefault(cs[j], null);
							if (is != null) {
								inv.setItem(i * 3 + j + 1, is);
							}
						}
					}
				} else if (recipe instanceof ShapelessRecipe) {
					ShapelessRecipe sr = (ShapelessRecipe) recipe;
					List<ItemStack> lis = sr.getIngredientList();
					for (ItemStack itemStack : lis) {
						inv.addItem(itemStack);
					}
				}
				inv.setItem(0, weapon);
				p.openInventory(inv);
			}
		}
		if (!ok) {
			p.sendMessage(header + "The weapon " + accent + args[1] + text + " doesn't exist.");
		}
	}
}
