package net.cristichi.ww;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class AnubisHoe extends WonderWeapon {
	private static double damagePerStack = 0.3;
	
	public static NamespacedKey key;
	public static NamespacedKey keyStacks;

	public AnubisHoe(Plugin plugin) {
		super(plugin, "Anubis' Hoe", Material.GOLDEN_HOE, new NamespacedKey(plugin, "craft_anubis_hoe"));

		if (key == null) {
			key = new NamespacedKey(plugin, "anubis_hoe");
			keyStacks = new NamespacedKey(plugin, "anubis_stacks");
		}

		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.RED + "Anubis' " + ChatColor.GOLD + "Hoe");
		im.addEnchant(Enchantment.DURABILITY, 5, true);
		im.getPersistentDataContainer().set(keyStacks, PersistentDataType.INTEGER, 0);
		setItemMeta(im);

		setMeta(key);

		ArrayList<String> lore = new ArrayList<>(3);
		lore.add(ChatColor.GOLD + "Sacred Sand");
		lore.add(ChatColor.GRAY + "Stacks: " + ChatColor.GOLD + " 0");
		setLore(lore);

		recipe = new ShapedRecipe(getKeyCraft(), this).shape("HHH", "HDH", "GGG")
				.setIngredient('D', Material.DIAMOND_HOE).setIngredient('H', Material.HAY_BLOCK)
				.setIngredient('G', Material.GOLD_BLOCK);
		try {
			Bukkit.addRecipe(recipe);
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.WARNING,
					"Recipe for Anubis' Hoe could not be added. The item will be uncraftable. Error: "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	public static class AnubisHoeListener implements Listener {

		@EventHandler
		private void onKill(EntityDeathEvent e) {
			LivingEntity victim = e.getEntity();
			Player killer = victim.getKiller();
			if (killer != null) {
				ItemStack hando = killer.getInventory().getItemInMainHand();
				if (hando != null && hando.hasItemMeta()) {
					ItemMeta im = hando.getItemMeta();
					if (im.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
						Integer stacks = im.getPersistentDataContainer().get(keyStacks,
								PersistentDataType.INTEGER);
						if (stacks != null) {
							stacks++;
							List<String> lore = im.getLore();
							for (int i = 0; i < lore.size(); i++) {
								String loreI = lore.get(i);
								if (loreI.startsWith(ChatColor.GRAY + "Stacks: ")) {
									lore.set(i, ChatColor.GRAY + "Stacks: " + ChatColor.GOLD + " " + stacks);
									break;
								}
							}
							im.setLore(lore);
							im.getPersistentDataContainer().set(keyStacks, PersistentDataType.INTEGER, stacks);
							hando.setItemMeta(im);

							killer.playSound(killer, Sound.BLOCK_ANVIL_USE, 1, 1);
						}
					}
				}
			}
		}

		@EventHandler
		private void onDamage(EntityDamageByEntityEvent e) {
			if (e.getDamager() instanceof Player) {
				Player damager = (Player) e.getDamager();
				ItemStack hando = damager.getInventory().getItemInMainHand();
				if (hando != null && hando.hasItemMeta()
						&& hando.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
					Integer stacks = hando.getItemMeta().getPersistentDataContainer().get(keyStacks,
							PersistentDataType.INTEGER);
					if (stacks != null) {
						e.setDamage(e.getDamage() + stacks*damagePerStack);
					}
				}
			}
		}

	}
}
