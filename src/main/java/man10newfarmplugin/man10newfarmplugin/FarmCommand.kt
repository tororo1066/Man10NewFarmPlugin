package man10newfarmplugin.man10newfarmplugin

import man10newfarmplugin.man10newfarmplugin.MNF.Companion.da
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.plugin
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.prefix
import man10newfarmplugin.man10newfarmplugin.Util.addcrops
import man10newfarmplugin.man10newfarmplugin.Util.changeint
import man10newfarmplugin.man10newfarmplugin.Util.givecrops
import man10newfarmplugin.man10newfarmplugin.Util.itemToBase64
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

object FarmCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty()){
            if (sender !is Player)return true
            sender.sendMessage(da.keys.toString() + "  " + da.values)
            return true
        }

        when(args[0]){
            "test"->{
                da.clear()
            }
            "addboruns"->{
                if (sender !is Player)return true
                if (args.size != 2)return true

                try {
                    if (args[1].toInt() !in 1..12){
                        sender.sendMessage(prefix + "1~12!")
                        return true
                    }
                }catch (e : NumberFormatException){
                    sender.sendMessage(prefix + "args[2]は数字！")
                    return true
                }
                val inv = Bukkit.createInventory(null,27,"$prefix ${args[1]}.yml")
                val con = File("plugins/${plugin.name}/${args[1]}.yml")
                if (!con.exists()){
                    con.createNewFile()
                    sender.openInventory(inv)
                }else{
                    val config = YamlConfiguration.loadConfiguration(con)
                    for (i in 0..24){
                        if (config.isSet("saveinv.$i")){
                            inv.setItem(i,config.getItemStack("saveinv.$i"))
                        }
                    }
                }


            }
            "add"->{
                if (!sender.hasPermission("admin"))return true
                if (sender !is Player)return true
                if (args.size != 4)return true
                val inv = sender.inventory
                changeint(args[3])?.let { changeint(args[2])?.let { it1 -> inv.getItem(4)?.type?.let { it2 -> changeint(args[1])?.let { it3 -> addcrops(it3, inv.getItem(0)!!, inv.getItem(1)?.type!!, inv.getItem(2)!!, inv.getItem(3)?.type!!, it2, it1, it,sender) } } } }
            }
            "givecrops","give","gc"->{
                if (!sender.hasPermission("admin"))return true
                if (args.size != 2)return true
                if (sender !is Player)return true
                changeint(args[1])?.let { givecrops(it,sender) }
            }

            "breakitem"->{
                if (!sender.hasPermission("admin"))return true
                if (args.size != 1)return true
                if (sender !is Player)return true
                plugin.config.set("farm.cropbreakitem", itemToBase64(sender.inventory.itemInMainHand))
                plugin.saveConfig()
                sender.sendMessage(prefix + "コンフィグへの書き込みが完了しました")
                return true
            }


        }
        return true
    }
}