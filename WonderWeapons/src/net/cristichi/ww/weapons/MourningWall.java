package net.cristichi.ww.weapons;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class MourningWall extends WonderWeapon {
	public static NamespacedKey key;
	public static MourningWallListener listener = new MourningWallListener();

//	private static final String nameMetaArrow = "RayArrow";
//	private static FixedMetadataValue metaArrow;

	public MourningWall(Plugin plugin) {
		super(plugin, "Mourning Wall", Material.SHIELD, new NamespacedKey(plugin, "craft_mour_wall_bow"));

		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.DARK_GRAY+"Mourning Wall");
		setItemMeta(im);
		
		if (key == null) {
			key = new NamespacedKey(plugin, "mour_wall_bow");
//			metaArrow = new FixedMetadataValue(plugin, true);
		}

		setMeta(key);
		
		ItemMeta meta = getItemMeta();
        BlockStateMeta bmeta = (BlockStateMeta) meta;
        if (bmeta.getBlockState() instanceof Banner) {
   
            Banner banner = (Banner) bmeta.getBlockState();
            banner.setBaseColor(DyeColor.BLACK);
            banner.addPattern(new Pattern(DyeColor.RED, PatternType.MOJANG));

            banner.update();
            bmeta.setBlockState(banner);
        }
        setItemMeta(bmeta);

		ArrayList<String> lore = new ArrayList<>(3);
		lore.add(ChatColor.GRAY + "It belonged to a legendary hero.");
		setLore(lore);

		recipe = new ShapedRecipe(getKeyCraft(), this).shape("GGG", "GSG", "GGG").setIngredient('S', Material.SHIELD)
				.setIngredient('G', Material.GUNPOWDER);
		try {
			Bukkit.addRecipe(recipe);
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.WARNING,
					"Recipe for Mourning Wall could not be added. The item will be uncraftable. Error: "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	public static class MourningWallListener implements Listener {

		@EventHandler
		private void onHit(EntityDamageEvent e) {
			Entity ent = e.getEntity();
			if (ent instanceof HumanEntity) {
				HumanEntity h = (HumanEntity) ent;
				ItemStack offHand = h.getInventory().getItemInOffHand();
				if (Math.random()>0.9 && offHand != null && offHand.getItemMeta() !=null && offHand.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
					e.setCancelled(true);
					ent.getWorld().playSound(ent.getLocation(), Sound.ENTITY_BAT_HURT, 1f, 1f);
				}
			}
		}
	}
}
