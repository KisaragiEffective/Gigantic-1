package com.github.unchama.yml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gui.huntingpoint.HuntingPointShopItem;
import com.github.unchama.gui.huntingpoint.HuntingPointShopItem.CategoryType;
import com.github.unchama.yml.CustomHeadManager.CustomHead;
import com.github.unchama.yml.CustomHeadManager.HeadCategory;
import com.github.unchama.yml.moduler.YmlManager;

/**
*
* @author ten_niti
*
*/
public class HuntingPointDataManager extends YmlManager {
	public class HuntMobData {
		public String name; // 呼び出し名の逆引き
		public String jpName; // 日本語名
		public String headName; // MobHeadで呼び出すための名前
		public boolean isTarget; // 狩猟対象ならtrue
		public double raidDistance; // ボス

		public HuntMobData(String name_, String jpName_, String headName_,
				boolean isTarget_, double raidDistance_) {
			name = name_;
			jpName = jpName_;
			headName = headName_;
			isTarget = isTarget_;
			raidDistance = raidDistance_;
		}
	}

	public static enum HuntingMobType{
		PIG("Pig"),
		SHEEP("Sheep"),
		COW("Cow"),
		MOOSHROOM("Mooshroom"),
		CHICKEN("Chicken"),
		SQUID("Squid"),
		WOLF("Wolf"),
		OCELOT("Ocelot"),
		HORSE("Horse"),
		SKELETONHORSE("SkeletonHorse"),
		RABBIT("Rabbit"),
		POLARBEAR("PolarBear"),
		VILLAGER("Villager"),
		ZOMBIE("Zombie"),
		HUSK("Husk"),
		SKELETON("Skeleton"),
		SPIDER("Spider"),
		CAVEPIDER("CaveSpider"),
		CREEPER("Creeper"),
		ENDERMAN("Enderman"),
		WITCH("Witch"),
		GUARDIAN("Guardian"),
		ELDERGUARDIAN("ElderGuardian"),
		SLIME("Slime"),
		SILVERFISH("Silverfish"),
		ENDERMITE("Endermite"),
		PIGZOMBIE("PigZombie"),
		WITHERSKELETON("WitherSkeleton"),
		BLAZE("Blaze"),
		GHAST("Ghast"),
		MAGMACUBE("MagmaCube"),
		SHULKER("Shulker"),
		WITHER("Wither"),
		ENDERDRAGON("EnderDragon"),
		;

		private String MobName;
		private static Map<String, HuntingMobType> typeMap = new LinkedHashMap<String, HuntingMobType>();

		// Enum用コンストラクタ
		HuntingMobType(String name){
			MobName = name;
		}

		static {
			for(HuntingMobType mobType : HuntingMobType.values()){
				typeMap.put(mobType.getMobName(), mobType);
			}
		}

		// HuntMobData.nameに使われている名前を返す
		public String getMobName(){
			return MobName;
		}

		// モンスター名からHuntingMobTypeを取得
		public static HuntingMobType getMobType(String name){
			return typeMap.get(name);
		}
	}

	// MOBごとのショップアイテム
	private static Map<String, List<HuntingPointShopItem>> shopItems;

	// MOBごとの基本情報
	private static Map<String, HuntMobData> MobNames;

	// 名前を変換する必要がある対応
	private static Map<String, String> ConvertNames;

	// 狩猟ポイントの判定を除外するワールド
	private static List<String> WorldIgnore;

	// 整地鯖で経験値判定を除外するMOB
	private static List<String> SeichiExpIgnore;

	// 最大狩猟レベル
	private int MaxHuntingLevel;

	CustomHeadManager headManager = Gigantic.yml
			.getManager(CustomHeadManager.class);

	// コンストラクタ
	public HuntingPointDataManager() {
		super();
		reload();
	}

	@Override
	protected void saveDefaultFile() {
		if (!file.exists()) {
			plugin.saveResource(filename, false);
		}
	}

