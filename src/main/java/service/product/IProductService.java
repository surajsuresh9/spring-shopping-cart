package service.product;

import model.product.AddProductRequest;
import model.product.Product;
import model.product.UpdateProductRequest;

import java.util.List;

public interface IProductService {

    Product addProduct(AddProductRequest product);

    List<Product> getAllProducts();

    Product getProductById(Long productId);

    void deleteProductById(Long productId);

    Product updateProduct(UpdateProductRequest updateProductRequest, Long productId);

    List<Product> getAllProductsByCategory(String category);

    List<Product> getProductsByBrand(String brand);

    List<Product> getProductsByCategoryAndBrand(String category, String brand);

    List<Product> getProductsByName(String name);

    List<Product> getProductsByBrandAndName(String brand, String name);

    int countProductsByBrandAndName(String brand, String name);
}
