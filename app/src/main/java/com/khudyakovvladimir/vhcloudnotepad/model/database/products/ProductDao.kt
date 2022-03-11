package com.khudyakovvladimir.vhcloudnotepad.model.database.products

import androidx.lifecycle.LiveData
import androidx.room.*
import com.khudyakovvladimir.vhcloudnotepad.model.database.Note

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    fun getProductsAsLiveDataListProduct(): LiveData<List<Product>>

    @Query("SELECT * FROM products")
    fun getProductsAsListProduct(): List<Product>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Int): Product

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(listProducts: List<Product>)

    @Delete
    fun deleteProduct(product: Product)

    @Query("DELETE FROM products WHERE id = :id")
    fun deleteProductById(id: Int)

    @Update
    fun updateProduct(product: Product)

    @Query("DELETE FROM products")
    fun deleteAllProducts()
}