package com.example.floklores.Models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProductItem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "product_name")
    private String productTitle;

    @ColumnInfo(name = "product_body")
    private String productBody;

    @ColumnInfo(name = "product_price")
    private String productPrice;

    @ColumnInfo(name = "product_contact")
    private String productContact;

    public ProductItem(String productTitle, String productBody, String productPrice, String productContact) {
        this.productTitle = productTitle;
        this.productBody = productBody;
        this.productPrice = productPrice;
        this.productContact = productContact;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductBody() {
        return productBody;
    }

    public void setProductBody(String productBody) {
        this.productBody = productBody;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductContact() {
        return productContact;
    }

    public void setProductContact(String productContact) {
        this.productContact = productContact;
    }
}
