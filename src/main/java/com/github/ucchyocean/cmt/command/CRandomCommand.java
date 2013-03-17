/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.cmt.ColorMeTeaming;
import com.github.ucchyocean.cmt.ColorMeTeamingConfig;
import com.github.ucchyocean.cmt.Utility;

/**
 * @author ucchy
 * ColorRandom(CR)コマンドの実行クラス
 */
public class CRandomCommand implements CommandExecutor {

    private static final String[] GROUP_COLORS =
        {"red", "blue", "yellow", "green", "aqua", "gray", "dark_red", "dark_green", "dark_aqua"};

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        // 設定するグループ数を、1番目の引数から取得する
        int numberOfGroups = 2;
        if ( args.length >= 1 && args[0].matches("[2-9]") ) {
            numberOfGroups = Integer.parseInt(args[0]);
        }

        // ゲームモードがクリエイティブの人は除外する
        ArrayList<Player> tempPlayers =
                ColorMeTeaming.getAllPlayersOnWorld(ColorMeTeamingConfig.defaultWorldName);
        ArrayList<Player> players = new ArrayList<Player>();
        for ( Player p : tempPlayers ) {
            if ( p.getGameMode() != GameMode.CREATIVE ) {
                players.add(p);
            }
        }

        // シャッフル
        Random rand = new Random();
        for ( int i=0; i<players.size(); i++ ) {
            int j = rand.nextInt(players.size());
            Player temp = players.get(i);
            players.set(i, players.get(j));
            players.set(j, temp);
        }

        // グループを設定していく
        for ( int i=0; i<players.size(); i++ ) {
            int group = i % numberOfGroups;
            String color = GROUP_COLORS[group];
            ColorMeTeaming.setPlayerColor(players.get(i), color);
        }

        // 各グループに、通知メッセージを出す
        for ( int i=0; i<numberOfGroups; i++ ) {
            ColorMeTeaming.sendTeamChat(GROUP_COLORS[i],
                    "あなたは " +
                    Utility.replaceColors(GROUP_COLORS[i]) +
                    GROUP_COLORS[i] +
                    ChatColor.GREEN +
                    " グループになりました。");
        }

        // 保護領域の更新
        if ( ColorMeTeamingConfig.protectRespawnPointWithWorldGuard ) {
            ColorMeTeaming.wghandler.refreshGroupMembers();
        }

        // メンバー情報の取得
        Hashtable<String, ArrayList<Player>> members =
                ColorMeTeaming.getAllColorMembers();

        // コマンド完了を、CCメッセージで通知する
        CCountCommand.sendCCMessage(sender, members, false);

        return true;
    }

}