	// ymlファイルからデータを取りなおす
	public void reload() {
		// 表示データ
		ConfigurationSection basedata = this.fc
				.getConfigurationSection("mobdata");
		MobNames = new LinkedHashMap<String, HuntMobData>();
		for (String name : basedata.getKeys(false)) {
			// 半角スペースが入るとSQLのコマンドに支障がある為
			name = name.replace(" ", "");

			boolean isTarget = basedata.getBoolean(name + ".target", false);

			String jpname = basedata.getString(name + ".jpname");
			String headname = basedata.getString(name + ".headname");
			double raidDistance = basedata.getDouble(name + ".raiddistance", 0);
			MobNames.put(name,
					new HuntMobData(name, jpname, headname, isTarget, raidDistance));
		}

		// 同種判定のリスト
		ConvertNames = new HashMap<String, String>();
		List<String> convertName = this.fc.getStringList("huntmob_convert");
		for (String name : convertName) {
			String[] n = name.split(" : ");
			if (n.length == 2) {
				ConvertNames.put(n[0], n[1]);
			}
		}

		// ショップのアイテム
		shopItems = new HashMap<String, List<HuntingPointShopItem>>();
		for (String name : MobNames.keySet()) {
			List<HuntingPointShopItem> list = new ArrayList<HuntingPointShopItem>();
			ConfigurationSection shopSection = this.fc
					.getConfigurationSection("shop." + name);
			if (shopSection != null) {
				for (String index : shopSection.getKeys(false)) {
					String path = "shop." + name + "." + index;
					String str = this.fc.getString(path + ".category", "");
					// Bukkit.getServer().getLogger().info(path + " : " + str);
					if (str != "") {
						HuntingPointShopItem item = getShopItem(path, name);
						addShopList(list, item, path);
					}
				}
			}
			shopItems.put(name, list);
		}

		// 最大狩猟レベル
		MaxHuntingLevel = this.fc.getInt("maxhuntinglevel");

		// 除外設定
		WorldIgnore = this.fc.getStringList("world_ignore");
		SeichiExpIgnore = this.fc.getStringList("seichi_exp_ignore");

	}

	private void addShopList(List<HuntingPointShopItem> list,
			HuntingPointShopItem shopItem, String path) {
		// データが不足していなければ追加
		if (!shopItem.isEnable()) {
			Bukkit.getServer().getLogger().warning(path + " : disable");
			return;
		}
		if (shopItem.getCategoryType() == CategoryType.HeadCategory) {
			HeadCategory category = headManager.getCategoryHeads(shopItem
					.getMeta());
			for (CustomHead head : category.heads) {
				HuntingPointShopItem item = shopItem.clone();
				item.setItemStack(head.getSkull());
				// 例外設定を試したかったが何かうまくいかなかった
//				int price = this.fc.getInt(path + ".exception." + head.name
//						+ ".price", item.getPrice());
//				Bukkit.getServer().getLogger()
//						.info(path + ".exception." + head.name + ".price");
//				item.setPrice(price);

				list.add(item);
			}
		} else {
			list.add(shopItem);
		}
	}

	// ショップのアイテムを取得
	private HuntingPointShopItem getShopItem(String path, String name) {
		HuntingPointShopItem ret = new HuntingPointShopItem();
		ret.setCategory(this.fc.getString(path + ".category"));
		ret.setPrice(this.fc.getInt(path + ".price", 0));
		// ret.setLogName(this.fc.getString(path + ".logname"));
		ret.setMeta(this.fc.getString(path + ".meta"));
		ItemStack item = this.fc.getItemStack(path + ".itemstack", null);
		String headName = "";
		if (ret.getCategoryType() != null) {
			switch (ret.getCategoryType()) {
			case ToHead:
				headName = MobNames.get(name).headName;
				if (item != null) {
					headManager.setSkull(item, headName);
				} else {
					item = headManager.getMobHead(headName);
				}
				break;
			case CustomHead:
				headName = this.fc.getString(path + ".headname", "");
				if (item != null) {
					headManager.setSkull(item, headName);
				} else {
					item = headManager.getMobHead(headName);
				}
				break;
			case HeadCategory:
				String CategoryName = this.fc.getString(path + ".categoryname",
						"");
				;
				item = headManager.getCategoryHeads(CategoryName).mainSkull;
				ret.setMeta(CategoryName);
				break;
			case Item:
				break;
			default:
				break;
			}
		}

		ret.setItemStack(item);
		return ret;
	}

	// ショップのアイテムリストを取得
	public List<HuntingPointShopItem> getShopItems(String name) {
		List<HuntingPointShopItem> ret = null;
		if (shopItems.containsKey(name)) {
			ret = shopItems.get(name);
		}
		return ret;
	}

	// 狩猟対象か否か
	public boolean isHuntMob(String name) {
		reload();
		name = ConvertName(name);
		if (!MobNames.containsKey(name)) {
			return false;
		}

		return MobNames.get(name).isTarget;
	}

	// 同種として扱われるMob名の変換
	public String ConvertName(String name) {
		String ret = name;
		// 現状、「同種判定はいらない」とのことなのでコメントアウト
		// if (ConvertNames.containsKey(name)) {
		// ret = ConvertNames.get(name);
		// }
		// 「Magma Cube」が半角スペースが入っているせいでそちらに合わせると
		// SQL周りで不具合が起こるためこちらで吸い取る
		ret = ret.replace(" ", "");
		return ret;
	}

	public Map<String, HuntMobData> getMobNames() {
		return MobNames;
	}

	public HuntMobData getMobData(String name) {
		return MobNames.get(name);
	}

	public boolean isIgnoreWorld(String worldName){
		return WorldIgnore.contains(worldName);
	}

	public boolean isExpIgnoreMob(String MobName){
		return SeichiExpIgnore.contains(MobName);
	}

	public int getMaxHuntingLevel(){
		return MaxHuntingLevel;
	}
}
