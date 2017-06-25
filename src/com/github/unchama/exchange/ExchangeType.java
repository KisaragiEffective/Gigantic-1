package com.github.unchama.exchange;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Mon_chi on 2017/06/17.
 */
public enum ExchangeType implements InventoryHolder {
    ORE(ChatColor.YELLOW + "不要鉱石景品交換システム", new String[]{ChatColor.GREEN + "不要な各種鉱石を", ChatColor.GREEN + "交換券と交換します"}, new ItemStack(Material.DIAMOND_PICKAXE), new OreExchanger());

    //メニュー用アイコン
    private ItemStack icon;
    //exchangeメソッドを実装したクラス
    private Exchanger exchanger;

    ExchangeType(String name, String[] lore, ItemStack icon, Exchanger exchanger) {
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        icon.setItemMeta(meta);
        this.icon = icon;
        this.exchanger = exchanger;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public Exchanger getExchanger() {
        return exchanger;
    }

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(this, 9*6);
    }
}
