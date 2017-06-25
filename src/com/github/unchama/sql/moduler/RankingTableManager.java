package com.github.unchama.sql.moduler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.sql.Sql;
import com.github.unchama.util.TimeUtil;
import com.github.unchama.yml.DebugManager.DebugEnum;

/**
 * @author tar0ss
 *
 */
public abstract class RankingTableManager extends TableManager {
	public static enum TimeType {
		DAY(0),
		WEEK(1),
		MONTH(2),
		YEAR(3);

		private static HashMap<Integer,TimeType> tMap = new HashMap<Integer,TimeType>(){
			{
				put(TimeType.DAY.getNum(),TimeType.DAY);
				put(TimeType.WEEK.getNum(),TimeType.WEEK);
				put(TimeType.MONTH.getNum(),TimeType.MONTH);
				put(TimeType.YEAR.getNum(),TimeType.YEAR);
			}
		};
		int n;

		TimeType(int n){
			this.n = n;
		}

		public int getNum(){
			return n;
		}

		public static TimeType getTypebyNum(int n){
			return tMap.get(n);
		}
	}

	//列名
	private String columnName;
	//テーブル名
	private String tableName;
	//常に更新されるべきマップ
	private HashMap<UUID, Double> map;
	//1分前のデータを保持するマップ
	private HashMap<UUID, Double> minuteMap;
	//上位150名のデータマップ
	private LinkedHashMap<String, Double> totalMap;

	private HashMap<TimeType,LinkedHashMap<String, Double>> timeMap;

	private HashMap<UUID,String> nameMap;

	public RankingTableManager(Sql sql) {
		super(sql);
		map = new HashMap<UUID, Double>();
		minuteMap = new HashMap<UUID, Double>();
		totalMap = new LinkedHashMap<String, Double>();
		timeMap = new HashMap<TimeType,LinkedHashMap<String, Double>>();
		for(TimeType tt : TimeType.values()){
			timeMap.put(tt, new LinkedHashMap<String, Double>());
		}
		nameMap = new HashMap<UUID,String>();
	}

	/**TimeTypeごとのランキングを更新します．
	 *
	 * @param tt
	 */
	public void updateLimitMap(TimeType tt) {
		updateNameMap();
		String command = "";
		LinkedHashMap<String, Double> map = timeMap.get(tt);
		map.clear();
		/**SELECT * FROM `mineblockranking` WHERE datetime BETWEEN '2017-05-18 00:00:00' and '2017-05-20 00:00:00'
		 *SELECT uuid,SUM(allmineblock) as allnum FROM `mineblockranking` WHERE datetime BETWEEN '2016-05-18 00:00:00' and '2017-07-20 00:00:00'
		 *GROUP BY uuid ORDER BY allnum DESC LIMIT 150
		 */
		String startDatetime = TimeUtil.getDateTimeOnString(tt, 0);
		String endDatetime = TimeUtil.getDateTimeOnString(tt, 1);

		command = "SELECT uuid,SUM(" + columnName + ") AS sum_num FROM " + db + "." + table + " "
				+ "WHERE datetime BETWEEN '" + startDatetime + "' and '" + endDatetime + "' "
				+ "GROUP BY uuid ORDER BY sum_num DESC LIMIT 150";
		UUID uuid;
		String name;
		// ロード
		try {
			rs = stmt.executeQuery(command);
			while (rs.next()) {
				// nameを取得
				uuid = UUID.fromString(rs.getString("uuid"));
				name = nameMap.get(uuid);
				map.put(name, rs.getDouble("sum_num"));
			}
			rs.close();
		} catch (SQLException e) {
			plugin.getLogger().warning(
					"Failed to loadTimeMap in " + table + " Table");
			e.printStackTrace();
		}
		updateMenu(tt,map);
	}
	private void updateNameMap() {
		nameMap.clear();
		String command = "";
		command = "SELECT uuid,name FROM " + db + ".gigantic" + " WHERE 1";
		UUID uuid;
		String name;
		// ロード
		try {
			rs = stmt.executeQuery(command);
			while (rs.next()) {
				// nameを取得
				uuid = UUID.fromString(rs.getString("uuid"));
				name = rs.getString("name");
				nameMap.put(uuid, name);
			}
			rs.close();
		} catch (SQLException e) {
			plugin.getLogger().warning(
					"Failed to loadTotalMap in " + table + " Table");
			e.printStackTrace();
		}
	}

	/**
	 *メニューを更新する．
	 * @param tt
	 * @param map
	 */
	protected abstract void updateMenu(TimeType tt, LinkedHashMap<String, Double> map);

