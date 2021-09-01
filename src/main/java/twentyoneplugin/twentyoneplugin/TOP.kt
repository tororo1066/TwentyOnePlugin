package twentyoneplugin.twentyoneplugin

import advancement.AdvancementManager
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import twentyoneplugin.twentyoneplugin.AdvancementUtils.Companion.awardAdvancement
import twentyoneplugin.twentyoneplugin.Inventory.getinv
import twentyoneplugin.twentyoneplugin.Inventory.invsetup
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.Util.format
import twentyoneplugin.twentyoneplugin.Util.getdata
import twentyoneplugin.twentyoneplugin.Util.hokancmd
import twentyoneplugin.twentyoneplugin.Util.isDone
import twentyoneplugin.twentyoneplugin.Util.runcmd
import twentyoneplugin.twentyoneplugin.Util.sendmsg
import twentyoneplugin.twentyoneplugin.advancements.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.floor


class PlayerData{
    lateinit var enemy : UUID
    var name = ""
    var tip : Double = 0.0
    var tipcoin = plugin.config.getInt("tipcoin")
    var bet = 0
    lateinit var inv : Inventory
    var through : Boolean = false
    var bjnumber = 21
    var action = ""
    var spuse = true
    var harvest = false
    var death = false

    var round = plugin.config.getInt("round")
    var firstbet = plugin.config.getInt("firstbet")
    var clocktime = plugin.config.getInt("clocktime")
    var settipcoin = plugin.config.getInt("tipcoin")

    var customsetting = false

    fun tipset(tipdouble : Double){
        tip = tipdouble
    }

    fun nameset(player : Player, enemyplayer : Player){
        enemy = enemyplayer.uniqueId
        name = player.name
    }

    fun dataset(player : Player, enemyplayer : Player){
        inv = invsetup(player.uniqueId,enemyplayer.uniqueId)
    }

    fun gamedataset(r : Int, fb : Int, ct : Int, tc : Int){
        round = r
        firstbet = fb
        clocktime = ct
        tipcoin = tc
        settipcoin = tc
        customsetting = true
    }
}


class TOP : JavaPlugin() {

    var mode = false


    override fun onEnable() {
        saveDefaultConfig()
        getCommand("21")?.setExecutor(this)
        plugin = this
        server.pluginManager.registerEvents(EventListener,this)
        server.pluginManager.registerEvents(AdvancementUtils(),this)
        vault = VaultManager(this)
        val mysql = MySQLManager(this,"21firstload")
        if (!mysql.connected){
            logger.warning("データベースへの接続に失敗しました")
            logger.warning("それによりmodeをoffにしました(鯖内で変えることが可能です)")
            mode = false
        }else{
            mysql.execute("CREATE TABLE IF NOT EXISTS `twentyone_log` (\n" +
                    "\t`startplayer` VARCHAR(16) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                    "\t`joinplayer` VARCHAR(16) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                    "\t`tip` DOUBLE NULL DEFAULT NULL,\n" +
                    "\t`firstcoin` INT(10) NULL DEFAULT NULL,\n" +
                    "\t`startlastcoin` INT(10) NULL DEFAULT NULL,\n" +
                    "\t`joinlastcoin` INT(10) NULL DEFAULT NULL\n" +
                    ")\n" +
                    "COLLATE='utf8mb4_0900_ai_ci'\n" +
                    "ENGINE=InnoDB\n" +
                    ";\n")

            mysql.execute("CREATE TABLE IF NOT EXISTS `twentyone_battle_log` (\n" +
                    "\t`uuid` VARCHAR(36) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                    "\t`mcid` VARCHAR(16) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                    "\t`win` INT(10) NULL DEFAULT NULL,\n" +
                    "\t`draw` INT(10) NULL DEFAULT NULL,\n" +
                    "\t`lose` INT(10) NULL DEFAULT NULL\n" +
                    ")\n" +
                    "COLLATE='utf8mb4_0900_ai_ci'\n" +
                    "ENGINE=InnoDB\n" +
                    ";\n")

            if (config.getBoolean("switch.savemode")){
                mode = config.getBoolean("switch.mode")
            }else{
                mode = false
                config.set("switch.mode",false)
                saveConfig()
            }
        }

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

        cardmaterial = config.getString("cardmaterial")?.let { Material.valueOf(it) }!!
        spcardmaterial = Material.valueOf(config.getString("spcardmaterial")!!)
        invisiblecardcsm = config.getInt("invisiblecardcsm")

        AdvancementUtils().loadAdvancements()
    }

