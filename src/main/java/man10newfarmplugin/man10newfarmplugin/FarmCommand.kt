package man10newfarmplugin.man10newfarmplugin

import man10newfarmplugin.man10newfarmplugin.MNF.Companion.da
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.plugin
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.prefix
import man10newfarmplugin.man10newfarmplugin.Util.changeint
import man10newfarmplugin.man10newfarmplugin.Util.givecrops
import man10newfarmplugin.man10newfarmplugin.Util.itemToBase64
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FarmCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty()){
            if (sender !is Player)return true
            sender.sendMessage(da.keys.toString() + "  " + da.values)
            return true
        }

        when(args[0]){
            "add"->{
                if (!sender.hasPermission("admin"))return true
                if (sender !is Player)return true
                if (args.size != 4)return true
                val inv = sender.inventory
                plugin.config.set("farm.No${changeint(args[1]) ?:return true}.seed",inv.getItem(0))
                plugin.config.set("farm.No${changeint(args[1])}.crop",inv.getItem(1))
                plugin.config.set("farm.No${changeint(args[1])}.cropblock",inv.getItem(2)?.type?.name)
                plugin.config.set("farm.No${changeint(args[1])}.canb",inv.getItem(3)?.type?.name)
                plugin.config.set("farm.No${changeint(args[1])}.cropbreakitem",inv.getItem(4))
                plugin.config.set("farm.No${changeint(args[1])}.growc", changeint(args[2]))
                plugin.config.set("farm.No${changeint(args[1])}.ll", changeint(args[3]))
                plugin.saveConfig()
                sender.sendMessage(prefix + "configの更新が完了しました！")
                return true
            }
            "addothercrops","addoc"->{
                if (!sender.hasPermission("admin"))return true
                if (sender !is Player)return true
                if (args.size != 5)return true
                if (!plugin.config.isSet("farm.No${changeint(args[1]) ?:return true}"))return true
                val l = plugin.config.getStringList("farm.No${changeint(args[1])}.othercrops")
                l.add("${itemToBase64(sender.inventory.itemInMainHand)}:${args[2]}:${args[3]}:${args[4]}")
                plugin.config.set("farm.No${changeint(args[1])}.othercrops",l)
                plugin.saveConfig()
                sender.sendMessage(prefix + "othercropsの追加が完了しました")
                return true
            }
            "resetothercrops","resetoc"->{
                if (!sender.hasPermission("admin"))return true
                if (sender !is Player)return true
                if (args.size != 2)return true
                val l = plugin.config.getStringList("farm.No${changeint(args[1]) ?:return true}.othercrops")
                l.clear()
                plugin.config.set("farm.No${changeint(args[1])}.othercrops",l)
                sender.sendMessage(prefix + "othercropsを削除しました")
                return true
            }
            "givecrops","give","gc"->{
                if (!sender.hasPermission("admin"))return true
                if (args.size != 2)return true
                if (sender !is Player)return true
                changeint(args[1])?.let { givecrops(it,sender) }
            }




        }

        return true
    }


}