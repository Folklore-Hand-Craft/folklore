package com.example.floklores.Infrastructure;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.floklores.Models.ProductItem;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    void addTask(ProductItem productItem);

//    @Delete
//    void deleteProduct(ProductItem productItem);

    @Query("SELECT * FROM productitem")
    List<ProductItem> findAll();

    @Query("SELECT * FROM productitem WHERE product_name LIKE :name")
    ProductItem findByName(String name);

    @Query("SELECT * FROM productitem WHERE product_name LIKE :id")
    ProductItem findById(Long id);
}
