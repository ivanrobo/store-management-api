package ro.robert.store.management.product.boundary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ro.robert.store.management.annotation.TrackExecutionTime;
import ro.robert.store.management.exception.entity.ServiceErrorResponse;
import ro.robert.store.management.product.entity.request.ProductCreateRequest;
import ro.robert.store.management.product.entity.request.ProductUpdateRequest;
import ro.robert.store.management.product.entity.response.ProductPagedResponse;
import ro.robert.store.management.product.entity.response.ProductResponse;
import ro.robert.store.management.product.control.ProductService;

@Tag(name = "Product Management", description = "API for managing store products")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @Operation(summary = "Create a new product", description = "Creates a new product in the store inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
    })
    @SecurityRequirement(name = "basicAuth")
    @PostMapping
    @TrackExecutionTime("Create Product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductPagedResponse.class)))
    })
    @GetMapping
    @TrackExecutionTime("Get All Products")
    public ResponseEntity<ProductPagedResponse> getAllProducts(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "name")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction", example = "ASC")
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            sort = Sort.by(sortDirection, sortBy);
        }
        
        Pageable pageable = PageRequest.of(page, size, sort);
        ProductPagedResponse products = productService.getAllProducts(pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    
    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "404", description = "Product not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceErrorResponse.class)))
    })
    @GetMapping("/{id}")
    @TrackExecutionTime("Get Product By ID")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
    
    @Operation(summary = "Update product", description = "Updates specific fields of an existing product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "404", description = "Product not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
    })
    @SecurityRequirement(name = "basicAuth")
    @PatchMapping("/{id}")
    @TrackExecutionTime("Update Product")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id, 
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @Operation(summary = "Delete product", description = "Deletes a product from the store inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
    })
    @SecurityRequirement(name = "basicAuth")
    @DeleteMapping("/{id}")
    @TrackExecutionTime("Delete Product")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
