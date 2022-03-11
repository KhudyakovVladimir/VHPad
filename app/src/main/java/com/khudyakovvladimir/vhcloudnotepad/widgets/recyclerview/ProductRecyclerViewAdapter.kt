package com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vhcloudnotepad.R
import com.khudyakovvladimir.vhcloudnotepad.model.database.products.Product
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.itemtouchhelper.ItemTouchHelperAdapter
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.itemtouchhelper.ItemTouchHelperViewHolder
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.itemtouchhelper.OnStartDragListener
import java.util.*

class ProductRecyclerViewAdapter(
    val context: Context,
    var listProducts: List<Product>,
    dragStartListener: OnStartDragListener,
    private val itemClick: (product: Product, view: View) -> Unit,
    private val itemLongClick: (product: Product, view: View) -> Boolean,
    private val itemSwipeDelete: (product: Product) -> Unit,
    private val itemLongClickMove: (productFrom: Product, productTo: Product) -> Unit,
): RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductHolder>(), ItemTouchHelperAdapter {

    private var lastPosition = -1
    private var newList: List<Product> = emptyList()
    private var flag: Boolean = false
    private lateinit var product: Product
    private val mDragStartListener: OnStartDragListener = dragStartListener

    inner class ProductHolder(itemView: View): RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

        lateinit var textViewHolder: TextView
        lateinit var textViewItem: TextView
        lateinit var textViewShoppingItem: TextView

        fun bind(_product: Product) {
            product = _product

            textViewHolder = itemView.findViewById(R.id.holderViewShoppingItem)
            textViewShoppingItem = itemView.findViewById(R.id.textViewShoppingItem)
            textViewItem = itemView.findViewById(R.id.buttonShoppingItem)


            if(_product.isBoought) {
                textViewShoppingItem.setText(_product.text, TextView.BufferType.SPANNABLE)
                val spannable = textViewShoppingItem.text as Spannable
                spannable.setSpan(StrikethroughSpan(), 0, _product.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            if(!_product.isBoought) {
                textViewShoppingItem.text = _product.text
            }

            textViewItem.setOnClickListener {
                itemClick(_product, textViewShoppingItem)
            }

            textViewShoppingItem.setOnLongClickListener {
                itemLongClick(_product, textViewShoppingItem)
            }
        }

        override fun onItemSelected() {

        }

        override fun onItemClear() {
            if (flag) {
                itemLongClickMove(newList[0], newList[1])
            }
            flag = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        return ProductHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_shopping_item, parent, false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(listProducts[position])

        holder.textViewHolder.setOnTouchListener { v, event ->
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onStartDrag(holder)
            }
            false
        }
    }

    override fun getItemCount(): Int {
        return listProducts.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    fun updateAdapter(_listProducts: List<Product>) {
        this.listProducts.apply {
            listProducts = emptyList()
            val tempList = listProducts.toMutableList()
            val tempList2 = _listProducts.toMutableList()
            tempList.addAll(tempList2)
            val resultList = tempList2.toList()
            listProducts = resultList
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        newList =  getNewList(listProducts[fromPosition], listProducts[toPosition])

        Collections.swap(listProducts, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        return true
    }

    override fun onItemDismiss(position: Int) {
        val note = listProducts[position]

        itemSwipeDelete(listProducts[position])

        val tempList = ArrayList(listProducts)
        tempList.removeAt(position)
        val list = tempList as List<Product>
        listProducts = list

        flag = false

        notifyItemRemoved(position)
    }

    private fun getNewList(product1: Product, product2: Product): List<Product> {
        flag = true
        return listOf(product1, product2)
    }
}
