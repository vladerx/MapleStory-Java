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
import tools.DatabaseConnection;

/**
 *
 * @author fives
 */
public class useent3 extends Command {
    {
        setDescription("");
    }
    @Override
    public void execute(MapleClient client, String[] params){
        MapleCharacter player = client.getPlayer();
        int reput = -1;
        int enta = 1;
        int entid = -1;
        Date currentDate = new Date();
        long timeNow = currentDate.getTime();
        if (params.length != 0) {
                player.yellowMessage("Syntax: @useent3");
                return;
        } else {
                try {
                    Connection con = DatabaseConnection.getConnection();
                    try (PreparedStatement ps = con.prepareStatement("SELECT * FROM family_entitlement WHERE entitlementid = ?")) {
                        ps.setInt(1, 3);
                        try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                }
                                else {
                                    entid = rs.getInt("charid");
                                    System.out.println(entid);
                                    if (entid == player.getId()){
                                        enta = 0;
                                    }
                                }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("empty result set");
                        }
                    } catch (SQLException e) {
                         e.printStackTrace();
                    }
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            if (enta == 1){
                try {
                    Connection con = DatabaseConnection.getConnection();
                    try (PreparedStatement ps = con.prepareStatement("SELECT reputation FROM family_character WHERE cid = ?")) {
                        ps.setInt(1, player.getId());
                        try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                }
                                else {
                                    reput = rs.getInt("reputation");
                                }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                         e.printStackTrace();
                    }
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (reput >= 700){
                    player.getFamilyEntry().setReputation(reput-700);
                    player.getFamilyEntry().saveReputation();
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO family_entitlement (charid, entitlementid, timestamp) VALUES (?,?,?)")) {
                                ps.setInt(1, player.getId());
                                ps.setInt(2, 3);
                                ps.setLong(3, timeNow);
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
                            try (PreparedStatement ps = con.prepareStatement("INSERT INTO fambuff (charid, endtime, type) VALUES (?,?,?)")) {
                                    ps.setInt(1, player.getId());
                                    ps.setLong(2, timeNow+900000);//its long
                                    ps.setInt(3, 3);
                                    ps.executeUpdate();

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    player.setPlayerDrop(2);
                    player.yellowMessage("You have used 700 Rep Points for entitlement 3: you gained x2 drop for 15 mins");
                } else {
                    player.yellowMessage("not enough Rep Points entitlement 3 requires 700 Rep Points");
                }
            } else {
                player.yellowMessage("you cant use entitlement 3 yet");
            }
        }
    }
}