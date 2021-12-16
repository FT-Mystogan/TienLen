/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import main.tools.Room;

/**
 *
 * @author Cuong
 */
public class RoomDB {

    public ArrayList<Room> getAllRoom(Connection conn) {
        ArrayList<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM Room";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room(rs.getInt(1), rs.getFloat(2));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        Connection conn = ConnectDB.getConnect();
        RoomDB r = new RoomDB();
        ArrayList<Room> list = r.getAllRoom(conn);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getBet());
        }
    }
}
