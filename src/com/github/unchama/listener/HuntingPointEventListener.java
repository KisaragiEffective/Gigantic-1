package com.github.unchama.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.PlayerManager;
import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.huntinglevel.HuntingLevelManager;
import com.github.unchama.player.huntingpoint.HuntingPointManager;
import com.github.unchama.player.time.PlayerTimeManager;
import com.github.unchama.yml.DebugManager;
import com.github.unchama.yml.DebugManager.DebugEnum;
import com.github.unchama.yml.HuntingPointDataManager;

/**
*
* @author ten_niti
*
*/
public class HuntingPointEventListener implements Listener {

	DebugManager debug = Gigantic.yml.getManager(DebugManager.class);
	HuntingPointDataManager huntingPointData = Gigantic.yml
			.getManager(HuntingPointDataManager.class);

	// // モンスターを攻撃したとき
	// @EventHandler
	// public void onDamage(EntityDamageByEntityEvent event){
	// RaidHuntingManager.Instance().onAttack(event);
	// }

	// 棘によるダメージが発生したとき
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event){
		if(event.getCause() == DamageCause.THORNS){
			// ボスの場合は棘無効
			Entity entity = event.getEntity();
			String name = entity.getName();
			name = nameConvert(name, entity);
			double distance = huntingPointData.getMobData(name).raidDistance;
			if (distance > 0){
				event.setCancelled(true);
			}
		}
	}

	// モンスターを倒した時
	@EventHandler
	public void onKill(EntityDeathEvent event) {
		if (!(event.getEntity().getKiller() instanceof Player)) {
			return;
		}
		if(huntingPointData.isIgnoreWorld(event.getEntity().getWorld().getName())){
			return;
		}

		Player player = (Player) event.getEntity().getKiller();
		GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
		HuntingPointManager huntingPointManager = gp
				.getManager(HuntingPointManager.class);
		LivingEntity entity = event.getEntity();
		// 棘で倒したらポイントが入らない旨を警告して終了
		if(entity.getLastDamageCause().getCause() == DamageCause.THORNS){
			huntingPointManager.ThornWarning();
			return;
		}
		String name = entity.getName();

		name = nameConvert(name, entity);

		String message = name;
		if (huntingPointData.isHuntMob(name)) {
			message += " 狩猟対象";
		} else {
			message += " 要らない子";
		}

		// ポイントの追加
		int addPoint = 1;
		double distance = huntingPointData.getMobData(name).raidDistance;
		message += " raid : " + distance;
		if (distance > 0) {
			// ボス
			GivePointByRaidBoss(entity, name, distance, addPoint);
		} else {
			// 通常Mob
			boolean isSuccess = addPoint(player, entity, name, addPoint);
			if(!isSuccess){
				return;
			}
		}
		debug.sendMessage(player, DebugEnum.HUNT, message + event.getEntity().getMaxHealth());

	}

	// 近くにいるプレイヤー全員にポイントを付与
	private void GivePointByRaidBoss(LivingEntity entity, String name, Double distance, int addPoint) {
		for (Entity e : entity.getNearbyEntities(distance, distance, distance)) {
			if (!(e instanceof Player)) {
				continue;
			}
			Player player = (Player) e;
			GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
			// 放置中のプレイヤーは除外
			if(gp.getManager(PlayerTimeManager.class).isIdle()){
				continue;
			}

			boolean isSuccess = addPoint(player, entity, name, addPoint);
			if(!isSuccess){
				continue;
			}

			player.sendMessage(huntingPointData.getMobData(name).jpName
					+ " が討伐されたため,狩猟ポイント " + addPoint + " が付与されました");
		}
	}

	private boolean addPoint(Player player, LivingEntity entity, String name, int addPoint){
		GiganticPlayer gp = PlayerManager.getGiganticPlayer(player);
		HuntingPointManager huntingPointManager = gp
				.getManager(HuntingPointManager.class);

		// フライ中は無効
		if(player.isFlying()){
			huntingPointManager.FlyWarning();
			return false;
		}
		huntingPointManager.addPoint(name, addPoint);

		HuntingLevelManager huntingLevelManager = gp
				.getManager(HuntingLevelManager.class);
		huntingLevelManager.addExp(entity, name);

		return true;
	}

	// 同種扱い、別種扱いの名前を変換
	@SuppressWarnings("deprecation")
	private String nameConvert(String name, Entity entity) {
		String ret = name;
		// ウィザースケルトン
		if ((entity instanceof Skeleton)) {
			// ウィザスケはスケルトンクラスのタイプの違いを比較しないと名前がわからない
			if (((Skeleton) entity).getSkeletonType() == Skeleton.SkeletonType.WITHER) {
				ret = "WitherSkeleton";
			}
			// エルダーガーディアン
		} else if ((entity instanceof Guardian)) {
			if (((Guardian) entity).isElder()) {
				ret = "ElderGuardian";
			}
			// 同種扱い
		} else {
			ret = huntingPointData.ConvertName(name);
		}

		return ret;
	}
}
