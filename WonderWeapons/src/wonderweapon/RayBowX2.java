package wonderweapon;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import main.WonderWeaponsPlugin;

public class RayBowX2 extends WonderWeapon {
	public static NamespacedKey key;
	public static RayBowX2Listener listener = new RayBowX2Listener();

	private static final String nameMetaArrow = "RayArrowX2";
	private static FixedMetadataValue metaArrow;

	public RayBowX2(Plugin plugin) {
		super("C's x2 Ray Bow", Material.BOW, new NamespacedKey(plugin, "craft_ray_bow_x2"));

		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.DARK_RED.toString()+ChatColor.BOLD+"C's x2 Ray Bow");
		setItemMeta(im);
		
		if (key == null) {
			key = new NamespacedKey(plugin, "ray_bow_x2");
			metaArrow = new FixedMetadataValue(plugin, true);
		}

		setMeta(key);

		ArrayList<String> lore = new ArrayList<>(3);
		lore.add(ChatColor.GRAY + "Processed Element 115");
		lore.add(ChatColor.GRAY + "Signed by C.");
		setLore(lore);

		recipe = new ShapedRecipe(getKeyCraft(), this).shape("TTT", "TBT", "TTT").setIngredient('B', Material.BOW)
				.setIngredient('T', Material.TNT);
		try {
			Bukkit.addRecipe(recipe);
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.WARNING, "Recipe for Ray Bow x2 could not be added. The item will be uncraftable.");
			e.printStackTrace();
		}
	}

	public static class RayBowX2Listener implements Listener {

		@EventHandler
		private void onBowShooot(EntityShootBowEvent e) {
			ItemStack bow = e.getBow();
			if (bow.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
				Entity projectile = e.getProjectile();

				if (projectile instanceof Arrow) {
					Arrow arrow = (Arrow) projectile;
					Team team = WonderWeaponsPlugin.teams.get(ChatColor.DARK_RED);
					team.addEntry(arrow.getUniqueId().toString());

					arrow.setVelocity(arrow.getVelocity().normalize().multiply(0.5));
					arrow.setColor(Color.RED);
					arrow.setGlowing(true);
					arrow.setGravity(false);
					arrow.setMetadata(nameMetaArrow, metaArrow);
					e.setProjectile(arrow);

					Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("WonderWeapons"),
							new Runnable() {
								@Override
								public void run() {
									if (arrow != null && !arrow.isDead()) {
										explode(arrow);
									}
								}
							}, 270);
				}
			}
		}

		@EventHandler
		private void onHit(ProjectileHitEvent e) {
			Projectile projectile = e.getEntity();
			if (projectile instanceof Arrow) {
				if (projectile.hasMetadata(nameMetaArrow)) {
					explode(projectile);
				}
			}
		}

		private void explode(Entity flecha) {
			Location loc = flecha.getLocation();
			flecha.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 2f, false, false);
			flecha.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 2f, false, false);
			flecha.remove();
		}
	}
}
