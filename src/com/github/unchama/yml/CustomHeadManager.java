package com.github.unchama.yml;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.unchama.util.Util;
import com.github.unchama.yml.moduler.YmlManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

/**
*
* @author ten_niti
*
*/
public class CustomHeadManager extends YmlManager {

	private static String FOLDERNAME = "mobhead";


	public class CustomHead {
		public String name; // 呼び出し名の逆引き
		public String dispName; // 表示名
		public String url; // url
		private ItemStack skull; // 頭

		public CustomHead(String name_, String dispName_, String url_,
				ItemStack skull_) {
			name = name_;
			dispName = dispName_;
			url = url_;
			skull = skull_;
		}

		public ItemStack getSkull() {
			return skull.clone();
		}
	}

	public class HeadCategory {
		public String name; // 呼び出し名の逆引き
		public String dispName; // 表示名
		public ItemStack mainSkull; // 代表となる頭
		public List<CustomHead> heads; // カテゴリが持っている頭

		public HeadCategory(String name_, String dispName_,
				String mainSkullName, List<CustomHead> heads_) {
			name = name_;
			dispName = dispName_;
			mainSkull = getMobHead(mainSkullName);
			Util.setDisplayName(mainSkull, ChatColor.RESET + dispName);

			heads = heads_;
		}
	}

	// Map.keysetで回すと順番が変わるため
	static private Map<String, CustomHead> customHeads;
	// カテゴリごとのリスト
	// static private Map<String, List<CustomHead>> headCategory;

	static private Map<String, HeadCategory> categoryData;

	private File folder;

	// コンストラクタ
	public CustomHeadManager() {
		super();
		loadmobhead();
	}

	@Override
	protected void saveDefaultFile() {
		if (!file.exists()) {
			plugin.saveResource(filename, false);
		}
		//フォルダーインスタンス
		folder = new File(
				plugin.getDataFolder(), FOLDERNAME);
		//フォルダがない場合は生成
		if(!folder.exists()){
			if(!folder.mkdirs()){
				Bukkit.getServer().getLogger()
				.warning(FOLDERNAME + "というディレクトリを作成できませんでした．");
				return;
			}
		}else if(!folder.isDirectory()){
			Bukkit.getServer().getLogger()
			.warning(FOLDERNAME + "という名前のファイルを削除してください");
			return;
		}
	}

	// ymlファイルからデータを取りなおす
	public void loadmobhead() {
		// ヘッドデータ
		customHeads = new LinkedHashMap<String, CustomHead>();
		// headCategory = new LinkedHashMap<String, List<CustomHead>>();

		// カテゴリデータ
		categoryData = new LinkedHashMap<String, HeadCategory>();
		ConfigurationSection categorydata = this.fc
				.getConfigurationSection("category");

		for (String name : categorydata.getKeys(false)) {
			String dispname = categorydata.getString(name + ".dispname");
			String mainHead = categorydata.getString(name + ".mainhead");
			categoryData.put(name, new HeadCategory(name, dispname, mainHead,
					readCategoryHeads(name)));
		}
	}

	private List<CustomHead> readCategoryHeads(String categoryName) {
		List<CustomHead> ret = new ArrayList<CustomHead>();

		String cname = categoryName + ".yml";
		File file = new File(
				folder, cname);
		if (!file.exists()) {
			plugin.saveResource(FOLDERNAME + "\\" + cname, false);
		}
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

		ConfigurationSection basedata = yml.getConfigurationSection("mobhead");

		// ヘッド生成
		for (String name : basedata.getKeys(false)) {
			String dispname = basedata.getString(name + ".dispname");
			String url = basedata.getString(name + ".url");
			ItemStack skull = getSkull(url);
			Util.setDisplayName(skull, ChatColor.RESET + dispname);
			// Bukkit.getServer()
			// .getLogger()
			// .info("name : " + name + ", dispname: " + dispname
			// + ", url : " + url + "," + (skull != null));
			CustomHead headData = new CustomHead(name, dispname, url, skull);

			ret.add(headData);
			customHeads.put(name, headData);
		}
		return ret;
	}

	/**
	 * 与えられた文字に合致する頭を取得する．
	 *
	 * @param s
	 * @return
	 */
	public ItemStack getMobHead(String name) {
		ItemStack ans = customHeads.get(name).skull;
		if (ans == null) {
			notFindWarning(name);
			return customHeads.get("grass").skull.clone();
		} else {
			return ans.clone();
		}
	}

	/**
	 * 与えられた文字に合致する頭のURLを付与する．
	 *
	 * @param s
	 * @return
	 */
	public void setSkull(ItemStack skull, String name) {
		if (!customHeads.containsKey(name)) {
			notFindWarning(name + "という名前のCustomHeadは見つかりません．");
			return;
		}
		setURL(skull, customHeads.get(name).url);
	}

	/**
	 * 与えられた文字に合致する頭のURLを取得する．
	 *
	 * @param name
	 * @return
	 */
	public String getURL(String name) {
		if (!customHeads.containsKey(name)) {
			notFindWarning(name + "という名前のCustomHeadは見つかりません．");
			return null;
		}
		return customHeads.get(name).url;

	}

	/**
	 * 与えられたMOBHEADにURLを付与する．
	 *
	 * @param skull
	 * @param url
	 */
	private void setURL(ItemStack skull, String url) {
		// privateに変更して動作の担保が取れたためコメントアウト
		// // 不正なURLをセットすると、表示したクライアントがクラッシュするため
		// if (url == null || !URLMap.containsValue(url)) {
		// return;
		// }

		ItemMeta meta = skull.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = Base64.getEncoder()
				.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url)
						.getBytes());
		profile.getProperties().put("textures",
				new Property("textures", new String(encodedData)));
		Field profileField;
		try {
			profileField = meta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(meta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException
				| IllegalAccessException e1) {
			e1.printStackTrace();
		}
		skull.setItemMeta(meta);
	}

	// カスタム頭取得
	private ItemStack getSkull(String url) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		setURL(skull, url);
		return skull;
	}

	// 指定したカテゴリのデータを返す
	public HeadCategory getCategoryHeads(String category) {
		if (!categoryData.containsKey(category)) {
			notFindWarning(category + "という名前のCustomHeadCategoryは見つかりません．");
			return categoryData.get("other");
		}

		return categoryData.get(category);
	}

	public Map<String, HeadCategory> getMapCategory() {
		return categoryData;
	}

	// nameに一致する頭が見つからなかった時のwarningメッセージ
	private void notFindWarning(String text) {
		Bukkit.getServer().getLogger().warning(text);
	}

	public ItemStack getPlayerHead(String name) {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta im = is.getItemMeta();
		SkullMeta sm = (SkullMeta)im;
		sm.setOwner(name);
		is.setItemMeta(im);
		return is;
	}
}
