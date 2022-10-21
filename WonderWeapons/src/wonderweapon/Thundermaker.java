package wonderweapon;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.util.Vector;

import main.WonderWeaponsPlugin;

public class Thundermaker extends WonderWeapon {
	public static NamespacedKey key;
	public static ThundermakerListener listener = new ThundermakerListener();

	private static final String nameMetaArrow = "ThundermakerArrow";
	private static FixedMetadataValue metaArrow;

	public Thundermaker(Plugin plugin) {
		super("Thundermaker", Material.CROSSBOW, new NamespacedKey(plugin, "craft_thundermaker_bow"));

		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.WHITE.toString()+ChatColor.BOLD+"Thundermaker");
		setItemMeta(im);
		
		if (key == null) {
			key = new NamespacedKey(plugin, "thundermaker_bow");
			metaArrow = new FixedMetadataValue(plugin, true);
		}

		addEnchantment(Enchantment.MULTISHOT, 1);

		setMeta(key);

		ArrayList<String> lore = new ArrayList<>(3);	
		lore.add(ChatColor.YELLOW + "Extra large A+ batteries");
		setLore(lore);

		recipe = new ShapedRecipe(getKeyCraft(), this).shape("L L", "RCR", "RRR").setIngredient('C', Material.CROSSBOW)
				.setIngredient('L', Material.LIGHTNING_ROD).setIngredient('R', Material.REDSTONE_BLOCK);
		try {
			Bukkit.addRecipe(recipe);
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.WARNING,
					"Recipe for Thundermaker could not be added. The item will be uncraftable. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static class ThundermakerListener implements Listener {

		@EventHandler
		private void onBowShooot(EntityShootBowEvent e) {
			ItemStack bow = e.getBow();
			if (bow.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
				Entity projectile = e.getProjectile();

				if (projectile instanceof Arrow) {
					Arrow arrow = (Arrow) projectile;
					Team team = WonderWeaponsPlugin.teams.get(ChatColor.WHITE);
					team.addEntry(arrow.getUniqueId().toString());

					arrow.setVelocity(arrow.getVelocity().multiply(1.5).add(new Vector(Math.random()-.5, Math.random()-.5, Math.random()-.5)));
					arrow.setColor(Color.WHITE);
					arrow.setGlowing(true);
					arrow.setGravity(false);
					arrow.setMetadata(nameMetaArrow, metaArrow);
					e.setProjectile(arrow);

					Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("WonderWeapons"),
							new Runnable() {
								@Override
								public void run() {
									if (arrow != null && !arrow.isDead()) {
										impact(arrow);
									}
								}
							}, 10);
				}
			}
		}
		
//		@EventHandler
//		private void onDamaged(EntityDamageEvent e) {
//			Entity damager = e.getEntity();
//			if (damager instanceof Arrow) {
//				if (damager.hasMetadata(nameMetaArrow)) {
//					explode(damager);
//				}
//			}	
//		}

		@EventHandler
		private void onHit(ProjectileHitEvent e) {
//			if (e.getHitBlock() != null) {
				Projectile projectile = e.getEntity();
				if (projectile instanceof Arrow) {
					if (projectile.hasMetadata(nameMetaArrow)) {
						impact(projectile);
					}
				}	
//			}
		}

		private void impact(Entity flecha) {
			Location loc = flecha.getLocation();
			flecha.getWorld().strikeLightningEffect(loc);
			flecha.remove();
		}
	}
}