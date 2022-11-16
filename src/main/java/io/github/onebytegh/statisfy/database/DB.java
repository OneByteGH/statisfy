package io.github.onebytegh.statisfy.database;

import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class DB {
    private final Connection con;

    public DB(@NotNull String url, @NotNull String user, String pass) throws SQLException {
        con = DriverManager.getConnection(url, user, pass);
    }

    public ResultSet query(@NotNull PreparedStatement qry) throws SQLException {
        return qry.executeQuery();
    }

    public int update(@NotNull PreparedStatement qry) throws SQLException {
        return qry.executeUpdate();
    }

    public PreparedStatement createStatement(@NotNull String sql) throws SQLException {
        return this.con.prepareStatement(sql);
    }
}
