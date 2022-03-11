package com.khudyakovvladimir.vhcloudnotepad.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vhcloudnotepad.R
import com.khudyakovvladimir.vhcloudnotepad.model.database.products.Product
import com.khudyakovvladimir.vhcloudnotepad.view.appComponent
import com.khudyakovvladimir.vhcloudnotepad.viewmodel.NoteViewModel
import com.khudyakovvladimir.vhcloudnotepad.viewmodel.NoteViewModelFactory
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.ProductDiffUtilCallback
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.ProductRecyclerViewAdapter
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.itemtouchhelper.OnStartDragListener
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.itemtouchhelper.SimpleItemTouchHelperCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class Shopping: Fragment(), OnStartDragListener {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteViewModelFactory: NoteViewModelFactory
    private lateinit var productRecyclerViewAdapter: ProductRecyclerViewAdapter

    private lateinit var recyclerView: RecyclerView
    private var mItemTouchHelper: ItemTouchHelper? = null

    @Inject
    lateinit var factory: NoteViewModelFactory.Factory

    private lateinit  var sharedPreferences: SharedPreferences
    var mute: Boolean = false

    override fun onAttach(context: Context) {
        context.appComponent.injectShopping(this)
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

        if (sharedPreferences.contains("mute")) {
            mute = sharedPreferences.getBoolean("mute", false)
        }
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.shopping_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModelFactory = factory.createNoteViewModelFactory(activity?.application!!)
        noteViewModel = ViewModelProvider(this, noteViewModelFactory).get(NoteViewModel::class.java)

        noteViewModel.getProducts()?.observe(this, Observer {
            val oldList = productRecyclerViewAdapter.listProducts
            val newList = it
            val productDiffUtilCallback = ProductDiffUtilCallback(oldList, newList)
            val diffResult = DiffUtil.calculateDiff(productDiffUtilCallback)

            productRecyclerViewAdapter.updateAdapter(it)

            diffResult.dispatchUpdatesTo(productRecyclerViewAdapter)
        })

        recyclerView = view.findViewById(R.id.recyclerViewShopping)!!
        recyclerView.itemAnimator?.changeDuration = 0
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)

        val itemClick = { product: Product, outsideView: View -> markAsBought(product, outsideView) }
        val itemLongClick = { product: Product, outsideView: View -> changeColor(product, outsideView) }
        val itemSwipeDelete = { product: Product -> deleteProduct(product) }
        val itemLongClickMove = { productFrom: Product, productTo: Product -> synchronizeDraggedProductsToDatabase(productFrom, productTo) }

        productRecyclerViewAdapter =
            ProductRecyclerViewAdapter(
                activity!!.applicationContext,
                ArrayList(emptyList()),
                this,
                itemClick,
                itemLongClick,
                itemSwipeDelete,
                itemLongClickMove
            )
        recyclerView.adapter = productRecyclerViewAdapter

        //----------------------------------------ItemTouchHelper
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(productRecyclerViewAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(recyclerView)
        //________________________________________

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    noteViewModel.soundPoolHelper.vibrate(activity!!.applicationContext, 2)
                    noteViewModel.soundPoolHelper.playSoundImpulse(mute)

                }
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    noteViewModel.soundPoolHelper.vibrate(activity!!.applicationContext, 2)
                    noteViewModel.soundPoolHelper.playSoundImpulse(mute)
                }
            }
        })

    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        mItemTouchHelper?.startDrag(viewHolder)
    }

    private fun markAsBought(_product: Product, _view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            if(!_product.isBoought) {
                noteViewModel.productRepository.insertProduct(Product(_product.id, _product.text, isBoought = true))
            }
            if(_product.isBoought) {
                noteViewModel.productRepository.insertProduct(Product(_product.id, _product.text, isBoought = false))
            }
        }
    }

    private fun changeColor(_product: Product, _view: View): Boolean {
        _view.setBackgroundColor(Color.parseColor("#FFBB86FC"))
        return true
    }

    private fun deleteProduct(_product: Product) {
        CoroutineScope(Dispatchers.IO).launch {
            if(noteViewModel.productRepository.getProductsList()?.size!! > 1) {
                //noteViewModel.productRepository.insertProduct(Product(_product.id, "", false))
                noteViewModel.productRepository.deleteProduct(_product)
                noteViewModel.soundPoolHelper.playSoundDeletePage(mute)
            }else if (noteViewModel.productRepository.getProductsList()?.size!! == 1) {
                _product.text = ""
                noteViewModel.productRepository.insertProduct(_product)
                noteViewModel.soundPoolHelper.playSoundDeletePage(mute)
            }
        }
    }

    private fun synchronizeDraggedProductsToDatabase(_productFrom: Product, _productTo: Product) {

        CoroutineScope(Dispatchers.IO).launch {
            val currentList = noteViewModel.productRepository.getProductsList()
            val updatedList = noteViewModel.productRepository.getProductsList()

            var indexFrom = 0
            var indexTo = 0

            for (product in updatedList!!) {
                if(product.id == _productFrom.id) {
                    break
                }
                indexFrom++
            }

            for (product in updatedList) {
                if(product.id == _productTo.id) {
                    break
                }
                indexTo++
            }

            val tempProduct = updatedList[indexFrom]
            updatedList.removeAt(indexFrom)
            updatedList.add(indexTo, tempProduct)

            //__________update database
            for (product in updatedList.indices) {
                val currentProduct = currentList?.get(product)
                val updatedProduct = updatedList[product]
                noteViewModel.productRepository.updateProduct(Product(currentProduct!!.id, updatedProduct.text, updatedProduct.isBoought))
            }
        }
    }

}