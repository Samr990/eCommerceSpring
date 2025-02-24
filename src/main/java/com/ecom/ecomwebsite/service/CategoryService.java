package com.ecom.ecomwebsite.service;

import com.ecom.ecomwebsite.dto.CategoryDTO;
import com.ecom.ecomwebsite.dto.ProductDTO;
import com.ecom.ecomwebsite.model.Category;
import com.ecom.ecomwebsite.model.Product;
import com.ecom.ecomwebsite.model.RoleType;
import com.ecom.ecomwebsite.model.User;
import com.ecom.ecomwebsite.repository.CategoryRepository;
import com.ecom.ecomwebsite.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // Convert Category Entity to DTO (Includes Products)
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());

        // Convert List<Product> to List<ProductDTO>
        List<ProductDTO> productDTOs = category.getProducts().stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

        dto.setProducts(productDTOs);
        return dto;
    }

    // Convert Product Entity to ProductDTO
    private ProductDTO convertToProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());  // ✅ Ensure productId is set
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        
        // ✅ Fix: Set categoryId from product's category
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getCategoryId());
        }

        return dto;
    }

    // ✅ Fetch all categories (Optionally includes products)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Fetch a single category by ID (Includes Products in Category)
    public ResponseEntity<?> getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        
        if (category.isPresent()) {
            CategoryDTO categoryDTO = convertToDTO(category.get());
            return ResponseEntity.ok(categoryDTO);
        } else {
            return ResponseEntity.status(404).body("Category not found with ID: " + id);
        }
    }

    // ✅ Create a New Category (Admin Only)
    public ResponseEntity<String> createCategory(CategoryDTO categoryDTO, String adminEmail) {
        Optional<User> adminUser = userRepository.findByEmail(adminEmail);
        
        if (adminUser.isEmpty() || adminUser.get().getRole() != RoleType.ADMIN) {
            return ResponseEntity.status(403).body("Access Denied: Only Admins can create categories.");
        }

        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());

        categoryRepository.save(category);
        return ResponseEntity.ok("Category created successfully.");
    }

    // ✅ Update Category (Admin Only)
    public ResponseEntity<String> updateCategory(Long categoryId, CategoryDTO categoryDTO, String adminEmail) {
        Optional<User> adminUser = userRepository.findByEmail(adminEmail);
        
        if (adminUser.isEmpty() || adminUser.get().getRole() != RoleType.ADMIN) {
            return ResponseEntity.status(403).body("Access Denied: Only Admins can update categories.");
        }

        Optional<Category> existingCategory = categoryRepository.findById(categoryId);
        if (existingCategory.isEmpty()) {
            return ResponseEntity.status(404).body("Category not found with ID: " + categoryId);
        }

        Category category = existingCategory.get();
        category.setCategoryName(categoryDTO.getCategoryName());

        categoryRepository.save(category);
        return ResponseEntity.ok("Category updated successfully.");
    }

    // ✅ Delete Category (Admin Only)
    public ResponseEntity<String> deleteCategory(Long categoryId, String adminEmail) {
        Optional<User> adminUser = userRepository.findByEmail(adminEmail);

        if (adminUser.isEmpty() || adminUser.get().getRole() != RoleType.ADMIN) {
            return ResponseEntity.status(403).body("Access Denied: Only Admins can delete categories.");
        }

        Optional<Category> existingCategory = categoryRepository.findById(categoryId);
        if (existingCategory.isEmpty()) {
            return ResponseEntity.status(404).body("Category not found with ID: " + categoryId);
        }

        categoryRepository.delete(existingCategory.get());
        return ResponseEntity.ok("Category deleted successfully.");
    }
}
