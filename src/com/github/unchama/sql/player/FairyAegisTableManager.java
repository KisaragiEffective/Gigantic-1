package com.github.unchama.sql.player;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.unchama.player.GiganticPlayer;
import com.github.unchama.player.seichiskill.active.FairyAegisManager;
import com.github.unchama.sql.Sql;
import com.github.unchama.sql.moduler.PlayerTableManager;

/**
 * @author tar0ss
 *
 */
public class FairyAegisTableManager extends PlayerTableManager{

	public FairyAegisTableManager(Sql sql) {
		super(sql);
	}

	@Override
	protected String addColumnCommand() {
		String command = "";
		command += "add column if not exists breaknum int default 1000,"
				+ "add column if not exists unlocked boolean default false,"
				;

		return command;
	}

	@Override
	protected boolean newPlayer(GiganticPlayer gp) {
		FairyAegisManager m = gp.getManager(FairyAegisManager.class);
		m.setBreakNum(m.getDefaultBreakNum());
		return true;
	}

	@Override
	public void loadPlayer(GiganticPlayer gp, ResultSet rs) throws SQLException {
		FairyAegisManager m = gp.getManager(FairyAegisManager.class);
		m.setBreakNum(rs.getInt("breaknum"));
		m.unlocked(rs.getBoolean("unlocked"));
	}

	@Override
	protected String saveCommand(GiganticPlayer gp,boolean loginflag) {
		String command = "";
		FairyAegisManager m = gp.getManager(FairyAegisManager.class);
		command += "breaknum = '" + m.getBreakNum() + "',"
				+ "unlocked = " + Boolean.toString(m.isunlocked()) + ",";
		return command;
	}

}
