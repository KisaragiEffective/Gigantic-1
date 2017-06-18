package com.github.unchama.player.seichiskill.moduler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.entity.Player;

/**
 * @author tar0ss
 *
 */
public class BreakRange {

	// 破壊する体積情報
	private Volume volume;
	// 基準
	private Coordinate zeropoint;
	// 破壊する全ての相対座標リスト
	private LinkedHashMap<CardinalDirection, List<Coordinate>> coordmap;
	// 破壊するブロックを囲う相対座標リスト
	private LinkedHashMap<CardinalDirection, List<Coordinate>> surroundmap;
	// 破壊する一番上の相対座標リスト
	private LinkedHashMap<CardinalDirection, List<Coordinate>> topmap;
	// surrondmapのtop部分
	private LinkedHashMap<CardinalDirection, List<Coordinate>> topsurroundmap;

	public BreakRange() {
		this(new Volume(), new Coordinate());
	}

	/**
	 * @param volume
	 * @param zeropoint
	 * @param coordmap
	 */
	public BreakRange(Volume volume, Coordinate zeropoint) {
		this.volume = volume;
		this.zeropoint = zeropoint;
		this.refresh();
	}

	/**
	 * 得られたvolume，起点zeropointからcoordmapを生成します．
	 *
	 */
	public void refresh() {
		coordmap = new LinkedHashMap<CardinalDirection, List<Coordinate>>();
		coordmap.put(CardinalDirection.SOUTH, new ArrayList<Coordinate>());
		coordmap.put(CardinalDirection.WEST, new ArrayList<Coordinate>());
		coordmap.put(CardinalDirection.NORTH, new ArrayList<Coordinate>());
		coordmap.put(CardinalDirection.EAST, new ArrayList<Coordinate>());
		surroundmap = new LinkedHashMap<CardinalDirection, List<Coordinate>>();
		surroundmap.put(CardinalDirection.SOUTH, new ArrayList<Coordinate>());
		surroundmap.put(CardinalDirection.WEST, new ArrayList<Coordinate>());
		surroundmap.put(CardinalDirection.NORTH, new ArrayList<Coordinate>());
		surroundmap.put(CardinalDirection.EAST, new ArrayList<Coordinate>());
		topmap = new LinkedHashMap<CardinalDirection, List<Coordinate>>();
		topmap.put(CardinalDirection.SOUTH, new ArrayList<Coordinate>());
		topmap.put(CardinalDirection.WEST, new ArrayList<Coordinate>());
		topmap.put(CardinalDirection.NORTH, new ArrayList<Coordinate>());
		topmap.put(CardinalDirection.EAST, new ArrayList<Coordinate>());
		topsurroundmap = new LinkedHashMap<CardinalDirection, List<Coordinate>>();
		topsurroundmap
				.put(CardinalDirection.SOUTH, new ArrayList<Coordinate>());
		topsurroundmap.put(CardinalDirection.WEST, new ArrayList<Coordinate>());
		topsurroundmap
				.put(CardinalDirection.NORTH, new ArrayList<Coordinate>());
		topsurroundmap.put(CardinalDirection.EAST, new ArrayList<Coordinate>());
		for (int y = volume.getHeight() - zeropoint.getY(); y >= -zeropoint
				.getY() - 1; y--) {
			for (int x = -zeropoint.getX() - 1; x <= volume.getWidth()
					- zeropoint.getX(); x++) {
				for (int z = -zeropoint.getZ() - 1; z <= volume.getDepth()
						- zeropoint.getZ(); z++) {
					Coordinate coord = new Coordinate(x, y, z);
					// 周囲の部分
					if (y == volume.getHeight() - zeropoint.getY()
							|| y == -zeropoint.getY() - 1
							|| x == -zeropoint.getX() - 1
							|| x == volume.getWidth() - zeropoint.getX()
							|| z == -zeropoint.getZ() - 1
							|| z == volume.getDepth() - zeropoint.getZ()) {
						surroundmap.get(CardinalDirection.SOUTH).add(coord);
						coord = coord.rotateXZ(zeropoint);
						surroundmap.get(CardinalDirection.WEST).add(coord);
						coord = coord.rotateXZ(zeropoint);
						surroundmap.get(CardinalDirection.NORTH).add(coord);
						coord = coord.rotateXZ(zeropoint);
						surroundmap.get(CardinalDirection.EAST).add(coord);
						coord = new Coordinate(x, y, z);
						if (y == volume.getHeight() - zeropoint.getY()) {
							// surroundトップ部分
							topsurroundmap.get(CardinalDirection.SOUTH).add(coord);
							coord = coord.rotateXZ(zeropoint);
							topsurroundmap.get(CardinalDirection.WEST).add(coord);
							coord = coord.rotateXZ(zeropoint);
							topsurroundmap.get(CardinalDirection.NORTH).add(coord);
							coord = coord.rotateXZ(zeropoint);
							topsurroundmap.get(CardinalDirection.EAST).add(coord);
						}
					} else {
						// 通常破壊の部分
						coordmap.get(CardinalDirection.SOUTH).add(coord);
						coord = coord.rotateXZ(zeropoint);
						coordmap.get(CardinalDirection.WEST).add(coord);
						coord = coord.rotateXZ(zeropoint);
						coordmap.get(CardinalDirection.NORTH).add(coord);
						coord = coord.rotateXZ(zeropoint);
						coordmap.get(CardinalDirection.EAST).add(coord);
						coord = new Coordinate(x, y, z);
						if (y == volume.getHeight() - zeropoint.getY() - 1) {
							// 通常破壊トップ部分
							topmap.get(CardinalDirection.SOUTH).add(coord);
							coord = coord.rotateXZ(zeropoint);
							topmap.get(CardinalDirection.WEST).add(coord);
							coord = coord.rotateXZ(zeropoint);
							topmap.get(CardinalDirection.NORTH).add(coord);
							coord = coord.rotateXZ(zeropoint);
							topmap.get(CardinalDirection.EAST).add(coord);
						}
					}



				}
			}
		}

	}

