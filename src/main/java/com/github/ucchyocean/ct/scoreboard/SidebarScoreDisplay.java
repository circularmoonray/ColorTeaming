/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * サイドバーにスコアを表示するためのAPIクラス
 * @author ucchy
 */
public class SidebarScoreDisplay {

    private Objective objective;
    private ColorTeaming plugin;
    
    private HashMap<String, SidebarTeamScore> teamscores;

    /**
     * コンストラクタ。コンストラクト時に、現在のチーム状況を取得し、
     * サイドバーを初期化、表示する。
     */
    public SidebarScoreDisplay(ColorTeaming plugin) {

        this.plugin = plugin;

        // Scoreboardからobjective取得。null の場合は再作成する。
        Scoreboard scoreboard = plugin.getAPI().getScoreboard();
        objective = scoreboard.getObjective("teamscore");
        if ( objective == null ) {
            objective = scoreboard.registerNewObjective("teamscore", "");
            objective.setDisplayName("チームスコア");
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // 項目を初期化
        teamscores = new HashMap<String, SidebarTeamScore>();

        ArrayList<TeamNameSetting> teamNames = plugin.getAPI().getAllTeamNames();
        for ( TeamNameSetting tns : teamNames ) {
            setScore(tns.getID(), 0);
        }
        
        refreshCriteria();
    }

    /**
     * サイドバーのクライテリアを、ColorTeamingConfigから取得し、更新する。
     */
    private void refreshCriteria() {

        SidebarCriteria criteria = plugin.getCTConfig().getSideCriteria();

        if ( criteria == SidebarCriteria.NONE ) {
            plugin.getAPI().removeSidebarScore();
            return;
        }

        objective.setDisplayName(
                ChatColor.ITALIC.toString() + ChatColor.YELLOW.toString() +
                criteria.getSidebarTitle() + ChatColor.RESET);

        refreshScore();
    }

    /**
     * スコアを再取得し、表示更新する。
     * スコアが更新されるタイミング（プレイヤー死亡時、ログアウト時）に、
     * 本メソッドを呼び出してスコア表示を更新すること。
     */
    public void refreshScore() {

        switch (plugin.getCTConfig().getSideCriteria()) {
        case KILL_COUNT:
            refreshScoreByKillOrDeathCount(SidebarCriteria.KILL_COUNT);
            break;
        case DEATH_COUNT:
            refreshScoreByKillOrDeathCount(SidebarCriteria.DEATH_COUNT);
            break;
        case POINT:
            refreshScoreByPoint();
            break;
        case REST_PLAYER:
            refreshScoreByRestPlayerCount();
            break;
        case NONE:
            break; // do nothing.
        }
    }

    /**
     * キル数、または、デス数による、スコア更新を行う
     * @param criteria
     */
    private void refreshScoreByKillOrDeathCount(SidebarCriteria criteria) {

        int index;
        if ( criteria == SidebarCriteria.KILL_COUNT ) {
            index = 0;
        } else {
            index = 1;
        }

        HashMap<String, int[]> killDeathCounts =
                plugin.getAPI().getKillDeathCounts();
        
        for ( String key : killDeathCounts.keySet() ) {
            int[] data = killDeathCounts.get(key);
            setScore(key, data[index]);
        }
    }

    /**
     * ポイントによるスコア更新を行う
     */
    private void refreshScoreByPoint() {

        HashMap<String, Integer> teamPoints = 
                plugin.getAPI().getAllTeamPoints();

        for ( String key : teamPoints.keySet() ) {
            setScore(key, teamPoints.get(key));
        }
    }

    /**
     * 残り人数によるスコア更新を行う
     */
    private void refreshScoreByRestPlayerCount() {
        
        HashMap<String, ArrayList<Player>> members =
                plugin.getAPI().getAllTeamMembers();
        
        for ( String key : members.keySet() ) {
            setScore(key, members.get(key).size());
        }
    }
    
    /**
     * 項目のスコアを設定する
     * @param name 項目名
     * @param point ポイント
     */
    private void setScore(String name, int point) {
        
        ColorTeamingAPI api = plugin.getAPI();
        
        SidebarTeamScore ts;
        if ( !teamscores.containsKey(name) ) {
            Team team = api.getScoreboard().getTeam(name);
            if ( team == null ) {
                return;
            }
            ts = new SidebarTeamScore(team);
            teamscores.put(name, ts);
        } else {
            ts = teamscores.get(name);
        }
        
        if ( point == 0 ) {
            // NOTE: 全ての項目に0を設定すると表示が消えるので、
            //       一旦1を設定することで回避する。
            objective.getScore(ts).setScore(1);
        }
        
        objective.getScore(ts).setScore(point);
    }

    /**
     * サイドバーの表示を消去する。
     */
    public void remove() {
        if ( plugin.getAPI().getScoreboard().getObjective("teamscore") != null ) {
            objective.unregister();
            objective = null;
        }
    }
}
