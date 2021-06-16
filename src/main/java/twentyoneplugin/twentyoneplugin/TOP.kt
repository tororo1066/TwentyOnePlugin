package twentyoneplugin.twentyoneplugin

import net.kyori.adventure.text.Component
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
import twentyoneplugin.twentyoneplugin.Util.sendmsg
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PlayerData{
    lateinit var enemy : UUID
    var tip : Double = 0.0
    var tipcoin = plugin.config.getInt("tipcoin")
    var bet = 0
    lateinit var inv : Inventory
    var through : Boolean = false
    var bjnumber = 21
    var action = ""


    fun dataset(player : Player, enemyplayer : Player,tipdouble : Double){
        enemy = enemyplayer.uniqueId
        tip = tipdouble
        inv = invsetup(player.uniqueId,enemyplayer.uniqueId,plugin.config.getInt("tipcoin"))
    }
}

class TOP : JavaPlugin() {

    private val savetips = HashMap<UUID,Double>()


    override fun onEnable() {
        saveDefaultConfig()
        getCommand("21")?.setExecutor(this)
        server.pluginManager.registerEvents(EventListener,this)
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
        const val prefix = "§4§l[21]§r"
        val datamap = HashMap<UUID,PlayerData>()
        val cansp = HashMap<UUID,Boolean>()
        val spcards = HashMap<Int,Int>()
        val cardcsm = ArrayList<Int>()
        val canjoin = ArrayList<UUID>()
        lateinit var plugin : TOP
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)return true
        if (args.isEmpty()){
            return true
        }
        when(args[0]){
            "start"->{
                if (args.size != 2){
                    sender.sendmsg("§4引数が不正です")
                    return true
                }
                if (args[1].toDoubleOrNull() == null){
                    sender.sendmsg("/21 start [掛け金]")
                    return true
                }
                if (datamap.containsKey(sender.uniqueId)){
                    sender.sendmsg("§4ゲームに参加中です")
                    return true
                }
                datamap[sender.uniqueId] = PlayerData()
                savetips[sender.uniqueId] = args[1].toDouble()
                canjoin.add(sender.uniqueId)
                Bukkit.broadcast(Component.text("§l${sender.name}§aが§5§l21§aを募集中...残り60秒\n" +
                        "§f/21 join ${sender.name} §4最低必須金額 ${Util.getdata(sender.uniqueId).tip * plugin.config.getInt("tipcoin")}"), Server.BROADCAST_CHANNEL_USERS)
                TwentyOne(sender.uniqueId).start()
            }

            "join"->{
                if (args.size != 2){
                    sender.sendmsg("§4引数が不正です")
                    return true
                }
                if (datamap.containsKey(sender.uniqueId)){
                    sender.sendmsg("§4ゲームに参加中です")
                    return true
                }
                val p = Bukkit.getPlayer(args[1])
                if (p == null || !p.isOnline){
                    sender.sendmsg("§4プレイヤーが存在しない、またはオフラインです")
                    return true
                }
                if (!canjoin.contains(p.uniqueId)){
                    sender.sendmsg("§4ゲームが存在しません")
                    return true
                }
                canjoin.remove(p.uniqueId)
                datamap[p.uniqueId]?.dataset(p,sender,savetips[p.uniqueId]!!)
                datamap[sender.uniqueId] = PlayerData()
                datamap[sender.uniqueId]?.dataset(sender,p,savetips[p.uniqueId]!!)
                return true
            }

            "open"->{
                if (args.size != 1){
                    sender.sendmsg("§4引数が不正です")
                    return true
                }
                if (!datamap.containsKey(sender.uniqueId)){
                    sender.sendmsg("§4ゲームに参加していません")
                    return true
                }
                sender.openInventory(getinv(sender.uniqueId))
                return true
            }

            "remove"->{
                datamap.clear()
                canjoin.clear()
                return true
            }

        }
        return true
    }
}