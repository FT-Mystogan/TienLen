package main.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.dto.PlayerData;

public class UserDB {

    public UserDB() {
    }

    public PlayerData checkLogin(Connection conn, String user, String pass) {
        PlayerData p = null;
        String sql = "SELECT * FROM playerdata WHERE username=? AND password=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            p = new PlayerData();
            while (rs.next()) {
                p.setId(rs.getInt("ID"));
                p.setMoney(rs.getFloat("coin"));
                p.setPlayerName(rs.getString("name"));
                return p;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public float getCoin(Connection conn, int id) {
        float coin = 0;
        String sql = "SELECT coin FROM playerdata WHERE id=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                coin = rs.getFloat("coin");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return coin;
    }

    public void setCoin(Connection conn, int id, float coin) {
        String sql = "UPDATE playerdata SET coin=? WHERE ID=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setFloat(1, coin);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String checkIsLogin(Connection conn, String user, String pass) {
        String isLogin = "";
        String sql = "SELECT islogin FROM playerdata WHERE username=? AND password=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                isLogin = rs.getString("islogin");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isLogin;
    }

    public void setIsLogin(Connection conn, int id, String isLogin) {
        String sql = "UPDATE playerdata SET islogin=? WHERE ID=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, isLogin);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIsLoginAndRoom(Connection conn, int id, String isLogin, int idRoom) {
        String sql = "UPDATE playerdata SET islogin=?,idroom=? WHERE ID=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, isLogin);
            ps.setInt(2, idRoom);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIsLogin(Connection conn, String isLogin) {
        String sql = "UPDATE playerdata SET islogin=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, isLogin);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIdRoom(Connection conn, int id, int idRoom) {
        String sql = "UPDATE playerdata SET idroom=? WHERE ID=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idRoom);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIdRoom(Connection conn, int idRoom) {
        String sql = "UPDATE playerdata SET idroom=?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idRoom);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PlayerData> listPlayer(Connection conn) {
        List<PlayerData> list = new ArrayList<>();
        String sql = "SELECT * FROM playerdata";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PlayerData pd = new PlayerData();
                pd.setPlayerName(rs.getString("name"));
                pd.setMoney(rs.getFloat("coin"));
                list.add(pd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void setPlayer(Connection conn) {
        String sql = "SELECT COUNT(*) AS size FROM playerdata";
        PreparedStatement ps = null;
        int n = 1;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (!rs.next()) {
                n = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 1; i <= n; i++) {
            setIdRoom(conn, i, -1);
        }
    }
}
