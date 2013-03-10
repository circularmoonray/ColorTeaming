/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.ucchyocean.cmt.ColorMeTeaming;
import com.github.ucchyocean.cmt.ColorMeTeamingConfig;

/**
 * @author ucchy
 * プレイヤーがリスポーンしたときに、通知を受け取って処理するクラス
 */
public class PlayerRespawnListener implements Listener {

    /**
     * Playerがログアウトしたときに発生するイベント
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();
        String color = ColorMeTeaming.getPlayerColor(player);

        // リスポーンポイントを設定
        if ( !ColorMeTeamingConfig.ignoreGroups.contains(color) ) {
            Location respawn = ColorMeTeaming.respawnConfig.get(color);
            if ( respawn != null ) {
                respawn = respawn.add(0.5, 0, 0.5);
                event.setRespawnLocation(respawn);
            }
        }
    }
}
