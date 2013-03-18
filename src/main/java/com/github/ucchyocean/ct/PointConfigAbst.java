/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author ucchy
 * Locationのファイル保存、ファイルから取得を行うための抽象クラス
 */
public abstract class PointConfigAbst {

    private static final String KEY_WORLD = "world";
    private static final String KEY_LOCX = "x";
    private static final String KEY_LOCY = "y";
    private static final String KEY_LOCZ = "z";

    private File file;
    private YamlConfiguration config;

    public abstract String getConfigFileName();

    /**
     * コンストラクタ
     */
    public PointConfigAbst() {

        file = new File(
                ColorTeaming.instance.getDataFolder() +
                File.separator + getConfigFileName());

        if ( !file.exists() ) {
            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * ポイントを取得する。未設定の場合はnullが返ることに注意すること。
     * @param name ポイント名
     * @return
     */
    public Location get(String name) {

        // 取得する前にリロードする
        config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection section = config.getConfigurationSection(name);
        if ( section == null ) {
            return null;
        }
        String w = section.getString(KEY_WORLD, "world");
        World world = ColorTeaming.getWorld(w);
        if ( world == null ) {
            return null;
        }
        int x = section.getInt(KEY_LOCX, 0);
        int y = section.getInt(KEY_LOCY, 65);
        int z = section.getInt(KEY_LOCZ, 0);
        return new Location(world, x, y, z);
    }

    /**
     * グループのリスポーンポイントを設定してコンフィグの保存を行う。
     * @param name グループ名
     * @param location グループのリスポーンポイント
     */
    public void set(String name, Location location) {

        if ( location != null ) {
            String world = location.getWorld().getName();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            config.set(name + "." + KEY_WORLD, world);
            config.set(name + "." + KEY_LOCX, x);
            config.set(name + "." + KEY_LOCY, y);
            config.set(name + "." + KEY_LOCZ, z);

        } else {

            config.set(name, null);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 現在の設定ファイル内のキー一覧を取得する
     * @return
     */
    public ArrayList<String> keys() {

        ArrayList<String> results = new ArrayList<String>();

        config = YamlConfiguration.loadConfiguration(file);

        Iterator<String> i = config.getValues(false).keySet().iterator();
        while (i.hasNext()) {
            results.add(i.next());
        }

        return results;
    }

    /**
     * ファイルから全ポイントのリストを取得する
     * @return リスト
     */
    public ArrayList<String> list() {

        ArrayList<String> results = new ArrayList<String>();

        // 取得する前にリロードする
        config = YamlConfiguration.loadConfiguration(file);

        Iterator<String> i = config.getValues(false).keySet().iterator();
        while (i.hasNext()) {
            String name = i.next();

            ConfigurationSection section = config.getConfigurationSection(name);
            String w = section.getString(KEY_WORLD, "world");
            int x = section.getInt(KEY_LOCX, 0);
            int y = section.getInt(KEY_LOCY, 65);
            int z = section.getInt(KEY_LOCZ, 0);

            results.add(name + " : " + w + "-(" + x + ", " + y + ", " + z + ")");
        }

        return results;
    }
}
