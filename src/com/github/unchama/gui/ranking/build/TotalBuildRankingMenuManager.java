/**
 *
 */
package com.github.unchama.gui.ranking.build;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

import com.github.unchama.gui.ranking.TotalRankingMenuManager;
import com.github.unchama.util.Util;

/**
 * @author tar0ss
 *
 */
public final class TotalBuildRankingMenuManager extends TotalRankingMenuManager {

	@Override
	protected String getLore(double value) {
		return "総建築量:" + Util.Decimal(value);
	}

	@Override
	public String getInventoryName(Player player) {
		return ChatColor.BLUE + "建築量ランキング（総合）";
	}

}