	/**総合ランキング上位150名のデータをロードします．
	 * @param totalMap
	 *
	 */
	public void updateTotalMap() {
		String command = "";
		//初期化
		totalMap.clear();
		/**SELECT allmineblock,uuid FROM gigantic.mineblock
		 * WHERE 1 ORDER BY allmineblock DESC LIMIT 150
		 *
		 */
		command = "SELECT name," + columnName + " FROM " + db + "." + tableName
				+ " WHERE 1 ORDER BY " + columnName + " DESC LIMIT 150";

		String name;
		double value;
		// ロード
		try {
			rs = stmt.executeQuery(command);
			while (rs.next()) {
				// nameを取得
				name = rs.getString("name");
				value = rs.getDouble(columnName);
				if(value != 0D){
					totalMap.put(name, value);
				}
			}
			rs.close();
		} catch (SQLException e) {
			plugin.getLogger().warning(
					"Failed to loadTotalMap in " + table + " Table");
			e.printStackTrace();
		}
		updateMenu(totalMap);
	}

	/**メニューを更新する．
	 *
	 * @param totalMap
	 */
	protected abstract void updateMenu(LinkedHashMap<String, Double> totalMap);

	/**テーブルを作成する処理
	 *
	 */
	@Override
	protected Boolean createTable() {
		String command;
		// create Table
		command = "CREATE TABLE IF NOT EXISTS " + db + "." + table
				+ " (id int primary key auto_increment,datetime timestamp)";
		// send
		if (!sendCommand(command)) {
			plugin.getLogger().warning("Failed to Create " + table + " Table");
			return false;
		}

		// Column add
		command = "alter table " + db + "." + table + " ";
		//UUID
		command += "add column if not exists uuid varchar(128) default null,";

		// original column
		columnName = getColumnName();
		tableName = getTableName();
		String tmp = this.addColumnCommand(columnName);
		if (tmp != null)
			command += tmp;

		command += "last";

		command = command.replace(",last", "");
		// send
		if (!sendCommand(command)) {
			plugin.getLogger().warning(
					"Failed to add Column in " + table + " Table");
			return false;
		}
		return true;
	}

	/**
	 * 追加する列名が存在するテーブル名を入れてください．
	 * @return
	 */
	protected abstract String getTableName();

	/**
	 * 追加する列の名前を入れてください．
	 * @return
	 */
	protected abstract String getColumnName();

	/**
	 * カラム追加コマンド<br>
	 * ex)command += "add column if not exists allmineblock double default 0,";<br>
	 */
	protected abstract String addColumnCommand(String columnName);

	/**
	 * ランキングデータを送信する．
	 */
	public void send() {

		String command = "";
		this.checkStatement();

		command = "insert into " + db + "." + table + " (uuid," + columnName + ") values ";

		command += this.getValuesData();

		command += "last";

		command = command.replace(",last", "");

		if (command.contains("last")) {
			debug.info(DebugEnum.SQL, "Table:" + table + " no Player");
			return;
		}

		try {
			stmt.executeUpdate(command);
		} catch (SQLException e) {
			plugin.getLogger().warning(
					"Failed to insert " + table);
			e.printStackTrace();
			return;
		}
		debug.info(DebugEnum.SQL, "Table:" + table + " 更新されました");
		this.reset();
	}

	/**値を更新するときに実行されるメソッド
	 *
	 * @param gp
	 * @param n
	 */
	public void update(GiganticPlayer gp, double n) {
		map.put(gp.uuid, n);
	}

	/*
	 * 保存する値をコマンドとして取得する
	 */
	protected String getValuesData() {
		String command = "";

		for (UUID uuid : map.keySet()) {
			double inc = map.get(uuid) - minuteMap.get(uuid);
			if (inc != 0) {
				command += "('" + uuid.toString() + "'," + inc + "),";
			}
		}
		return command;
	}

	/**データを送信したときの終了処理を記述
	 *
	 */
	protected void reset() {
		for (UUID uuid : map.keySet()) {
			Player p = Bukkit.getServer().getPlayer(uuid);

			//もしプレイヤーが存在しなければ削除
			if (p == null) {
				map.remove(uuid);
				minuteMap.remove(uuid);
				return;
			}

			//プレイヤーが存在するときの処理

			minuteMap.put(uuid, map.get(uuid));
		}
	}

	/**プレイヤのデータが利用可能になった時に呼び出される．
	 *
	 * @param gp
	 */
	public void join(GiganticPlayer gp) {
		double a = getValue(gp);
		map.put(gp.uuid, a);
		minuteMap.put(gp.uuid, a);
	}

	/* ランキングに使用される値を返す
	 *
	 */
	protected abstract double getValue(GiganticPlayer gp);

	/**データマップが一つもない時True
	 *
	 * @return
	 */
	public boolean isEmpty() {
		return map.isEmpty() || minuteMap.isEmpty();
	}

}
