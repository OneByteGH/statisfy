package io.github.onebytegh.statisfy.database;

import java.sql.*;

public class DB {
    private final Connection con;

    public DB(String url, String userpass) throws SQLException {
        con = DriverManager.getConnection(
            url,
            userpass.split(":")[0],
            userpass.split(":")[1]
        );
    }

    public ResultSet query(PreparedStatement qry) throws SQLException {
        return qry.executeQuery();
    }

    public PreparedStatement createStatement(String sql) throws SQLException {
        return this.con.prepareStatement(sql);
    }
}
