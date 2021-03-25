package man10newfarmplugin.man10newfarmplugin

import man10newfarmplugin.man10newfarmplugin.MNF.Companion.d
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.da
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.plugin
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.prefix
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
        var count = 1
        while (plugin.config.isSet("farm.No$count")){
            if (i == count){
                p.inventory.setItemInMainHand(plugin.config.getItemStack("farm.No$count.seed"))
                p.sendMessage(prefix + "種を付与しました")
                plugin.server.logger.info("${p.name}に${plugin.config.getItemStack("farm.No$count.seed")?.itemMeta?.displayName}($count)を付与しました")
                return
            }
            count++
        }
    }

    fun plantcrops(clicked: Block, item: ItemStack, p: Player) {
        val item2 = ItemStack(item)
        item2.amount = 1
        if (!d.seed.contains(item2))return
        val i = d.seed.indexOf(item2)
        if (clicked.type == d.canb[i]){
            if (clicked.location.add(0.0,1.0,0.0).block.lightLevel >= d.ll[i].toByte()){

                clicked.type = Material.FARMLAND
                clicked.location.add(0.0,1.0,0.0).block.type = Material.WHEAT
                item.amount = item.amount-1
                da[clicked.location.add(0.0,1.0,0.0)] = d.crop[i]
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
        return p.isOp || p.hasPermission("admin")
    }


    fun gotcrop(loc : Location){
        da.remove(loc)
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
