package com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.khudyakovvladimir.vhcloudnotepad.model.database.products.Product

class ProductDiffUtilCallback (
    private val oldList: List<Product>,
    private val newList: List<Product>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id &&
                oldList[oldItemPosition].text == newList[newItemPosition].text &&
                oldList[oldItemPosition].isBoought == newList[newItemPosition].isBoought
    }
}