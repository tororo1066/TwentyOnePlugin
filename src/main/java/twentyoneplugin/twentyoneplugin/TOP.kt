package twentyoneplugin.twentyoneplugin

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.event.HoverEventSource
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import twentyoneplugin.twentyoneplugin.Inventory.getinv
import twentyoneplugin.twentyoneplugin.Inventory.invsetup
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.Util.getdata
import twentyoneplugin.twentyoneplugin.Util.hokancmd
import twentyoneplugin.twentyoneplugin.Util.runcmd
import twentyoneplugin.twentyoneplugin.Util.sendmsg
import java.util.*
import kotlin.properties.Delegates


class PlayerData{
    lateinit var enemy : UUID
    var tip : Double = 0.0
    var tipcoin = plugin.config.getInt("tipcoin")
    var bet = 0
    lateinit var inv : Inventory
    var through : Boolean = false
    var bjnumber = 21
    var action = ""
    var spuse = true
    var harvest = false

    fun tipset(tipdouble : Double){
        tip = tipdouble
    }

    fun dataset(player : Player, enemyplayer : Player){
        enemy = enemyplayer.uniqueId
        inv = invsetup(player.uniqueId,enemyplayer.uniqueId)
    }
}

class TOP : JavaPlugin() {

    var mode by Delegates.notNull<Boolean>()


    override fun onEnable() {
        saveDefaultConfig()
        getCommand("21")?.setExecutor(this)
        server.pluginManager.registerEvents(EventListener,this)
        vault = VaultManager(this)
        if (config.getBoolean("switch.savemode")){
            mode = config.getBoolean("switch.mode")
        }else{
            mode = false
            config.set("switch.mode",false)
            saveConfig()
        }
        plugin = this
        var int = 1
        while (config.isSet("sp.$int")){
            if (config.getBoolean("sp.$int.enable")){
                spcards[int] = config.getInt("sp.$int.csm")
            }
            int++
        }
        for (l in config.getIntegerList("cardcsm")){
            cardcsm.add(l)
        }
    }

