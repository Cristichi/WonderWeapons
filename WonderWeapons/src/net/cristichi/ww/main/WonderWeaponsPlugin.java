package net.cristichi.ww.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.simple.parser.ParseException;

import net.cristichi.ww.AnubisHoe;
import net.cristichi.ww.MourningWall;
import net.cristichi.ww.RayBow;
import net.cristichi.ww.RayBowX2;
import net.cristichi.ww.Thundermaker;
import net.cristichi.ww.TravellingBow;
import net.cristichi.ww.WonderWeapon;
import net.cristichi.ww.updater.Updater;

public class WonderWeaponsPlugin extends JavaPlugin implements Listener {
	private PluginDescriptionFile desc = getDescription();

	public static final ChatColor MAIN_COLOR = ChatColor.BLUE;
	public static final ChatColor TEXT_COLOR = ChatColor.AQUA;
	public static final ChatColor ACCENT_COLOR = ChatColor.DARK_AQUA;
	public static final ChatColor ERROR_COLOR = ChatColor.RED;
	public final String header = MAIN_COLOR + "[Wonder Weapons] " + TEXT_COLOR;
	public final String accent = ACCENT_COLOR.toString();
	public final String text = TEXT_COLOR.toString();
	public final String error = ERROR_COLOR.toString();

	// Updater
	private static final int CurseForgeID = 297542;
	private Updater updater;
	private boolean update = false;

	/**
	 * These teams will serve the purpose of making things glow in specific colors.
	 * They get generated for this plugin using the color's name
	 */
	public static final ChatColor[] TEAM_COLORS = new ChatColor[] { ChatColor.BLUE, ChatColor.GOLD, ChatColor.WHITE,
			ChatColor.BLACK, ChatColor.DARK_PURPLE, ChatColor.RED, ChatColor.DARK_RED };
	private static final String TEAMS_PREFIX = "WW-";
	public static Map<ChatColor, Team> teams;

	// Weapons
	public static RayBow rayBow;
	public static RayBowX2 rayBowX2;
	public static TravellingBow travelingBow;
	public static MourningWall mourningWall;
	public static Thundermaker thundermaker;
	public static AnubisHoe anubisHoe;

	@Override
	public void onEnable() {
		if (checkUpdate()) {
			getServer().getConsoleSender()
					.sendMessage(header + ChatColor.GREEN
							+ "An update is available, use /ww update to update to the lastest version." + text
							+ " (Current version: v" + desc.getVersion() + " to v" + updater.getRemoteVersion() + ")");
		}

		if (teams == null) {
			teams = new HashMap<>(TEAM_COLORS.length);

			Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
			for (ChatColor color : TEAM_COLORS) {
				Team team = board.getTeam(TEAMS_PREFIX + color.name());
				if (team != null) {
					team.unregister();
				}
				team = board.registerNewTeam(TEAMS_PREFIX + color.name());
				team.setColor(color);
				teams.put(color, team);
				getLogger().log(Level.CONFIG, "Creado el equipo " + team.getDisplayName());
			}
		}

		rayBow = new RayBow(this);
		getServer().getPluginManager().registerEvents(RayBow.listener, this);
		rayBowX2 = new RayBowX2(this);
		getServer().getPluginManager().registerEvents(RayBowX2.listener, this);
		travelingBow = new TravellingBow(this);
		getServer().getPluginManager().registerEvents(TravellingBow.listener, this);
		mourningWall = new MourningWall(this);
		getServer().getPluginManager().registerEvents(MourningWall.listener, this);
		thundermaker = new Thundermaker(this);
		getServer().getPluginManager().registerEvents(Thundermaker.listener, this);
		anubisHoe = new AnubisHoe(this);
		getServer().getPluginManager().registerEvents(new AnubisHoe.AnubisHoeListener(header), this);

		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("WonderWeapons Enabled");

		getCommand("cristichiwonderweapons").setExecutor(new CmdExecutor());
		getCommand("cristichiwonderweapons").setTabCompleter(new CmdTabCompleter());

	}

	@Override
	public void onDisable() {
		teams.forEach((color, team) -> {
			team.unregister();
		});
		teams = null;
	}

