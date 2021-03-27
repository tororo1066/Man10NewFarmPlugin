package man10newfarmplugin.man10newfarmplugin

import man10newfarmplugin.man10newfarmplugin.MNF.Companion
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


object Util {

    private const val prefix = Companion.prefix
    private val plugin = Companion.plugin
    private val configdata = Companion.configdata
    private val farmdata = Companion.farmdata

    fun per(i : Double): Boolean {
        return Math.random() <= i/100
    }

    fun changeint(s: String): Int? {
        try {
            return s.toInt()
        } catch (e: NumberFormatException) {
            plugin.server.logger.warning(prefix + "文字列の数字化に失敗しました")
        }
        return null
    }

    fun givecrops(i: Int, p: Player) {
        p.inventory.setItemInMainHand(configdata.seed[i])
        p.sendMessage(prefix + "種を付与しました")
        plugin.server.logger.info("${p.name}に${configdata.seed[i].itemMeta.displayName}を付与しました")
        return
    }

    fun plantcrops(clicked: Block, item: ItemStack, p: Player) {
        val item2 = ItemStack(item)
        item2.amount = 1
        if (!configdata.seed.contains(item2))return
        val i = configdata.seed.indexOf(item2)
        if (clicked.type == configdata.canb[i]){
            if (clicked.location.add(0.0,1.0,0.0).block.lightLevel >= configdata.ll[i].toByte()){

                clicked.type = Material.FARMLAND
                clicked.location.add(0.0,1.0,0.0).block.type = Material.WHEAT
                item.amount = item.amount-1
                farmdata[clicked.location.add(0.0,1.0,0.0)] = configdata.crop[i]
                if (!item.hasItemMeta()) return
                p.sendMessage("${item.itemMeta.displayName}を植えました")
                return
            }else{
                p.sendMessage(prefix + "明かりの強さが足りません！")
                return
            }
        }else{
            p.sendMessage(prefix + "植える場所が適切ではありません！")
            return
        }
    }



    fun parm(p : Player): Boolean {
        return p.isOp || p.hasPermission("man10farm.admin")
    }


    fun gotcrop(loc : Location){
        farmdata.remove(loc)
    }


    ///////////////////////////////
    //base 64
    //////////////////////////////
    fun itemFromBase64(data: String): ItemStack? = try {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)
        val items = arrayOfNulls<ItemStack>(dataInput.readInt())

        // Read the serialized inventory
        for (i in items.indices) {
            items[i] = dataInput.readObject() as ItemStack
        }

        dataInput.close()
        items[0]
    } catch (e: Exception) {
        null
    }

    @Throws(IllegalStateException::class)
    fun itemToBase64(item: ItemStack): String {
        try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            val items = arrayOfNulls<ItemStack>(1)
            items[0] = item
            dataOutput.writeInt(items.size)

            for (i in items.indices) {
                dataOutput.writeObject(items[i])
            }

            dataOutput.close()

            return Base64Coder.encodeLines(outputStream.toByteArray())

        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }
    }




}
