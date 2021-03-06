package com.github.unchama.achievement.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.github.unchama.achievement.GiganticAchievement;
import com.github.unchama.event.TotalJoinIncrementEvent;

/**
 * 
 * @author tar0ss
 *
 */
public final class TotalJoinAchievement extends GiganticAchievement implements Listener{
	/**合計ログイン日数がこの値以上の時に実績を解除します
	 *
	 */
	private final long unlock_join;

	public TotalJoinAchievement(int id,long unlock_join) {
		super(id);
		this.unlock_join = unlock_join;
	}


	/**
	 * @return unlock_join
	 */
	public long getUnlockJoin() {
		return unlock_join;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void TotalJoinIncrementListener(TotalJoinIncrementEvent event) {
		if (event.getNextAll() >= this.getUnlockJoin())
			this.unlockAchievement(event.getGiganticPlayer());
	}

	@Override
	public String getUnlockInfo() {
		return "累計ログイン日数が" + this.getUnlockJoin() + "日を超える";
	}

	@Override
	public String getLockInfo() {
		return "累計ログイン日数が" + this.getUnlockJoin() + "日を超える";
	}

	@Override
	public int getPoint() {
		return 10;
	}

	@Override
	public int getUsePoint() {
		return 0;
	}

	@Override
	public boolean isPurchasable() {
		return false;
	}
}
