package server.mecs.serverspawn

import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin


class ServerSpawn : JavaPlugin(), Listener {
    private var config: FileConfiguration? = null
    var s = false
    var x = 0
    var y = 0
    var z = 0
    var w: String? = null
    var l: Location? = null

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        reloadConfig()
        config = getConfig()
        x = config.getInt("x", 0)
        y = config.getInt("y", 0)
        z = config.getInt("z", 0)
        w = config.getString("w", null)
        s = config.getBoolean("s", false)
        saveConfig()
        reloadConfig()
        println("座標の情報が読み取れなかった場合に設定されます\non/offの情報が読み取れなかった場合falseに設定されます\nワールド名が読み取れなかった場合nullに設定されます")
        getCommand("sspawn").executor = this
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String?, args: Array<String>): Boolean {
        if (command.name == "sspawn") {
            if (!sender.isOp) {
                sender.sendMessage("§c§lあなたにはこのこまんどを実行する権限がありません")
                return true
            }
            if (sender is Player) {
                if (args[0] == "set") {
                    val p = sender
                    config!!["x"] = p.location.blockX
                    config!!["y"] = p.location.blockY
                    config!!["z"] = p.location.blockZ
                    config!!["w"] = p.location.world
                    saveConfig()
                    reloadConfig()
                    sender.sendMessage("座標を保存しました")
                    return true
                }
            }
            if (args.size != 1) {
                sender.sendMessage("/sspawn set : 実行しているプレイヤーが立っている場所をスポーン地点に設定します")
                sender.sendMessage("/sspawn <x> <y> <z> : プレイヤーがログイン時にスポーンする座標を設定します")
                sender.sendMessage("/sspawn reload : 設定値をリロードします")
                return true
            }
            if (args[0] == "on") {
                config!!["s"] = true
                saveConfig()
                reloadConfig()
                sender.sendMessage("serverspawnを有効化しました")
                return true
            }
            if (args[0] == "off") {
                config!!["s"] = false
                saveConfig()
                reloadConfig()
                sender.sendMessage("serverspawnを無効化しました")
                return true
            }
            if (args[0] == "reload") {
                reloadConfig()
                return true
            }
        }
        return true
    }

    @EventHandler
    fun spawn(e: PlayerJoinEvent) {
        if (!s) {
            return
        }
        l = Location(server.getWorld(w), x.toDouble(), y.toDouble(), z.toDouble())
        e.player.teleport(l)
    }
}
