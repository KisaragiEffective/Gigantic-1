package com.github.unchama.gui.huntingpoint;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
*
* @author ten_niti
*
*/
public class HuntingPointShopItem {
	public enum CategoryType {
		ToHead, // Mob毎に定められた頭
		CustomHead, // カスタムヘッド
		HeadCategory, // カテゴリまるごと
		Item, // 普通にアイテム
		// 実績
	}

	// カテゴリ
	private CategoryType categoryType;
	// 陳列するアイテム（アイテム系の場合はこれを渡す）
	private ItemStack itemStack;
	// 値段
	private int price;
	// //ログに流す名前
	// private String logName;
	// その他のデータ
	private String meta;

	public HuntingPointShopItem clone(){
		HuntingPointShopItem ret = new HuntingPointShopItem();
		ret.setCategory(categoryType);
		ret.setItemStack(itemStack.clone());
		ret.setPrice(price);
		ret.setMeta(meta);

		return ret;
	}

	public HuntingPointShopItem() {
	}

	// 有効なデータならtrue
	public boolean isEnable() {
		if (categoryType == null) {
			return false;
		}
		if (itemStack == null) {
			return false;
		}
		if (price <= 0) {
			return false;
		}
		// if(logName == null){
		// return false;
		// }

		return true;
	}

	// カテゴリのsetterとgetter
	public void setCategory(String type) {
		try {
			setCategory(CategoryType.valueOf(type));
		} catch (IllegalArgumentException e) {
			Bukkit.getLogger().warning(type + " というCategoryTypeは存在しません．");
		}
	}

	public void setCategory(CategoryType type) {
		categoryType = type;
	}

	public CategoryType getCategoryType() {
		return categoryType;
	}

	// アイテムデータのsetterとgetter
	public void setItemStack(ItemStack item) {
		itemStack = item;
	}

	public ItemStack getItemStack() {
		return itemStack.clone();
	}

	// 値段のsetterとgetter
	public void setPrice(int price_) {
		price = price_;
	}

	public int getPrice() {
		return price;
	}

	// //ログに流す名前のsetterとgetter
	// public void setLogName(String logName_){
	// logName = logName_;
	// }
	// public String getLogName(){
	// return logName;
	// }

	// その他のデータのsetterとgetter
	public void setMeta(String meta_) {
		meta = meta_;
	}

	public String getMeta() {
		return meta;
	}
}