	public boolean checkUpdate() {
		try {
			updater = new Updater(this, CurseForgeID, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
			update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return update;
	}

	@EventHandler
	public void abrirInventario(InventoryOpenEvent e) {
		HumanEntity p = e.getPlayer();
		if (e.getInventory().getType() == InventoryType.WORKBENCH) {
			for (WonderWeapon w : WonderWeapon.LIST) {
				p.undiscoverRecipe(w.getKeyCraft());
				p.discoverRecipe(w.getKeyCraft());
			}
		}
	}

	@EventHandler
	public void clickInventario(InventoryClickEvent e) {
		if (e.getView().getTitle().startsWith(header)) {
			if (e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)
					&& !e.getSlotType().equals(SlotType.CRAFTING)) {
				return;
			}
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		for (WonderWeapon w : WonderWeapon.LIST) {
			p.discoverRecipe(w.getKeyCraft());
		}
	}

	class CmdExecutor implements CommandExecutor {

		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if (args.length == 0) {
				args = new String[] { "help" };
			}
			if (sender instanceof Player) {
				// Cmds for players only
				Player p = (Player) sender;
				switch (args[0]) {
				case "recipe":
					cmdRecipe(label, args, p);
					return true;
				}
			} else {
				// Cmds for the console only

			}

			// Cmds for everyone
			switch (args[0]) {
			case "version": {
				cmdVersion(sender);
				return true;
			}
			case "update": {
				cmdUpdate(sender, accent, text, error);
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
			case "give": {
				cmdGive(label, args, sender);
				return true;
			}
			}

			return false;
		}

		private void cmdUpdate(CommandSender sender, String accent, String text, String error) {
			if (sender.hasPermission("wonderweapons.useop")) {
				if (checkUpdate()) {
					sender.sendMessage(header + "Updating Wonder Weapons...");
					try {
						updater = new Updater(WonderWeaponsPlugin.this, CurseForgeID,
								WonderWeaponsPlugin.this.getFile(), Updater.UpdateType.DEFAULT, true);
						updater.getResult();
						sender.sendMessage(header + "Use " + accent + "/reload" + text + " to apply changes.");
					} catch (ParseException e) {
						sender.sendMessage(header + error
								+ "An internal error ocurred while trying to update Wonder Weapons: " + e.getMessage());
						e.printStackTrace();
					}
				} else {
					sender.sendMessage(header + "Wonder Weapons is already up to date.");
				}
			} else {
				sender.sendMessage(header + error + "You don't have permission to use that command.");
			}
		}

		private void cmdVersion(CommandSender sender) {
			sender.sendMessage(header + " v" + desc.getVersion());
		}

		private void cmdHelp(String label, CommandSender sender) {
			sender.sendMessage(header + "With Wonder Weapons you can create chaos and mass destruction. No refunds.",
					accent + "Commands:", accent + "/" + label + " help" + text + ": Check the version.",
					accent + "/" + label + " version" + text + ": Check the version.",
					accent + "/" + label + " list" + text + ": Lists all available weapons.",
					accent + "/" + label + " recipe <weapon>" + text
							+ ": It shows you how to craft it. In Creative you can pick the weapon.",
					accent + "/" + label + " give <player> <weapon>" + text
							+ ": Gives a specific weapon to that player.",
					accent + "/" + label + " update" + text + ": Checks for updates, and updates if necessary.");
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
				sender.sendMessage(header + error + "You don't have permission to use that command. But nice try.");
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
					sender.sendMessage(header + "You gave " + accent + p.getName() + text + " one " + accent
							+ weaponName + text + ".");
					p.sendMessage(header + "You were given one " + accent + weaponName + text + " by " + accent
							+ sender.getName() + text + ".");
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
						header + "You can see a list of weapons using " + accent + "/" + label + " list" + text
								+ "." });
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

					CraftInventoryCustom craftInventory = (CraftInventoryCustom) Bukkit.createInventory(p,
							InventoryType.WORKBENCH, header + weapon.getItemMeta().getDisplayName());
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
									craftInventory.setItem(i * 3 + j + 1, is);
								}
							}
						}
					} else if (recipe instanceof ShapelessRecipe) {
						ShapelessRecipe sr = (ShapelessRecipe) recipe;
						List<ItemStack> lis = sr.getIngredientList();
						for (ItemStack itemStack : lis) {
							craftInventory.addItem(itemStack);
						}
					}
					craftInventory.setItem(0, weapon);
					p.openInventory(craftInventory);
				}
			}
			if (!ok) {
				p.sendMessage(header + "The weapon " + accent + args[1] + text + " doesn't exist.");
			}
		}
	}
	class CmdTabCompleter implements TabCompleter {

		public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
			switch (args.length) {
				case 1: {
					List<String> sol = new ArrayList<>(0);
					sol.add("help");
					sol.add("list");
					sol.add("recipe");
					if (sender.hasPermission("wonderweapons.admin"))
						sol.add("give");
					return sol;
				}
				case 2: {
					switch (args[0].toLowerCase()) {
					case "recipe": {
						List<String> sol = new ArrayList<>(0);
						String writenName = args.length>=4?args[1].toLowerCase().trim():"";
						for (WonderWeapon ww : WonderWeapon.LIST) {
							if (writenName.isEmpty()||ww.getName().toLowerCase().startsWith(writenName)) {
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
						String writenName = args.length>=4?args[2].toLowerCase().trim():"";
						for (WonderWeapon ww : WonderWeapon.LIST) {
							if (writenName.isEmpty()||ww.getName().toLowerCase().startsWith(writenName)) {
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
}
