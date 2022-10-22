package net.cristichi.ww;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.cristichi.ww.obj.GlowEnchantment;

public class WonderWeapon extends ItemStack {
	public static ArrayList<WonderWeapon> LIST = new ArrayList<>();
	
	protected String name;
	protected Recipe recipe;
	protected NamespacedKey keyCraft;
	protected FixedMetadataValue meta;

	protected WonderWeapon(Plugin plugin, String name, Material type, NamespacedKey keyCraft) {
		super(type);
		
		ItemMeta im = getItemMeta();
		im.addEnchant(new GlowEnchantment(new NamespacedKey(plugin, "glow_ench")), 1, false);
		setItemMeta(im);
		
		this.name = name;
		this.keyCraft = keyCraft;
		LIST.add(this);
	}

	public String getName() {
		return name;
	}
	
	public void setLore(ArrayList<String> lore) {
		ItemMeta im = getItemMeta();
		im.setLore(lore);
		setItemMeta(im);
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public Recipe getRecipe() {
		return recipe;
	}
	
	public void setMeta(FixedMetadataValue meta) {
		this.meta = meta;
	}

	public FixedMetadataValue getMeta() {
		return meta;
	}

	public NamespacedKey getKeyCraft() {
		return keyCraft;
	}
	
	public void setMeta(NamespacedKey key) {
		ItemMeta im = getItemMeta();
		im.getPersistentDataContainer().set(key, PersistentDataType.BYTE, Byte.MIN_VALUE);
		setItemMeta(im);
	}
}