	/**
	 * プレイヤーが破壊する最高高度の相対座標リストを取得します．
	 *
	 * @param player
	 * @return
	 */
	public List<Coordinate> getTopCoordList(Player player) {
		CardinalDirection cd = CardinalDirection.getCardinalDirection(player);
		return topmap.get(cd);
	}

	/**
	 * プレイヤーが破壊する相対座標リストを取得します．
	 *
	 * @param player
	 * @return
	 */
	public List<Coordinate> getBreakCoordList(Player player) {
		CardinalDirection cd = CardinalDirection.getCardinalDirection(player);
		return coordmap.get(cd);
	}

	/**
	 * プレイヤーが破壊する範囲の周囲の相対座標リストを取得します．
	 *
	 * @param player
	 * @return
	 */
	public List<Coordinate> getSurroundCoordList(Player player) {
		CardinalDirection cd = CardinalDirection.getCardinalDirection(player);
		return surroundmap.get(cd);
	}

	/**
	 * プレイヤーが破壊する範囲の周囲のTOP部分の相対座標リストを取得します．
	 *
	 * @param player
	 * @return
	 */
	public List<Coordinate> getTopSurroundCoordList(Player player) {
		CardinalDirection cd = CardinalDirection.getCardinalDirection(player);
		return topsurroundmap.get(cd);
	}

	/**
	 * @return volume
	 */
	public Volume getVolume() {
		return volume;
	}

	/**
	 * @param volume
	 *            セットする volume
	 */
	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	/**
	 * @return zeropoint
	 */
	public Coordinate getZeropoint() {
		return zeropoint;
	}

	/**
	 * @param zeropoint
	 *            セットする zeropoint
	 */
	public void setZeropoint(Coordinate zeropoint) {
		this.zeropoint = zeropoint;
	}

}
