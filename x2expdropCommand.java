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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.server.Server;
import server.MapleItemInformationProvider;
import server.life.MapleMonsterInformationProvider;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

/**
 *
 * @author fives
 */
public class x2expdropCommand extends Command {
    public static boolean isEventOn = false;
    {
        setDescription("");
    }
    @Override
    public void execute(MapleClient client, String[] params){
        int eventdur = 0;
        int canStartx2 = -1;
        Date currentDate = new Date();
        int timeNow = (int) (currentDate.getTime() / 1000);
        MapleCharacter player = client.getPlayer();
        if (params.length != 1) {
                player.yellowMessage("Syntax: !x2expdrop [duration in hours]");
                return;
        } else {
                try {
                    eventdur = Integer.parseInt(params[0]);
                } catch (NumberFormatException e){
                    player.yellowMessage("Syntax: !x2expdrop can include only numbers!");
                    return;
                }
                if (eventdur > 0 && eventdur < 25){
                    eventdur = Integer.parseInt(params[0]);
                } else {
                    player.yellowMessage("!x2expdrop duration must be 24 hours at max!");
                    return;
                }
                try {
                    Connection con = DatabaseConnection.getConnection();
                    try (PreparedStatement ps = con.prepareStatement("SELECT start from x2exp")) {
                        try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                }
                                canStartx2 = rs.getInt("start");

                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("noooo");
                        }
                    } catch (SQLException e) {
                         e.printStackTrace();
                    }
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (canStartx2 == 0){
                    for (MapleCharacter victim : player.getWorldServer().getPlayerStorage().getAllCharacters()) {
                        victim.announce(MaplePacketCreator.getClock(eventdur*3600));
                    }
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("UPDATE x2exp SET start = ?, endtime = ?")) {
                            ps.setInt(1, 1);
                            ps.setInt(2, timeNow+(eventdur*3600));
                            ps.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                            con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    client.getWorldServer().setExpRate(client.getWorldServer().getExpRate()*2);
                    client.getWorldServer().setDropRate(client.getWorldServer().getDropRate()*2);
                    client.getPlayer().yellowMessage("You have started a "+params[0]+" hours x2 exp and x2 drop event");
                    Server.getInstance().broadcastMessage(client.getWorld(), MaplePacketCreator.serverNotice(6, "[Server Notice] x2 Exp and x2 drop event is now active for "+params[0]+" hours enjoy your time."));
                } else if (canStartx2 == 1) {
                    client.getPlayer().yellowMessage("x2 exp and x2 drop event is already active!");
                }
   
        }
    }
}
