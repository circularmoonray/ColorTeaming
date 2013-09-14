/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.config.RespawnConfiguration;
import com.github.ucchyocean.ct.config.TPPointConfiguration;
import com.github.ucchyocean.ct.config.TeamMemberSaveDataHandler;
import com.github.ucchyocean.ct.config.TeamNameConfig;
import com.github.ucchyocean.ct.config.TeamNameSetting;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerLeaveEvent.Reason;
import com.github.ucchyocean.ct.item.CustomItem;

/**
 * ColorTeaming APIクラス
 * @author ucchy
 */
public interface ColorTeamingAPI {

    /**
     * スコアボードを返す。
     * @return スコアボード
     */
    public Scoreboard getScoreboard();
    
    /**
     * 指定されたチームIDが存在するかどうかを返す。
     * @param id チームID
     * @return 存在するかどうか
     */
    public boolean isExistTeam(String id);
    
    /**
     * チーム名をチームIDから取得する。
     * @param id チームID
     * @return チーム名
     */
    public TeamNameSetting getTeamNameFromID(String id);
    
    /**
     * Player に設定されている、チームを取得する。
     * @param player プレイヤー
     * @return チーム
     */
    public Team getPlayerTeam(Player player);

    /**
     * Player に設定されている、チームのチーム名を取得する。
     * @param player プレイヤー
     * @return チーム名
     */
    public TeamNameSetting getPlayerTeamName(Player player);

    /**
     * Player にチームを設定する。
     * @param player プレイヤー
     * @param teamName チーム名
     * @return チーム、イベントキャンセルされた場合はnullになることに注意
     */
    public Team addPlayerTeam(Player player, TeamNameSetting teamName);

    /**
     * Player に設定されているチームを削除する。
     * @param player プレイヤー
     * @param reason 離脱理由
     */
    public void leavePlayerTeam(Player player, Reason reason);

    /**
     * フレンドリーファイアの設定。<br>
     * NOTE: 本メソッドは、透明可視化が設定されている場合は、
     * 強制的にfalseになることに注意
     * @param ff trueならフレンドリーファイア有効、falseなら無効
     */
    public void setFriendlyFire(boolean ff);

    /**
     * 仲間の可視化の設定。<br>
     * @param fi trueならフレンドリーファイア有効、falseなら無効
     */
    public void setSeeFriendlyInvisibles(boolean fi);

    /**
     * 指定したチーム名のチームを削除する
     * @param name チームID
     * @return 削除したかどうか（イベントでキャンセルされた場合はfalseになる）
     */
    public boolean removeTeam(String name);

    /**
     * 全てのチームを削除する
     */
    public void removeAllTeam();

    /**
     * ユーザーをチームごとのメンバーに整理して返すメソッド
     * @return チームIDをKey メンバーをValueとした HashMap
     */
    public HashMap<String, ArrayList<Player>> getAllTeamMembers();

    /**
     * チームメンバーを取得する
     * @param id チームID
     * @return チームメンバー。チームが存在しない場合はnullが返されることに注意
     */
    public ArrayList<Player> getTeamMembers(String id);
    
    /**
     * 全てのプレイヤーを取得する
     * @return 全てのプレイヤー
     */
    public ArrayList<Player> getAllPlayers();

    /**
     * 指定したワールドにいる全てのプレイヤーを取得する。
     * ただし、指定したワールドが存在しない場合は、空のリストが返される。
     * @param worldNames 対象にするワールド名
     * @return 全てのプレイヤー
     */
    public ArrayList<Player> getAllPlayersOnWorld(List<String> worldNames);

    /**
     * 全てのチーム名（＝全ての色）を取得する
     * @return 全てのチーム名
     */
    public ArrayList<TeamNameSetting> getAllTeamNames();

    /**
     * メッセージをチームチャットに送信する。
     * @param player 送信元プレイヤー
     * @param message 送信するメッセージ
     */
    public void sendTeamChat(Player player, String message);

    /**
     * 情報をチームチャットに送信する。
     * @param team 送信先のチーム
     * @param message 送信するメッセージ
     */
    public void sendInfoToTeamChat(String team, String message);

    /**
     * サイドバーを新しく作る。
     * もともとサイドバーがあった場合は、削除して再作成される。
     */
    public void makeSidebarScore();

    /**
     * サイドバーを消去する。
     */
    public void removeSidebarScore();

    /**
     * サイドバーのスコアを更新する。
     */
    public void refreshSidebarScore();

    /**
     * タブキーリストのスコアを新しく作る。
     * もともとスコアがあった場合は、削除して再作成される。
     */
    public void makeTabkeyListScore();

    /**
     * タブキーリストのスコアを消去する。
     */
    public void removeTabkeyListScore();

    /**
     * タブキーリストのスコアを更新する。
     */
    public void refreshTabkeyListScore();

    /**
     * 名前下のスコアを新しく作る。
     * もともとスコアがあった場合は、削除して再作成される。
     */
    public void makeBelowNameScore();

