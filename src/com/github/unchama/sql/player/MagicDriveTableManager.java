package com.github.unchama.sql.player;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.seichiskill.active.MagicDriveManager;
import com.github.unchama.player.seichiskill.moduler.BreakRange;
import com.github.unchama.player.seichiskill.moduler.Coordinate;
import com.github.unchama.player.seichiskill.moduler.Volume;
import com.github.unchama.sql.Sql;
import com.github.unchama.sql.moduler.PlayerTableManager;

/**
 * @author tar0ss
 *
 */
public class MagicDriveTableManager extends PlayerTableManager{

	public MagicDriveTableManager(Sql sql) {
		super(sql);
	}

	@Override
	protected String addColumnCommand() {
		String command = "";
		// MagecDrive
		command += "add column if not exists width int default 1,"
				+ "add column if not exists depth int default 1,"
				+ "add column if not exists height int default 1,"
				+ "add column if not exists zero_x int default 0,"
				+ "add column if not exists zero_y int default 0,"
				+ "add column if not exists zero_z int default 0,"
				+ "add column if not exists unlocked boolean default false,"
				;

		return command;
	}

	@Override
	protected boolean newPlayer(GiganticPlayer gp) {
		MagicDriveManager m = gp.getManager(MagicDriveManager.class);
		m.setRange(new BreakRange());
		return true;
	}

	@Override
	public void loadPlayer(GiganticPlayer gp, ResultSet rs) throws SQLException {
		MagicDriveManager m = gp.getManager(MagicDriveManager.class);
		m.setRange(new BreakRange(
				new Volume(rs.getInt("width"), rs.getInt("depth"), rs.getInt("height")),
				new Coordinate(rs.getInt("zero_x"), rs.getInt("zero_y"), rs.getInt("zero_z"))
		));

		m.unlocked(rs.getBoolean("unlocked"));
	}

	@Override
	protected String saveCommand(GiganticPlayer gp) {
		String command = "";
		// MagecDrive
		MagicDriveManager m = gp.getManager(MagicDriveManager.class);
		BreakRange range = m.getRange();
		command += "width = '" + range.getVolume().getWidth() + "',"
				+ "depth = '" + range.getVolume().getDepth() + "',"
				+ "height = '" + range.getVolume().getHeight() + "',"
				+ "zero_x = '" + range.getZeropoint().getX() + "',"
				+ "zero_y = '" + range.getZeropoint().getY() + "',"
				+ "zero_z = '" + range.getZeropoint().getZ() + "',"
				+ "unlocked = " + Boolean.toString(m.isunlocked()) + ",";

		return command;
	}

}
