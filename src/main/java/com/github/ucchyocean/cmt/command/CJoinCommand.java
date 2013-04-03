/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.cmt.ColorMeTeaming;
import com.github.ucchyocean.cmt.ColorMeTeamingConfig;
import com.github.ucchyocean.cmt.Utility;

/**
 * @author ucchy
 * colorjoin(cjoin)コマンドの実行クラス
 */
public class CJoinCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( !(sender instanceof Player) ) {
            sender.sendMessage(
                    PREERR + "このコマンドはゲーム内からのみ実行可能です。");
            return true;
        }

        Player player = (Player)sender;

        String group = "";
        if ( args.length == 0 || args[0].equalsIgnoreCase("random") ) {

            if ( !ColorMeTeamingConfig.allowPlayerJoinRandom ) {
                player.sendMessage(
                        PREERR +
                        "cjoinコマンドによるランダム参加は、許可されておりません。");
                return true;
            }

            group = getLeastGroup();
            if ( group != null ) {
                ColorMeTeaming.setPlayerColor(player, group);
                player.sendMessage(
                        ChatColor.GREEN + "あなたは " +
                        Utility.replaceColors(group) +
                        group +
                        ChatColor.GREEN +
                        " グループになりました。");
                return true;
            } else {
                sender.sendMessage(
                        PREERR + "設定できるグループが無いようです。");
                return true;
            }

        } else {

            if ( !ColorMeTeamingConfig.allowPlayerJoinAny ) {
                player.sendMessage(
                        PREERR +
                        "cjoin (group) コマンドによる任意グループへの参加は、許可されておりません。");
                return true;
            }

            group = args[0];
            if ( !Utility.isValidColor(group) || ColorMeTeamingConfig.ignoreGroups.contains(group) ) {
                sender.sendMessage(PREERR + "グループ " + group + " は設定できないグループ名です。");
                return true;
            }
            ColorMeTeaming.setPlayerColor(player, group);
            player.sendMessage(
                    ChatColor.GREEN + "あなたは " +
                    Utility.replaceColors(group) +
                    group +
                    ChatColor.GREEN +
                    " グループになりました。");
            return true;

        }
    }


    /**
     * メンバー人数が最小のグループを返す。
     * @return メンバー人数が最小のグループ
     */
    private String getLeastGroup() {

        Hashtable<String, ArrayList<Player>> members =
                ColorMeTeaming.getAllColorMembers();
        int least = 999;
        String leastGroup = null;

        ArrayList<String> groups = new ArrayList<String>(members.keySet());
        // ランダム要素を入れるため、グループ名をシャッフルする
        Collections.shuffle(groups);

        for ( String group : groups ) {
            if ( least > members.get(group).size() ) {
                least = members.get(group).size();
                leastGroup = group;
            }
        }

        return leastGroup;
    }
}
