package com.github.unchama.listener;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.unchama.event.SeichiLevelUpEvent;
import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.PlayerManager;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.mana.ManaManager;
import com.github.unchama.player.seichiskill.passive.manarecovery.ManaRecoveryManager;
import com.github.unchama.player.sidebar.SideBarManager;
import com.github.unchama.player.sidebar.SideBarManager.Information;
import com.github.unchama.util.ParticleUtil;
import com.github.unchama.util.SeichiSkillAutoAllocation;
import com.github.unchama.yml.ConfigManager;

/**
 * @author tar0ss
 *
 */
public class SeichiLevelListener implements Listener {
	private Gigantic plugin = Gigantic.plugin;
	private ConfigManager config = Gigantic.yml.getManager(ConfigManager.class);

	/**
	 * マナリカバリースキルのレベルを更新する，
	 *
	 * @param event
	 */
	@EventHandler
	public void refreshManaRecoveryLevel(SeichiLevelUpEvent event) {
		GiganticPlayer gp = event.getGiganticPlayer();
		gp.getManager(ManaRecoveryManager.class).refresh(true);
	}

	// レベルアップ時の花火の打ち上げ
	@EventHandler
	public void launchFireFlower(SeichiLevelUpEvent event){
		Player player = PlayerManager.getPlayer(event.getGiganticPlayer());
		Location loc = player.getLocation();
		ParticleUtil.launchFireWorks(loc);
	}

	// レベルアップメッセージを表示する．
	@EventHandler
	public void sendMessage(SeichiLevelUpEvent event) {
		GiganticPlayer gp = event.getGiganticPlayer();
		int level = event.getLevel();
		Player p = plugin.getServer().getPlayer(gp.uuid);
		String m = config.getSeichiLevelUpMessage();
		m = PlaceholderAPI.setPlaceholders(p, m);
		if (m != null && m != "") {
			p.sendMessage(m);
		}
		m = config.getSeichiLevelMessage(p, level);
		if (m != null && m != "") {
			p.sendMessage(m);
		}
	}

	// 整地スキルの自動振り分けを行う
	@EventHandler
	public void AutoAllocation(SeichiLevelUpEvent event) {
		GiganticPlayer gp = event.getGiganticPlayer();
		SeichiSkillAutoAllocation.AutoAllocation(gp);
	}

	/**
	 * サイドバーを更新する．
	 *
	 * @param event
	 */
	@EventHandler
	public void refreshSideBar(SeichiLevelUpEvent event) {
		GiganticPlayer gp = event.getGiganticPlayer();
		int level = event.getLevel();
		SideBarManager m = gp.getManager(SideBarManager.class);
		m.updateInfo(Information.SEICHI_LEVEL, level);
		m.refresh();

	}

	@EventHandler
	public void refreshMana(SeichiLevelUpEvent event) {
		GiganticPlayer gp = event.getGiganticPlayer();
		gp.getManager(ManaManager.class).Levelup(event.getLevel());
	}

}
