package wonderweapon;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

public class TravelingBow extends WonderWeapon {
	public static NamespacedKey key;
	public static TravelingBowListener listener = new TravelingBowListener();

	private static final String nameMetaArrow = "TravelingArrow";
	private static FixedMetadataValue metaArrow;

	public TravelingBow(Plugin plugin) {
		super(plugin, "Traveling Bow", Material.BOW, new NamespacedKey(plugin, "craft_traveling_bow"));

		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.DARK_PURPLE+"Traveling Bow");
		setItemMeta(im);
		
		if (key == null) {
			key = new NamespacedKey(plugin, "traveling_bow");
			metaArrow = new FixedMetadataValue(plugin, true);
		}

		setMeta(key);

		ArrayList<String> lore = new ArrayList<>(3);
		lore.add(ChatColor.GRAY + "Arrow hook");
		setLore(lore);

		recipe = new ShapedRecipe(getKeyCraft(), this).shape("PHP", "PBP", "PPP")
				.setIngredient('H', Material.TRIPWIRE_HOOK).setIngredient('B', Material.BOW)
				.setIngredient('P', Material.ENDER_PEARL);
		try {
			Bukkit.addRecipe(recipe);
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.WARNING,
					"Recipe for Ray Bow could not be added. The item will be uncraftable. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static class TravelingBowListener implements Listener {

		@EventHandler
		private void onBowShooot(EntityShootBowEvent e) {
			ItemStack bow = e.getBow();
			if (bow.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
				Entity projectile = e.getProjectile();

				if (projectile instanceof Arrow) {
					Arrow arrow = (Arrow) projectile;
					Team team = WonderWeaponsPlugin.teams.get(ChatColor.DARK_PURPLE);
					team.addEntry(arrow.getUniqueId().toString());

					arrow.setVelocity(arrow.getVelocity().multiply(0.7));
					arrow.addPassenger(e.getEntity());
					arrow.setColor(Color.PURPLE);
					arrow.setGlowing(true);
//					arrow.setGravity(false);
					arrow.setMetadata(nameMetaArrow, metaArrow);
					e.setProjectile(arrow);
					Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("WonderWeapons"),
							new Runnable() {
								@Override
								public void run() {
									if (projectile != null && !projectile.isDead())
										remove(projectile);
								}
							}, 300);
				}
			}
		}

		@EventHandler
		private void onEntityHit(EntityDamageByEntityEvent e) {
			Entity damager = e.getDamager();
			if (damager instanceof Arrow && damager.hasMetadata(nameMetaArrow)) {
				e.setCancelled(true);
			}
		}

		@EventHandler
		private void onHit(ProjectileHitEvent e) {
			Projectile proyectil = e.getEntity();
			if (proyectil instanceof Arrow && proyectil.hasMetadata(nameMetaArrow)) {
				remove(proyectil);
			}
		}

		private void remove(Entity arrow) {
			arrow.remove();
		}
	}
}