    companion object{
        lateinit var vault : VaultManager
        const val prefix = "§5§l[21]§r"
        val datamap = HashMap<UUID,PlayerData>()
        val spcards = HashMap<Int,Int>()
        val cardcsm = ArrayList<Int>()
        val canjoin = ArrayList<UUID>()
        lateinit var plugin : TOP
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        if (!mode && !sender.isOp && !sender.hasPermission("21.switch")) {
            sender.sendmsg("§421は現在停止中です")
            return true
        }
        if (args.isEmpty()) {
            sender.sendmsg("§b/21 helpでコマンドを確認しよう！")
            return true
        }
        when (args[0]) {
            "start" -> {
                if (args.size != 2) {
                    sender.sendmsg("§4引数が不正です")
                    return true
                }
                if (args[1].toDoubleOrNull() == null) {
                    sender.sendmsg("/21 start [掛け金]")
                    return true
                }
                if (datamap.containsKey(sender.uniqueId)) {
                    sender.sendmsg("§4ゲームに参加中です")
                    return true
                }
                datamap[sender.uniqueId] = PlayerData()
                canjoin.add(sender.uniqueId)
                datamap[sender.uniqueId]?.tipset(args[1].toDouble())

                Bukkit.broadcast(
                    runcmd("§l${sender.name}§aが§5§l21§aを募集中...残り60秒\n" +
                            "§f/21 join ${sender.name} §4最低必須金額 ${
                                Util.getdata(sender.uniqueId).tip * plugin.config.getInt(
                                    "tipcoin"
                                )
                            }","/21 join ${sender.name}", "§6またはここをクリック！"), Server.BROADCAST_CHANNEL_USERS
                )
                TwentyOne(sender.uniqueId).start()
            }

            "join" -> {
                if (args.size != 2) {
                    sender.sendmsg("§4引数が不正です")
                    return true
                }
                if (datamap.containsKey(sender.uniqueId)) {
                    sender.sendmsg("§4ゲームに参加中です")
                    return true
                }
                val p = Bukkit.getPlayer(args[1])
                if (p == null || !p.isOnline) {
                    sender.sendmsg("§4プレイヤーが存在しない、またはオフラインです")
                    return true
                }
                if (!canjoin.contains(p.uniqueId)) {
                    sender.sendmsg("§4ゲームが存在しません")
                    return true
                }
                if (getdata(p.uniqueId).tip > vault.getBalance(sender.uniqueId)){
                    sender.sendmsg("§4所持金が不足しています")
                    return true
                }
                canjoin.remove(p.uniqueId)

                datamap[sender.uniqueId] = PlayerData()
                datamap[p.uniqueId]?.dataset(p, sender)
                datamap[sender.uniqueId]?.dataset(sender, p)
                datamap[sender.uniqueId]?.tipset(getdata(p.uniqueId).tip)
                return true
            }

            "open" -> {
                if (args.size != 1) {
                    sender.sendmsg("§4引数が不正です")
                    return true
                }
                if (!datamap.containsKey(sender.uniqueId)) {
                    sender.sendmsg("§4ゲームに参加していません")
                    return true
                }
                sender.openInventory(getinv(sender.uniqueId))
                return true
            }

            "help" -> {
                sender.sendMessage("§5==========TwentyOnePlugin(21)==========")
                sender.sendMessage(hokancmd("§5/21 start [賭け数] 指定した賭け数で21を募集します","/21 start ","§6またはここをクリック！"))
                sender.sendMessage(hokancmd("§5/21 join [name] 指定したプレイヤーの部屋に入ります","/21 join ", "§6またはここをクリック！"))
                sender.sendMessage(runcmd("§5/21 open ゲーム中だった場合そこのインベントリを開きます","/21 open","§6またはここをクリック！"))
                sender.sendMessage("§5/21 rule 21のルールを確認できます")
                if (sender.isOp || sender.hasPermission("21.switch")) {
                    sender.sendMessage(runcmd("§c/21 switch モードを切り替えます","/21 switch","§6またはここをクリック！"))
                    if (sender.isOp) {
                        sender.sendMessage(runcmd("§c/21 switchsavemode モード変更を鯖再起後も保持するかどうかを切り替えます","/21 switchsavemode","§6またはここをクリック！"))
                    }
                    sender.sendMessage("§c赤はOP(21.switch)用のコマンドです")
                }
                sender.sendMessage("§5==========TwentyOnePlugin(21)==========")
                return true

            }
            "rule" -> {

            }

            "switch" -> {
                if (!sender.isOp && !sender.hasPermission("21.switch")) {
                    sender.sendmsg("§4あなたはこのコマンドを実行する権限がありません")
                    return true
                }
                config.set("switch.mode", !config.getBoolean("switch.mode"))
                saveConfig()
                sender.sendmsg("§bmodeを§a${config.getBoolean("switch.mode")}§bに変更しました")
                return true
            }

            "switchsavemode" -> {
                if (!sender.isOp) {
                    sender.sendmsg("§4あなたはこのコマンドを実行する権限がありません")
                    return true
                }
                config.set("switch.savemode", !config.getBoolean("switch.savemode"))
                saveConfig()
                sender.sendmsg("§bsavemodeを§a${config.getBoolean("switch.savemode")}§bに変更しました")
                return true
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {

        if (alias == "21"){
            if (args.size == 1){
                return mutableListOf("help","join","start","open")
            }
            if (args.size == 2){
                when(args[0]){
                    "start"-> return mutableListOf("0","100","10000")
                }
            }
        }

        val list = mutableListOf<String>()

        for (p in Bukkit.getOnlinePlayers()){
            if (p.name == sender.name)continue
            list.add(p.name)
        }

        return list
    }
}