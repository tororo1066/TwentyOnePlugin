package twentyoneplugin.twentyoneplugin

import twentyoneplugin.twentyoneplugin.*
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import twentyoneplugin.twentyoneplugin.Inventory.invsetup
import twentyoneplugin.twentyoneplugin.Util.gamestart
import twentyoneplugin.twentyoneplugin.Util.sendmsg
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap





class PlayerData{
    lateinit var enemy : UUID
    var tip : Double = 0.0
    var bet = 0
    lateinit var inv : Inventory
    var through : Boolean = false
    var bjnumber = 21
    var gamecount = 0


    fun dataset(player : Player, enemyplayer : Player,tipdouble : Double){
        enemy = enemyplayer.uniqueId
        tip = tipdouble
        inv = invsetup(player.uniqueId,enemyplayer.uniqueId)
    }
}

class TOP : JavaPlugin() {

    private val savetips = HashMap<UUID,Double>()
    private val canjoin = ArrayList<UUID>()

    override fun onEnable() {
        saveDefaultConfig()
        getCommand("21")?.setExecutor(this)
        server.pluginManager.registerEvents(EventListener,this)
        plugin = this
        var int = 1
        while (config.isSet("sp.$int")){
            if (config.getBoolean("sp.$int.enable")) spcards.add(int)
            int++
        }
        for (l in config.getIntegerList("cardcsm")){
            cardcsm.add(l)
        }
    }

    companion object{
        const val prefix = "§f[§0§l21§f]§r"
        val datamap = HashMap<UUID,PlayerData>()
        val spcards = ArrayList<Int>()
        val cardcsm = ArrayList<Int>()
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
                gamestart(p.uniqueId,sender.uniqueId)
                return true
            }

        }
        return true
    }
}