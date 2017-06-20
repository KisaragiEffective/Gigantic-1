package com.github.unchama.gui.seichiskill.active.fairyaegis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.unchama.gigantic.PlayerManager;
import com.github.unchama.gui.GuiMenu.ManagerType;
import com.github.unchama.gui.moduler.ActiveSkillMenuManager;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.seichilevel.SeichiLevelManager;
import com.github.unchama.player.seichiskill.SkillEffectManager;
import com.github.unchama.player.seichiskill.active.FairyAegisManager;
import com.github.unchama.player.seichiskill.moduler.ActiveSkillType;

/**
 * @author tar0ss
 *
 */
public class FairyAegisMenuManager extends ActiveSkillMenuManager {

	private static ActiveSkillType st = ActiveSkillType.FAIRYAEGIS;

	@Override
	public String getInventoryName(Player player) {
		GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
		return gp.getManager(FairyAegisManager.class).getJPName();
	}

	@Override
	protected ItemMeta getItemMeta(Player player, int slot, ItemStack itemstack) {
		GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
		SkillEffectManager Em = gp.getManager(SkillEffectManager.class);
		FairyAegisManager m = gp.getManager(FairyAegisManager.class);
		MenuType mt = MenuType.getMenuTypebySlot(slot);
		if (mt == null)
			return null;
		ItemMeta itemmeta = itemstack.getItemMeta();
		List<String> lore = new ArrayList<String>();
		switch (mt) {
		case INFO:
			itemmeta.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD
					+ "基本情報");
			lore = new ArrayList<String>();
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GRAY + "他スキル使用時に追加で");
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GRAY
					+ "上部のブロックを妖精さんが");
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GRAY + "ゆっくり破壊します．");

			if (m.getToggle()) {
				lore.add("" + ChatColor.RESET + ChatColor.GREEN + "トグル："
						+ ChatColor.GREEN + "ON");
			} else {
				lore.add("" + ChatColor.RESET + ChatColor.GREEN + "トグル："
						+ ChatColor.RED + "OFF");
			}
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GREEN
					+ "現在の最大破壊ブロック数:" + m.getBreakNum());
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GREEN + "現在の最大マナ消費:"
					+ (int) m.getMana(m.getBreakNum()));
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GREEN + "現在の妖精数:"
					+ m.getFairy());
			lore.add("" + ChatColor.RESET + ChatColor.GREEN
					+ "クリックするとオンオフを切り替えます．");

			itemmeta.setLore(lore);
			break;
		case RANGE:
			itemmeta.setDisplayName(ChatColor.BLUE + "最大破壊数設定");
			lore = new ArrayList<String>();
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GRAY
					+ "最大破壊数を設定します．");
			lore.add("" + ChatColor.RESET + ChatColor.AQUA + "現在の最大破壊ブロック数:"
					+ m.getBreakNum());

			lore.add("" + ChatColor.RESET + ChatColor.BLUE + ChatColor.BOLD
					+ "クリックして数を設定");
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GRAY
					+ "※自動でトグルがオフになります");
			itemmeta.setLore(lore);
			break;
		case EXTENSION:
			itemmeta.setDisplayName(ChatColor.DARK_AQUA + "スキル強化");
			lore = new ArrayList<String>();
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GRAY + "スキルレベル :"
					+ 1);
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GRAY + "最大破壊ブロック数:"
					+ m.getMaxBreakNum());
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GRAY + "未実装");
			itemmeta.setLore(lore);
			break;
		case EFFECT:
			itemmeta.setDisplayName(ChatColor.DARK_PURPLE + "エフェクト選択");
			lore = new ArrayList<String>();
			lore.add("" + ChatColor.RESET + ChatColor.DARK_GRAY + "現在のエフェクト :" + Em.getName(st));
			itemmeta.setLore(lore);
			break;
		default:
			break;
		}
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		return itemmeta;
	}

	@Override
	protected ItemStack getItemStack(Player player, int slot) {
		GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
		MenuType mt = MenuType.getMenuTypebySlot(slot);
		if (mt == null)
			return null;
		ItemStack itemstack = null;
		switch (mt) {
		case INFO:
			itemstack = gp.getManager(FairyAegisManager.class)
					.getMenuItemStack();
			break;
		case RANGE:
			itemstack = new ItemStack(Material.GLASS);
			break;
		case EXTENSION:
			itemstack = new ItemStack(Material.ENCHANTMENT_TABLE);
			break;
		case EFFECT:
			itemstack = head.getMobHead("f_cube");
			break;
		default:
			break;
		}
		return itemstack;
	}

	@Override
	protected void setOpenMenuMap(HashMap<Integer, ManagerType> openmap) {
		openmap.put(MenuType.RANGE.getSlot(), ManagerType.F_RANGEMENU);
		openmap.put(MenuType.EFFECT.getSlot(), ManagerType.F_EFFECTSELECTMENU);

	}

	@Override
	protected void setIDMap(HashMap<Integer, String> id_map) {
		id_map.put(MenuType.INFO.getSlot(), "toggle");
	}

	@Override
	public boolean invoke(Player player, String identifier) {
		GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
		SeichiLevelManager sm = gp.getManager(SeichiLevelManager.class);
		FairyAegisManager m = gp.getManager(FairyAegisManager.class);
		switch (identifier) {
		case "toggle":
			if (sm.getLevel() < m.getUnlockLevel()) {
				player.sendMessage("解放できるレベルに達していません");
				return true;
			}
			m.toggle();
			player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK,
					(float) 0.7, (float) 2.2);
			player.openInventory(this.getInventory(player, 0));
			return true;
		}
		return false;
	}

}
