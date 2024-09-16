package service.product;

import exception.ResourceNotFoundException;
import model.product.UpdateProductRequest;
import repository.category.CategoryRepository;
import repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import model.category.Category;
import model.product.Product;
import model.product.AddProductRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product addProduct(AddProductRequest addProductRequest) {

        // check if category exists in db
        // add this product to category
        // else, create new category and add this product

        Category category = Optional.ofNullable(categoryRepository.findByName(addProductRequest.getCategory().getName())).orElseGet(() -> {
            Category newCategory = new Category(addProductRequest.getCategory().getName());
            return categoryRepository.save(newCategory);
        });
        return productRepository.save(createProduct(addProductRequest, category));
    }

    private Product createProduct(AddProductRequest addProductRequest, Category category) {
        return new Product(addProductRequest.getName(), addProductRequest.getBrand(), addProductRequest.getPrice(), addProductRequest.getInventory(), addProductRequest.getDescription(), category);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with id: " + productId + " not found"));
    }

    @Override
    public void deleteProductById(Long productId) {
        productRepository.findById(productId).ifPresentOrElse(productRepository::delete, () -> {
            throw new ResourceNotFoundException("Product with id: " + productId + " not found");
        });
    }

    @Override
    public Product updateProduct(UpdateProductRequest updateProductRequest, Long productId) {
        return productRepository.findById(productId).map(existingProduct -> updateExistingProduct(existingProduct, updateProductRequest)).map(productRepository::save).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product updateExistingProduct(Product existingProduct, UpdateProductRequest updateProductRequest) {
        existingProduct.setName(updateProductRequest.getName());
        existingProduct.setBrand(updateProductRequest.getBrand());
        existingProduct.setPrice(updateProductRequest.getPrice());
        existingProduct.setInventory(updateProductRequest.getInventory());
        existingProduct.setDescription(updateProductRequest.getDescription());

        Category category = categoryRepository.findByName(updateProductRequest.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;
    }

    @Override
    public List<Product> getAllProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public int countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);

    }
}
