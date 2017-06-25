package com.github.unchama.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.github.unchama.event.MenuClickEvent;
import com.github.unchama.gacha.Gacha.GachaType;
import com.github.unchama.gacha.moduler.GachaManager;
import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.PlayerManager;
import com.github.unchama.gui.GuiMenu;
import com.github.unchama.gui.moduler.GuiMenuManager;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.GiganticStatus;
import com.github.unchama.player.menu.PlayerMenuManager;
import com.github.unchama.yml.DebugManager;
import com.github.unchama.yml.DebugManager.DebugEnum;

import de.tr7zw.itemnbtapi.NBTItem;

/**
 * @author tar0ss
 *
 */
public class InventoryClickListener implements Listener {
	GuiMenu guimenu = Gigantic.guimenu;
	DebugManager debug = Gigantic.yml.getManager(DebugManager.class);

	@EventHandler(priority = EventPriority.HIGHEST)
	public void cancelPlayerClickMenu(InventoryClickEvent event) {

		InventoryView view = event.getView();
		HumanEntity he = view.getPlayer();
		// インベントリを開けたのがプレイヤーではない時終了
		if (!he.getType().equals(EntityType.PLAYER)) {
			return;
		}
		Player player = (Player) he;
		Inventory topinventory = view.getTopInventory();
		Inventory bottominventory = view.getBottomInventory();
		// インベントリが存在しない時終了
		if (topinventory == null) {
			return;
		}
		// debug.sendMessage(player, DebugEnum.GUI,"InventoryAction:" +
		// event.getAction().toString());
		// debug.sendMessage(player, DebugEnum.GUI,"ClickType:" +
		// event.getClick().toString());

		GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
		if (gp == null) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD
					+ "プレイヤーデータを読み込んでいます．しばらくお待ちください．");
			return;
		} else {
			if (!gp.getStatus().equals(GiganticStatus.AVAILABLE)) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD
						+ "プレイヤーデータが利用できない状態です．");
				return;
			}
		}

		// 開いているメニューが無ければ終了
		PlayerMenuManager pm = gp.getManager(PlayerMenuManager.class);
		if (pm.isEmpty()) {
			return;
		}

		// 現在開いているメニューを取得します．
		GuiMenuManager m = (GuiMenuManager) guimenu.getManager(pm.get()
				.getManagerClass());

		// 別のメニューを開いていれば終了
		if (!topinventory.getName().contains(m.getInventoryName(player))) {
			return;
		}

		debug.sendMessage(player, DebugEnum.GUI, m.getInventoryName(player)
				+ ChatColor.RESET + "内でクリックを検知");

		event.setCancelled(true);

		if (bottominventory.equals(event.getClickedInventory())) {
			return;
		}
		MenuClickEvent mevent = new MenuClickEvent(pm.get(), event);
		Bukkit.getServer().getPluginManager().callEvent(mevent);
	}


	// ガチャアイテムを金床で使えなくする
	@EventHandler
    public void canselGachaItemAnvil(InventoryClickEvent event) {
		// 金床を開いているか
		InventoryType invType = event.getWhoClicked().getOpenInventory().getType();
		if(invType != InventoryType.ANVIL){
			return;
		}

		// エンチャ本なら終了
		ItemStack item = event.getCurrentItem();
		if(item == null){
			return;
		}
		Material itemType = item.getType();
		if(itemType == Material.AIR || itemType == Material.ENCHANTED_BOOK){
			return;
		}

		// ガチャアイテムでなければ終了
		NBTItem nbt = new NBTItem(event.getCurrentItem());
		GachaType type = GachaManager.getGachaType(nbt);
		if(type == null){
			return;
		}

		event.getWhoClicked().sendMessage(ChatColor.AQUA + "ガチャアイテムを金床で使用することはできません.");
		event.setCancelled(true);
	}
}
