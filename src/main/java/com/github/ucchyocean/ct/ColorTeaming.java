/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.ucchyocean.ct.bridge.VaultChatBridge;
import com.github.ucchyocean.ct.command.CChatCommand;
import com.github.ucchyocean.ct.command.CChatGlobalCommand;
import com.github.ucchyocean.ct.command.CClassCommand;
import com.github.ucchyocean.ct.command.CCountCommand;
import com.github.ucchyocean.ct.command.CExplodeCommand;
import com.github.ucchyocean.ct.command.CFriendlyFireCommand;
import com.github.ucchyocean.ct.command.CGiveCommand;
import com.github.ucchyocean.ct.command.CJoinCommand;
import com.github.ucchyocean.ct.command.CLeaderCommand;
import com.github.ucchyocean.ct.command.CLeaveCommand;
import com.github.ucchyocean.ct.command.CPointCommand;
import com.github.ucchyocean.ct.command.CRandomCommand;
import com.github.ucchyocean.ct.command.CRemoveCommand;
import com.github.ucchyocean.ct.command.CSpawnCommand;
import com.github.ucchyocean.ct.command.CTPCommand;
import com.github.ucchyocean.ct.command.CTeamingCommand;
import com.github.ucchyocean.ct.config.ColorTeamingConfig;
import com.github.ucchyocean.ct.listener.PlayerChatListener;
import com.github.ucchyocean.ct.listener.PlayerDeathListener;
import com.github.ucchyocean.ct.listener.PlayerJoinQuitListener;
import com.github.ucchyocean.ct.listener.PlayerRespawnListener;

/**
 * 簡易PVPチーミングプラグイン
 * @author ucchy
 */
public class ColorTeaming extends JavaPlugin {

    public static ColorTeaming instance;
    protected ColorTeamingConfig config;
    private ColorTeamingManager manager;

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        instance = this;

        // 設定の読み込み処理
        config = ColorTeamingConfig.loadConfig();
        
        // VaultChatをロード
        VaultChatBridge vaultchat = null;
        if ( getServer().getPluginManager().isPluginEnabled("Vault") ) {
            vaultchat = VaultChatBridge.load(
                    getServer().getPluginManager().getPlugin("Vault"));
        }

        // クラスフォルダが存在しない場合は、jarファイルの中からデフォルトをコピーする
        File classDir = new File(getDataFolder(), "classes");
        if ( !classDir.exists() || !classDir.isDirectory() ) {
            classDir.mkdirs();
            Utility.copyFolderFromJar(getFile(), classDir, "classes");
        }
        
        // マネージャの初期化
        manager = new ColorTeamingManager(this, config, vaultchat);

        // コマンドをサーバーに登録
        getCommand("colorcount").setExecutor(new CCountCommand(this));
        getCommand("colorfriendlyfire").setExecutor(new CFriendlyFireCommand(this));
        getCommand("colorchat").setExecutor(new CChatCommand(this));
        getCommand("colorglobal").setExecutor(new CChatGlobalCommand());
        getCommand("colorleader").setExecutor(new CLeaderCommand(this));
        getCommand("colortp").setExecutor(new CTPCommand(this));
        getCommand("colorclass").setExecutor(new CClassCommand(this));
        getCommand("colorpoint").setExecutor(new CPointCommand(this));
        getCommand("colorspawn").setExecutor(new CSpawnCommand(this));
        getCommand("colorrandom").setExecutor(new CRandomCommand(this));
        getCommand("colorremove").setExecutor(new CRemoveCommand(this));
        getCommand("colorexplode").setExecutor(new CExplodeCommand(this));
        getCommand("colorgive").setExecutor(new CGiveCommand(this));
        getCommand("colorjoin").setExecutor(new CJoinCommand(this));
        getCommand("colorleave").setExecutor(new CLeaveCommand(this));
        getCommand("colorteaming").setExecutor(new CTeamingCommand(this));

        // イベント購読をサーバーに登録
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
    }

    /**
     * このプラグインのJarファイル自身を示すFileクラスを返す。
     * @return
     */
    public File getPluginJarFile() {
        return this.getFile();
    }

    /**
     * ColorTeamingConfig を取得する
     * @return ColorTeamingConfig
     */
    public ColorTeamingConfig getCTConfig() {
        return config;
    }

    /**
     * ColorTeamingAPI を取得する
     * @return ColorTeamingAPI
     */
    public ColorTeamingAPI getAPI() {
        return manager;
    }
}
