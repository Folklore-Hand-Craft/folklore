package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Product type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Products")
@Index(name = "productItem", fields = {"sectionId"})
public final class Product implements Model {
  public static final QueryField ID = field("Product", "id");
  public static final QueryField PRODUCT_TITLE = field("Product", "productTitle");
  public static final QueryField PRODUCT_BODY = field("Product", "productBody");
  public static final QueryField PRODUCT_PRICE = field("Product", "productPrice");
  public static final QueryField PRODUCT_CONTACT = field("Product", "productContact");
  public static final QueryField SECTION = field("Product", "sectionId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String productTitle;
  private final @ModelField(targetType="String", isRequired = true) String productBody;
  private final @ModelField(targetType="String", isRequired = true) String productPrice;
  private final @ModelField(targetType="String", isRequired = true) String productContact;
  private final @ModelField(targetType="Section", isRequired = true) @BelongsTo(targetName = "sectionId", type = Section.class) Section section;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getProductTitle() {
      return productTitle;
  }
  
  public String getProductBody() {
      return productBody;
  }
  
  public String getProductPrice() {
      return productPrice;
  }
  
  public String getProductContact() {
      return productContact;
  }
  
  public Section getSection() {
      return section;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Product(String id, String productTitle, String productBody, String productPrice, String productContact, Section section) {
    this.id = id;
    this.productTitle = productTitle;
    this.productBody = productBody;
    this.productPrice = productPrice;
    this.productContact = productContact;
    this.section = section;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Product product = (Product) obj;
      return ObjectsCompat.equals(getId(), product.getId()) &&
              ObjectsCompat.equals(getProductTitle(), product.getProductTitle()) &&
              ObjectsCompat.equals(getProductBody(), product.getProductBody()) &&
              ObjectsCompat.equals(getProductPrice(), product.getProductPrice()) &&
              ObjectsCompat.equals(getProductContact(), product.getProductContact()) &&
              ObjectsCompat.equals(getSection(), product.getSection()) &&
              ObjectsCompat.equals(getCreatedAt(), product.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), product.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getProductTitle())
      .append(getProductBody())
      .append(getProductPrice())
      .append(getProductContact())
      .append(getSection())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Product {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("productTitle=" + String.valueOf(getProductTitle()) + ", ")
      .append("productBody=" + String.valueOf(getProductBody()) + ", ")
      .append("productPrice=" + String.valueOf(getProductPrice()) + ", ")
      .append("productContact=" + String.valueOf(getProductContact()) + ", ")
      .append("section=" + String.valueOf(getSection()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static ProductTitleStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static Product justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Product(
      id,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      productTitle,
      productBody,
      productPrice,
      productContact,
      section);
  }
  public interface ProductTitleStep {
    ProductBodyStep productTitle(String productTitle);
  }
  

  public interface ProductBodyStep {
    ProductPriceStep productBody(String productBody);
  }
  

  public interface ProductPriceStep {
    ProductContactStep productPrice(String productPrice);
  }
  

  public interface ProductContactStep {
    SectionStep productContact(String productContact);
  }
  

  public interface SectionStep {
    BuildStep section(Section section);
  }
  

  public interface BuildStep {
    Product build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements ProductTitleStep, ProductBodyStep, ProductPriceStep, ProductContactStep, SectionStep, BuildStep {
    private String id;
    private String productTitle;
    private String productBody;
    private String productPrice;
    private String productContact;
    private Section section;
    @Override
     public Product build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Product(
          id,
          productTitle,
          productBody,
          productPrice,
          productContact,
          section);
    }
    
    @Override
     public ProductBodyStep productTitle(String productTitle) {
        Objects.requireNonNull(productTitle);
        this.productTitle = productTitle;
        return this;
    }
    
    @Override
     public ProductPriceStep productBody(String productBody) {
        Objects.requireNonNull(productBody);
        this.productBody = productBody;
        return this;
    }
    
    @Override
     public ProductContactStep productPrice(String productPrice) {
        Objects.requireNonNull(productPrice);
        this.productPrice = productPrice;
        return this;
    }
    
    @Override
     public SectionStep productContact(String productContact) {
        Objects.requireNonNull(productContact);
        this.productContact = productContact;
        return this;
    }
    
    @Override
     public BuildStep section(Section section) {
        Objects.requireNonNull(section);
        this.section = section;
        return this;
    }
    
    /** 
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String productTitle, String productBody, String productPrice, String productContact, Section section) {
      super.id(id);
      super.productTitle(productTitle)
        .productBody(productBody)
        .productPrice(productPrice)
        .productContact(productContact)
        .section(section);
    }
    
    @Override
     public CopyOfBuilder productTitle(String productTitle) {
      return (CopyOfBuilder) super.productTitle(productTitle);
    }
    
    @Override
     public CopyOfBuilder productBody(String productBody) {
      return (CopyOfBuilder) super.productBody(productBody);
    }
    
    @Override
     public CopyOfBuilder productPrice(String productPrice) {
      return (CopyOfBuilder) super.productPrice(productPrice);
    }
    
    @Override
     public CopyOfBuilder productContact(String productContact) {
      return (CopyOfBuilder) super.productContact(productContact);
    }
    
    @Override
     public CopyOfBuilder section(Section section) {
      return (CopyOfBuilder) super.section(section);
    }
  }
  
}
