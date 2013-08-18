/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerLeaveEvent.Reason;

/**
 * csaveコマンドやcrestoreコマンドで扱う、チームメンバー保存データのハンドルクラス
 * @author ucchy
 */
public class TeamMemberSaveDataHandler {

    private File saveDir;

    /**
     * コンストラクタ
     * @param saveDir セーブデータを保存するフォルダ
     */
    public TeamMemberSaveDataHandler(File dataFolder) {

        this.saveDir = new File(dataFolder.getAbsolutePath(), "saves");

        if ( !saveDir.exists() ) {
            saveDir.mkdirs();
        }
    }

    /**
     * メンバー情報の保存
     * @param name 保存名
     * @return 保存が成功したかどうか
     */
    public boolean save(String name) {

        YamlConfiguration config = new YamlConfiguration();
        ColorTeamingAPI api = ColorTeaming.instance.getAPI();

        // メンバー情報の保存
        HashMap<String, ArrayList<Player>> members = api.getAllTeamMembers();

        for ( String key : members.keySet() ) {

            // プレイヤーを名前のリストに変換する
            ArrayList<String> names = new ArrayList<String>();
            for ( Player p : members.get(key) ) {
                names.add(p.getName());
            }

            config.set("members." + key, names);
        }

        // チームキルデス数の保存
        HashMap<String, int[]> killDeathCounts = api.getKillDeathCounts();
        for ( String key : killDeathCounts.keySet() ) {
            List<Integer> data =
                    convertToList(killDeathCounts.get(key));
            config.set("killDeathCounts." + key, data);
        }

        // ユーザーキルデス数の保存
        HashMap<String, int[]> killDeathUserCounts = api.getKillDeathUserCounts();
        for ( String key : killDeathUserCounts.keySet() ) {
            List<Integer> data =
                    convertToList(killDeathUserCounts.get(key));
            config.set("killDeathUserCounts." + key, data);
        }

        // セーブデータをファイルへ保存
        File file = new File(saveDir, name + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * メンバー情報の復帰
     * @param name メンバー保存情報の保存名
     * @return 復帰できたかどうか
     */
    public boolean load(String name) {

        if ( !isExist(name) ) {
            return false;
        }

        // ファイルからYamlConfigurationをロード
        File file = new File(saveDir, name + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // メンバー情報の復帰
        ConfigurationSection msection = config.getConfigurationSection("members");
        if ( msection == null ) {
            return false;
        }

        clearAllUsers(); // 一旦、全てのユーザーのグループを解散する

        ColorTeamingAPI api = ColorTeaming.instance.getAPI();

        Iterator<String> groups = msection.getValues(false).keySet().iterator();
        while (groups.hasNext()) {
            String group = groups.next();

            List<String> groupMembers = msection.getStringList(group);
            for ( String pname : groupMembers ) {
                Player player = Bukkit.getPlayerExact(pname);
                if ( player != null ) {
                    api.addPlayerTeam(player, group);
                }
            }
        }

        // チームキルデス数の復帰
        ConfigurationSection tksection =
                config.getConfigurationSection("killDeathCounts");
        if ( tksection != null ) {

            HashMap<String, int[]> killDeathCounts = api.getKillDeathCounts();
            killDeathCounts.clear(); // 一旦、全てクリア

            for ( String group : killDeathCounts.keySet() ) {
                List<Integer> data = tksection.getIntegerList(group);
                if ( data != null && data.size() >= 3 ) {
                    int[] value = {data.get(0), data.get(1), data.get(2)};
                    killDeathCounts.put(group, value);
                }
            }
        }

        // プレイヤーキルデス数の復帰
        ConfigurationSection uksection =
                config.getConfigurationSection("killDeathUserCounts");
        if ( uksection != null ) {

            HashMap<String, int[]> killDeathUserCounts = api.getKillDeathUserCounts();
            killDeathUserCounts.clear(); // 一旦、全てクリア

            for ( String user : killDeathUserCounts.keySet() ) {
                List<Integer> data = uksection.getIntegerList(user);
                if ( data != null && data.size() >= 3 ) {
                    int[] value = {data.get(0), data.get(1), data.get(2)};
                    killDeathUserCounts.put(user, value);
                }
            }
        }

        return true;
    }

    /**
     * 指定した名前のセーブデータが既に存在するかどうかを返すメソッド
     * @param name セーブデータ名
     * @return 存在するかどうか
     */
    public boolean isExist(String name) {

        File file = new File(saveDir, name + ".yml");
        return file.exists() && file.isFile();
    }

    /**
     * 全てのユーザーの色を解除して、全グループを解散させる
     */
    private void clearAllUsers() {

        ColorTeamingAPI api = ColorTeaming.instance.getAPI();
        HashMap<String, ArrayList<Player>> members = api.getAllTeamMembers();

        for ( String group : members.keySet() ) {
            for ( Player p : members.get(group) ) {
                api.leavePlayerTeam(p, Reason.TEAM_REMOVED);
            }
        }
    }

    private List<Integer> convertToList(int[] arr) {

        List<Integer> result = new ArrayList<Integer>();
        for ( int i : arr ) {
            result.add(new Integer(i));
        }
        return result;
    }
}
