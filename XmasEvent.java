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
import java.util.concurrent.ScheduledFuture;
import net.server.Server;
import server.TimerManager;
import server.life.MapleMonsterInformationProvider;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

/**
 *
 * @author fives
 */
public class XmasEvent extends Command { 
    private static boolean iseventon = false;
    private ScheduledFuture<?> checkeventend = null;
    {
        setDescription("");
    }
    @Override
    public void execute(MapleClient client, String[] params){
        MapleCharacter player = client.getPlayer();
        int[] xmasglobdrops = {4031313,2020034,2022470,3991001,3991002,3991003,3991004,3991005,3991007,3991008,3991009,3991011,3991012,3991013,3991014,3991015,3991017,3991018,3991019,3991020,3991021,3991022,3991024};
        if (params.length != 1) {
            player.yellowMessage("Syntax: !xmas 1/0");
            return;
        }
        int seton = Integer.parseInt(params[0]);
        if (seton == 1){
            if (iseventon == false){
                iseventon = true;
                player.yellowMessage("you have started xmas event");
                Server.getInstance().broadcastMessage(client.getWorld(), MaplePacketCreator.serverNotice(6, "[Server Notice] Christmas event is now active for 12 days"));
                for (int gdrop : xmasglobdrops){
                    int rate = 0;
                    if (gdrop == 2022470){
                        rate = 5000;
                    }else if(gdrop == 2020034){
                        rate = 500;
                    }else if(gdrop == 4031313){
                        rate = 200;
                    } else {
                        rate = 300;
                    }
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO drop_data_global (continent, itemid, minimum_quantity, maximum_quantity, questid, chance, comments) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                            ps.setInt(1, -1);
                            ps.setInt(2, gdrop);
                            ps.setInt(3, 1);
                            ps.setInt(4, 1);
                            ps.setInt(5, 0);
                            ps.setInt(6, rate*(client.getWorldServer().getDropRate()));
                            ps.setString(7, "xmas event");
                            ps.executeUpdate();
                            MapleMonsterInformationProvider.getInstance().clearDrops();
                            MapleMonsterInformationProvider.getInstance().retrieveGlobal();
                        } catch (SQLException e) {
                             e.printStackTrace();
                         }
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                checkeventend = TimerManager.getInstance().schedule(new Runnable() {
                    public void run() {
                        iseventon = false;
                        for (int gdrop : xmasglobdrops){
                            try {
                                Connection con = DatabaseConnection.getConnection();
                                try (PreparedStatement ps = con.prepareStatement("DELETE FROM drop_data_global WHERE itemid = ?")) {
                                    ps.setInt(1, gdrop);
                                    ps.executeUpdate();
                                    MapleMonsterInformationProvider.getInstance().clearDrops();
                                    MapleMonsterInformationProvider.getInstance().retrieveGlobal();
                                } catch (SQLException e) {
                                     e.printStackTrace();
                                }
                                con.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        Server.getInstance().broadcastMessage(client.getWorld(), MaplePacketCreator.serverNotice(6, "[Server Notice] Christmas event has ended!"));
                    }
                }, 1036800000); 
            } else {
                player.yellowMessage("x mas Event already active!");
            }
        } else {
            if (iseventon == true){
                player.yellowMessage("x mas Event is cancelled!");
                iseventon = false;
                checkeventend.cancel(true);
                for (int gdrop : xmasglobdrops){
                    try {
                        Connection con = DatabaseConnection.getConnection();
                        try (PreparedStatement ps = con.prepareStatement("DELETE FROM drop_data_global WHERE itemid = ?")) {
                            ps.setInt(1, gdrop);
                            ps.executeUpdate();
                            MapleMonsterInformationProvider.getInstance().clearDrops();
                            MapleMonsterInformationProvider.getInstance().retrieveGlobal();
                        } catch (SQLException e) {
                             e.printStackTrace();
                        }
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                player.yellowMessage("cannot cancel inactive event!");
            }
        }
    }
    public boolean isEventOn(){
        return iseventon;
    }
}


