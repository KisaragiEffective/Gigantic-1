package com.github.unchama.gui.ranking.huntingexp;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

import com.github.unchama.gui.ranking.TotalRankingMenuManager;
import com.github.unchama.util.Util;

/**
 * @author ten_niti
 *
 */
public class TotalHuntingExpRankingMenuManager extends TotalRankingMenuManager {

	@Override
	protected String getLore(double value) {
		return "総狩猟経験値:" + Util.Decimal(value);
	}

	@Override
	public String getInventoryName(Player player) {
		return ChatColor.BLUE + "総合狩猟経験値ランキング";
	}

}
