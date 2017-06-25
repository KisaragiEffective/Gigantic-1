package com.github.unchama.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
*
* @author ten_niti
*
*/
public class InventoryUtil {

	// インベントリ内に入っているスタック数を取得
	static public int getStackCount(Inventory inventory){
		int count = 0;
		for(ItemStack item : inventory.getContents()){
			if(item != null){
				count++;
			}
		}
		inventory.addItem(inventory.getItem(0));
		return count;
	}

	// インベントリに新しいスタックでアイテムを追加
	static public boolean addItemStack(Inventory inventory, ItemStack item, boolean isNew) {
		if (item == null) {
			return false;
		}
		for(int i = 0; i < inventory.getSize(); i++){
			ItemStack itemstack = inventory.getItem(i);

			// 空白を見つけて入れる
			if(itemstack == null){
				inventory.setItem(i, item);
				return true;
			}

			// アイテムがある場合は識別してスタックさせる
			if(!isNew){
				if(!itemstack.isSimilar(item)){
					continue;
				}
				int amount = itemstack.getAmount() + item.getAmount();
				// スタック数の最大を超えていたら繰り越し処理が厄介なのでとりあえず飛ばす（必要になったら要検討）
				if(amount > itemstack.getMaxStackSize()){
					continue;
				}
				itemstack.setAmount(amount);
				return true;
			}
		}
		// 入り切らなければ終了
		return false;
	}
}
