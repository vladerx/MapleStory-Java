/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import net.server.Server;
import server.life.MapleMonsterInformationProvider;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

/**
 *
 * @author fives
 */
public class totgevent extends Command {
    {
        setDescription("");
    }
    @Override
    public void execute(MapleClient client, String[] params){
        MapleCharacter player = client.getPlayer();
        long dropid = -1;
        int dropidData= -1;
        int canStartevent = -1;
        int data = -1;
        Date currentDate = new Date();
        int timeNow = (int) (currentDate.getTime() / 1000);
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT status FROM totg")) {/*do not delete expdrop table, just set start value to 0 to reset event*/
                try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                }
                                canStartevent = rs.getInt("status");
                                System.out.println(String.valueOf(canStartevent));

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("noooo");
                }
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(canStartevent == 0){
            if (params.length > 0) {
                player.yellowMessage("Syntax: !totgevent");
                return;
            }
            player.yellowMessage("Tears of the goddess now dropped globally with 24 hours time limit");
            Server.getInstance().broadcastMessage(client.getWorld(), MaplePacketCreator.serverNotice(6, "[Server Notice] Tear of the goddess event is active for 24h and monsters now drop TOTG, you can bring the tears to the goddess for a reward"));
            try {
                Connection con = DatabaseConnection.getConnection();
                try (PreparedStatement ps = con.prepareStatement("UPDATE totg SET startime = ?, endtime = ?, status = ?")) {
                    ps.setInt(1, timeNow);
                    ps.setInt(2, timeNow+86400);
                    ps.setInt(3, 1);
                    ps.executeUpdate();
                } catch (SQLException e) {
                     e.printStackTrace();
                 }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO drop_data_global (continent, itemid, minimum_quantity, maximum_quantity, questid, chance, comments) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, -1);
                    ps.setInt(2, 4000401);
                    ps.setInt(3, 1);
                    ps.setInt(4, 1);
                    ps.setInt(5, 0);
                    ps.setInt(6, 4000*(client.getWorldServer().getDropRate()));
                    ps.setString(7, "TOTG");
                    ps.executeUpdate();
                    data = 1;
                    MapleMonsterInformationProvider.getInstance().clearDrops();
                    MapleMonsterInformationProvider.getInstance().retrieveGlobal();
                } catch (SQLException e) {
                     e.printStackTrace();
                     data = 0;
                 }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (data == 1) {
                try {
                    Connection con = DatabaseConnection.getConnection();
                    try (PreparedStatement ps = con.prepareStatement("SELECT id FROM drop_data_global WHERE itemid = ?")) {
                        ps.setInt(1, 4000401);
                        try (ResultSet rs = ps.executeQuery()) {
                                        if (!rs.next()) {
                                        }
                                        dropid = rs.getLong("id");
                                        dropidData = 1;
                                        System.out.println(String.valueOf(dropid));

                        } catch (SQLException e) {
                            e.printStackTrace();
                            dropidData = 0;
                            System.out.println("noooo");
                        }
                    }
                        con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (dropidData == 1) {
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("UPDATE totg SET dropid = ?")) {
                            ps.setLong(1, dropid);
                            ps.executeUpdate();
                        } catch (SQLException e) {
                             e.printStackTrace();
                         }
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
             player.yellowMessage("Event already active!");
        }
        
    }
}
