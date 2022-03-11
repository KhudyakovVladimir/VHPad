package com.khudyakovvladimir.vhcloudnotepad.repository

import androidx.lifecycle.LiveData
import com.khudyakovvladimir.vhcloudnotepad.model.database.products.Product
import com.khudyakovvladimir.vhcloudnotepad.model.database.products.ProductDao
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ProductRepository @Inject constructor(private var productDao: ProductDao) {
    private var products: LiveData<List<Product>>? = null
    private var productsList: ArrayList<Product>? = null

    fun getProductsList(): ArrayList<Product>? {
        runBlocking {
            productsList = ArrayList(productDao.getProductsAsListProduct())
        }
        return productsList
    }

    fun getProducts(): LiveData<List<Product>>? {
        runBlocking {
            products = productDao.getProductsAsLiveDataListProduct()
        }
        return products
    }

    fun getProductById(id : Int): Product? {
        var resultProduct: Product? = null
        runBlocking {
            resultProduct = productDao.getProductById(id)
        }
        return resultProduct
    }

    fun insertProduct(product: Product){
        runBlocking {
            productDao.insertProduct(product)
        }
    }

    fun insertAllProducts(listProducts: List<Product>) {
        runBlocking {
            productDao.insertAllProducts(listProducts)
        }
    }

    fun deleteProduct(product: Product){
        runBlocking {
            productDao.deleteProduct(product)
        }
    }

    fun deleteProductById(id: Int){
        runBlocking { this.launch {
            productDao.deleteProductById(id)
        } }
    }

    fun updateProduct(product: Product){
        runBlocking {
            productDao.updateProduct(product)
        }
    }

    fun deleteAllProducts() {
        runBlocking {
            productDao.deleteAllProducts()
        }
    }
}