    /**
     * 名前下のスコアを消去する。
     */
    public void removeBelowNameScore();

    /**
     * 名前下のスコアを更新する。
     */
    public void refreshBelowNameScore();

    /**
     * TeamMemberSaveDataHandler を取得する
     * @return TeamMemberSaveDataHandler
     */
    public TeamMemberSaveDataHandler getCTSaveDataHandler();

    /**
     * チームのポイント数を全取得する
     * @return チームのポイント数
     */
    public HashMap<String, Integer> getAllTeamPoints();

    /**
     * チームポイントを設定する。
     * @param team チーム名
     * @param point ポイント数
     */
    public void setTeamPoint(String team, int point);
    
    /**
     * チームポイントを増減する。
     * @param team チーム名
     * @param amount ポイント増減量（マイナスでポイント減少）
     * @return 増減後のポイント
     */
    public int addTeamPoint(String team, int amount);

    /**
     * キルデス数やポイントを全てクリアする
     */
    public void clearKillDeathPoints();
    
    /**
     * チーム単位のキルデス数を取得する
     * @return キルデス数
     */
    public HashMap<String, int[]> getKillDeathCounts();

    /**
     * ユーザー単位のキルデス数を取得する
     * @return キルデス数
     */
    public HashMap<String, int[]> getKillDeathUserCounts();

    /**
     * ユーザー単位のキルデス数を設定する
     * @param playerName プレイヤー名
     * @param kill キル数
     * @param death デス数
     * @param tk TK数
     */
    public void setKillDeathUserCounts(String playerName, int kill, int death, int tk);

    /**
     * リーダー設定を全てクリアする
     */
    public void clearLeaders();

    /**
     * リーダー設定を取得する
     * @return リーダー設定
     */
    public HashMap<String, ArrayList<String>> getLeaders();

    /**
     * リスポーン設定を取得する
     * @return リスポーン設定
     */
    public RespawnConfiguration getRespawnConfig();

    /**
     * TP地点設定を取得する
     * @return TP地点設定
     */
    public TPPointConfiguration getTppointConfig();
    
    /**
     * チーム名設定を取得する
     * @return チーム名設定
     */
    public TeamNameConfig getTeamNameConfig();

    /**
     * リスポーンマップ名を取得する
     * @return リスポーンマップ名
     */
    public String getRespawnMapName();

    /**
     * リスポーンマップ名を設定する
     * @param respawnMapName リスポーンマップ名
     */
    public void setRespawnMapName(String respawnMapName);
    
    /**
     * カスタムアイテムを登録する
     * @param item カスタムアイテム
     */
    public void registerCustomItem(CustomItem item);
    
    /**
     * カスタムアイテムを登録する
     * @param item 登録するアイテム
     * @param name アイテム名
     * @param displayName 表示アイテム名
     */
    public void registerCustomItem(ItemStack item, String name, String displayName);
    
    /**
     * 登録されているカスタムアイテムを取得する
     * @param name カスタムアイテム名
     * @return カスタムアイテム、登録されていないアイテム名を指定した場合はnullが返される。
     */
    public CustomItem getCustomItem(String name);

    /**
     * ランダムな順序で、プレイヤーをチームわけします。<br/>
     * 既にチームわけが存在する場合は、全部クリアしてから分けられます。
     * @param players チームわけを行うプレイヤー
     * @param teamNum チーム数（2から9までの数を指定可能です）
     */
    public void makeColorTeamsWithRandomSelection(ArrayList<Player> players, int teamNum);

    /**
     * 指定されたプレイヤー順序で、プレイヤーをチームわけします。<br/>
     * 既にチームわけが存在する場合は、全部クリアしてから分けられます。
     * @param players チームわけを行うプレイヤー
     * @param teamNum チーム数（2から9までの数を指定可能です）
     */
    public void makeColorTeamsWithOrderSelection(ArrayList<Player> players, int teamNum);
    
    /**
     * 既存のチームわけをそのままに、指定されたプレイヤーを既存のチームへ加えていきます。<br/>
     * プレイヤーはランダムな順序で追加が行われます。<br/>
     * 加えられる先のチームは、人数の少ないチームが選択されます。
     * 同数の場合はその中からランダムに選択されます。
     * @param players チームに加えるプレイヤー
     * @return 最後まで処理が行われたかどうか
     */
    public boolean addPlayerToColorTeamsWithRandomSelection(ArrayList<Player> players);
    
    /**
     * 既存のチームわけをそのままに、指定されたプレイヤーを既存のチームへ加えていきます。<br/>
     * プレイヤーは指定の順序で追加が行われます。<br/>
     * 加えられる先のチームは、人数の少ないチームが選択されます。
     * 同数の場合はその中からランダムに選択されます。
     * @param players チームに加えるプレイヤー
     * @return 最後まで処理が行われたかどうか
     */
    public boolean addPlayerToColorTeamsWithOrderSelection(ArrayList<Player> players);
    
    /**
     * ColorTeamingの設定ファイルを全て再読み込みする
     */
    public void realod();
}