    companion object{
        lateinit var vault : VaultManager
        const val prefix = "§5§l[21]§r"
        val datamap = ConcurrentHashMap<UUID,PlayerData>()
        val spcards = ConcurrentHashMap<Int,Int>()
        val cardcsm = ArrayList<Int>()
        val canjoin = ArrayList<UUID>()
        var cardmaterial = Material.PAPER
        var spcardmaterial = Material.PAPER
        var invisiblecardcsm = 0
        lateinit var plugin : TOP
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender is Player && !mode && !sender.isOp && !sender.hasPermission("21.switch")) {
            sender.sendmsg("§421は現在停止中です")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage(prefix + "§b/21 helpでコマンドを確認しよう！")
            return true
        }
        when (args[0]) {
            "start" -> {
                if (sender !is Player) return true
                if (args.size == 6) {

                    if (!args[1].matches(Regex("-?\\d+"))){
                        sender.sendmsg("掛け金が不正です")
                        return true
                    }

                    val money = args[1].toDoubleOrNull()?:return true
                    val round = args[2].toIntOrNull()?:return true
                    val tip = args[3].toIntOrNull()?:return true
                    val bet = args[4].toIntOrNull()?:return true
                    val clocktime = args[5].toIntOrNull()?:return true

                    if (money % 1 > 0|| money < 0 || round !in 1..10 || tip !in 10..30 || bet !in 1..10 || clocktime !in 10..60) {
                        sender.sendmsg("/21 start [1枚当たりの掛け金] [Round数(1~10)] [初期チップ数(10~30)] [初期ベット数(1~10)] [1ターンの時間(10~60)]")
                        return true
                    }
                    if (datamap.containsKey(sender.uniqueId)) {
                        sender.sendmsg("§4ゲームに参加中です")
                        return true
                    }

                    if (money * tip > vault.getBalance(sender.uniqueId)){
                        sender.sendmsg("§4金額が足りません\n必要金額：${money * tip}")
                        return true
                    }
                    vault.withdraw(sender.uniqueId, money * tip)
                    datamap[sender.uniqueId] = PlayerData()
                    canjoin.add(sender.uniqueId)
                    datamap[sender.uniqueId]?.tipset(money)
                    datamap[sender.uniqueId]?.gamedataset(round,bet,clocktime,tip)

                    Bukkit.broadcast(
                        runcmd("§l${sender.name}§aが§5§l21§aを募集中...残り60秒\n" +
                                "§f/21 join ${sender.name} §e最低必須金額 ${
                                    format(getdata(sender.uniqueId).tip * tip)
                                }\n" +
                                "§b部屋設定 1チップ当たりの金額:${format(money)}円、Round数:${datamap[sender.uniqueId]?.round}、初期チップ数:${datamap[sender.uniqueId]?.settipcoin}枚、初期ベット数:${datamap[sender.uniqueId]?.firstbet}枚、1ターンの時間:${datamap[sender.uniqueId]?.clocktime}秒","/21 join ${sender.name}", "§6またはここをクリック！"), Server.BROADCAST_CHANNEL_USERS
                    )
                    TwentyOne(sender.uniqueId).start()
                    return true
                }
                if (args.size == 2){

                    val money = args[1].toDoubleOrNull()?:return true

                    if (datamap.containsKey(sender.uniqueId)) {
                        sender.sendmsg("§4ゲームに参加中です")
                        return true
                    }
                    if (money % 1 > 0|| money < 0){
                        sender.sendmsg("§4金額の指定が不正規です")
                        return true
                    }
                    if (money * plugin.config.getInt("tipcoin") > vault.getBalance(sender.uniqueId)){
                        sender.sendmsg("§4金額が足りません\n必要金額：${money * plugin.config.getInt("tipcoin")}")
                        return true
                    }
                    vault.withdraw(sender.uniqueId, money * plugin.config.getInt("tipcoin"))
                    datamap[sender.uniqueId] = PlayerData()
                    canjoin.add(sender.uniqueId)
                    datamap[sender.uniqueId]?.tipset(money)

                    Bukkit.broadcast(
                        runcmd("§l${sender.name}§aが§5§l21§aを募集中...残り60秒\n" +
                                "§f/21 join ${sender.name} §e最低必須金額 ${
                                    format(getdata(sender.uniqueId).tip * plugin.config.getInt(
                                        "tipcoin"
                                    ))
                                }\n" +
                                "§b部屋設定 1チップ当たりの金額:${format(getdata(sender.uniqueId).tip)}円、Round数:${datamap[sender.uniqueId]?.round}、初期チップ数:${datamap[sender.uniqueId]?.settipcoin}枚、初期ベット数:${datamap[sender.uniqueId]?.firstbet}枚、1ターンの時間:${datamap[sender.uniqueId]?.clocktime}秒","/21 join ${sender.name}", "§6またはここをクリック！"), Server.BROADCAST_CHANNEL_USERS
                    )
                    TwentyOne(sender.uniqueId).start()
                }



            }

            "join" -> {
                if (sender !is Player) return true
                if (args.size != 2) {
                    sender.sendmsg("/21 join [プレイヤーの名前]")
                    return true
                }
                if (datamap.containsKey(sender.uniqueId)) {
                    sender.sendmsg("§4ゲームに参加中です")
                    return true
                }
                val p = Bukkit.getPlayer(args[1])
                if (p == null) {
                    sender.sendmsg("§4プレイヤーが存在しない、またはオフラインです")
                    return true
                }
                if (!canjoin.contains(p.uniqueId)) {
                    sender.sendmsg("§4ゲームが存在しません")
                    return true
                }
                if (getdata(p.uniqueId).tip * getdata(p.uniqueId).tipcoin > vault.getBalance(sender.uniqueId)){
                    sender.sendmsg("§4所持金が不足しています")
                    return true
                }
                canjoin.remove(p.uniqueId)

                vault.withdraw(sender.uniqueId, getdata(p.uniqueId).tip * getdata(p.uniqueId).tipcoin)
                datamap[sender.uniqueId] = PlayerData()
                datamap[sender.uniqueId]?.tipset(getdata(p.uniqueId).tip)
                datamap[sender.uniqueId]?.gamedataset(getdata(p.uniqueId).round, getdata(p.uniqueId).firstbet,getdata(p.uniqueId).clocktime,getdata(p.uniqueId).tipcoin)
                datamap[sender.uniqueId]?.nameset(sender,p)
                datamap[p.uniqueId]?.nameset(p,sender)
                datamap[p.uniqueId]?.dataset(p, sender)
                datamap[sender.uniqueId]?.dataset(sender, p)

                if (LoginServer.key.isDone(p)) p.awardAdvancement(JoinGame.key)
                if (LoginServer.key.isDone(sender)) sender.awardAdvancement(JoinGame.key)
                return true
            }

            "open" -> {
                if (sender !is Player) return true
                if (args.size != 1) {
                    sender.sendmsg("§4引数が不正です")
                    return true
                }
                if (!datamap.containsKey(sender.uniqueId)) {
                    sender.sendmsg("§4ゲームに参加していません")
                    return true
                }
                try {
                    sender.openInventory(getinv(sender.uniqueId))
                }catch (e : Exception){
                    sender.sendmsg("§4少し時間をおいて試してください")
                }
                return true
            }

            "log"->{
                if (args.size == 2 && sender is ConsoleCommandSender){
                    val p = Bukkit.getOfflinePlayer(args[1])
                    Thread {
                        val mysql = MySQLManager(this, "21GetLog")
                        val rs = mysql.query("SELECT * FROM twentyone_battle_log WHERE uuid = '${p.uniqueId}';")
                        if (rs == null || !rs.next()) {
                            sender.sendMessage("§4${p.name}はまだ一度も試合をしていません")
                            rs?.close()
                            mysql.close()
                            return@Thread
                        }
                        val win = rs.getInt("win")
                        val draw = rs.getInt("draw")
                        val lose = rs.getInt("lose")
                        val winper =
                            floor((win.toDouble() / (win.toDouble() + draw.toDouble() + lose.toDouble())) * 100)

                        sender.sendMessage(
                            """
                    §e§l${p.name}の戦績
                    §6勝利数：$win
                    §a引分数：$draw
                    §c敗北数：$lose
                    §b勝率(切捨て)：${winper}%
                """.trimIndent()
                        )
                        rs.close()
                        mysql.close()
                        return@Thread
                    }.start()
                }else {
                    if (sender !is Player) return true
                    Thread {
                        val mysql = MySQLManager(this, "21GetLog")
                        val rs = mysql.query("SELECT * FROM twentyone_battle_log WHERE uuid = '${sender.uniqueId}';")
                        if (rs == null || !rs.next()) {
                            sender.sendmsg("§4あなたはまだ一度も試合をしていません")
                            rs?.close()
                            mysql.close()
                            return@Thread
                        }
                        val win = rs.getInt("win")
                        val draw = rs.getInt("draw")
                        val lose = rs.getInt("lose")
                        val winper =
                            floor((win.toDouble() / (win.toDouble() + draw.toDouble() + lose.toDouble())) * 100)
                        Bukkit.getScheduler().runTask(this, Runnable {
                            if (win + draw + lose >= 10) {
                                if (JoinGame.key.isDone(sender)) sender.awardAdvancement(Addict.key)
                            }
                            if (win + draw + lose >= 100) {
                                if (Addict.key.isDone(sender)) sender.awardAdvancement(HundredBattles.key)
                                if (winper >= 70.0) {
                                    if (HundredBattles.key.isDone(sender)) sender.awardAdvancement(ProGamer.key)
                                }
                            }
                        })

                        sender.sendMessage(
                            """
                    §e§lあなたの戦績
                    §6勝利数：$win
                    §a引分数：$draw
                    §c敗北数：$lose
                    §b勝率(切捨て)：${winper}%
                """.trimIndent()
                        )
                        rs.close()
                        mysql.close()
                        return@Thread
                    }.start()
                }
                return true
            }

            "help" -> {
                sender.sendMessage("§d===================TwentyOnePlugin(21)===================")
                sender.sendMessage(hokancmd("§b/21 start [賭け数] 指定した賭け数で21を募集します","/21 start ","§6またはここをクリック！"))
                sender.sendMessage(hokancmd("§b/21 start [1枚当たりの掛け金] [Round数(1~10)] [初期チップ数(10~30)] [初期ベット数(1~10)] [1ターンの時間(10~60)]","/21 start ","§6またはここをクリック！"))
                sender.sendMessage("§b細かいルールを指定して部屋を作成します")
                sender.sendMessage(hokancmd("§b/21 join [name] 指定したプレイヤーの部屋に入ります","/21 join ", "§6またはここをクリック！"))
                sender.sendMessage(runcmd("§b/21 open ゲーム中だった場合そこのインベントリを開きます","/21 open","§6またはここをクリック！"))
                sender.sendMessage(runcmd("§b/21 log 戦績を表示します","/21 log","§6またはここをクリック！"))
                if (sender.isOp || sender.hasPermission("21.switch")) {
                    sender.sendMessage(runcmd("§c/21 switch モードを切り替えます","/21 switch","§6またはここをクリック！"))
                    if (sender.isOp) {
                        sender.sendMessage(runcmd("§c/21 switchsavemode モード変更を鯖再起後も保持するかどうかを切り替えます","/21 switchsavemode","§6またはここをクリック！"))
                    }
                    sender.sendMessage("§c赤はOP(21.switch)用のコマンドです")
                }
                sender.sendMessage("§dVersion:3.0=======TwentyOnePlugin(21)==§lAuthor:tororo_1066")
                return true

            }

            "switch" -> {
                if (sender !is Player) return true
                if (!sender.isOp && !sender.hasPermission("21.switch")) {
                    sender.sendmsg("§4あなたはこのコマンドを実行する権限がありません")
                    return true
                }
                mode = !config.getBoolean("switch.mode")
                config.set("switch.mode", !config.getBoolean("switch.mode"))
                saveConfig()

                sender.sendmsg("§bmodeを§a${config.getBoolean("switch.mode")}§bに変更しました")
                return true
            }

            "switchsavemode" -> {
                if (sender !is Player) return true
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
                return mutableListOf("help","join","start","open","log")
            }
            if (args.size == 2){
                when(args[0]){
                    "start"-> return mutableListOf("0","100","1000","10000")
                }
            }
            if (args.size == 3 && args[0] == "start")return mutableListOf("round数(1~10)")
            if (args.size == 4 && args[0] == "start")return mutableListOf("初期チップ数(10~30)")
            if (args.size == 5 && args[0] == "start")return mutableListOf("初期ベット数(1~10)")
            if (args.size == 6 && args[0] == "start")return mutableListOf("1ターンの時間(10~60)")
        }

        val list = mutableListOf<String>()

        for (p in Bukkit.getOnlinePlayers()){
            if (p.name == sender.name)continue
            list.add(p.name)
        }

        return list
    }
}