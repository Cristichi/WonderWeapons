package net.cristichi.ww.main;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.cristichi.ww.MourningWall;
import net.cristichi.ww.RayBow;
import net.cristichi.ww.RayBowX2;
import net.cristichi.ww.Thundermaker;
import net.cristichi.ww.TravelingBow;
import net.cristichi.ww.WonderWeapon;
import net.cristichi.ww.obj.CmdExecutor;
import net.cristichi.ww.obj.CmdTabCompleter;

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

	/**
	 * These teams will serve the purpose of making things glow in specific colors. They get generated for this plugin using the color's name
	 */
	public static final ChatColor[] TEAM_COLORS = new ChatColor[] {
			ChatColor.BLUE, ChatColor.GOLD, ChatColor.WHITE, ChatColor.BLACK,
			ChatColor.DARK_PURPLE, ChatColor.RED, ChatColor.DARK_RED };
	private static final String TEAMS_PREFIX = "WW-";
	public static Map<ChatColor, Team> teams;
	
	// Weapons
	public static RayBow rayBow;
	public static RayBowX2 rayBowX2;
	public static TravelingBow travelingBow;
	public static MourningWall mourningWall;
	public static Thundermaker thundermaker;

	@Override
	public void onEnable() {
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
		travelingBow = new TravelingBow(this);
		getServer().getPluginManager().registerEvents(TravelingBow.listener, this);
		mourningWall = new MourningWall(this);
		getServer().getPluginManager().registerEvents(MourningWall.listener, this);
		thundermaker = new Thundermaker(this);
		getServer().getPluginManager().registerEvents(Thundermaker.listener, this);
		
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("WonderWeapons Enabled");
		
		getCommand("cristichiwonderweapons").setExecutor(new CmdExecutor(header, accent, text, error, desc.getVersion()));
		getCommand("cristichiwonderweapons").setTabCompleter(new CmdTabCompleter());
		
	}

	@Override
	public void onDisable() {
		teams.forEach((color, team) -> {
			team.unregister();
		});
		teams = null;
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
			if (e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE) && !e.getSlotType().equals(SlotType.CRAFTING)) {
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

}
