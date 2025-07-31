package ro.robert.store.management.product.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ro.robert.store.management.exception.entity.ServiceErrorType;
import ro.robert.store.management.exception.entity.ServiceException;
import ro.robert.store.management.product.boundary.ProductRepository;
import ro.robert.store.management.product.entity.ProductEntity;
import ro.robert.store.management.product.entity.request.ProductCreateRequest;
import ro.robert.store.management.product.entity.request.UpdatePriceRequest;
import ro.robert.store.management.product.entity.request.UpdateStockRequest;
import ro.robert.store.management.product.entity.request.UnsupportedUpdateRequest;
import ro.robert.store.management.product.entity.response.ProductPagedResponse;
import ro.robert.store.management.product.entity.response.ProductResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private ProductEntity testProductEntity;
    private ProductResponse testProductResponse;
    private ProductCreateRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        testProductEntity = generateProductEntityV1();
        testProductResponse = generateProductResponseV1(testProductEntity.getCreatedAt(), testProductEntity.getUpdatedAt());
        testCreateRequest = generateProductCreateRequestV1();
    }

    @Test
    @DisplayName("Create Product - Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Define
        when(productMapper.toEntity(testCreateRequest)).thenReturn(testProductEntity);
        when(productRepository.save(testProductEntity)).thenReturn(testProductEntity);
        when(productMapper.toResponse(testProductEntity)).thenReturn(testProductResponse);

        // Execute
        ProductResponse result = productService.createProduct(testCreateRequest);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("99.99"));

        verify(productMapper).toEntity(testCreateRequest);
        verify(productRepository).save(testProductEntity);
        verify(productMapper).toResponse(testProductEntity);
    }

    @DisplayName("Create Product - Should handle mapper exception during creation")
    void shouldHandleMapperExceptionDuringCreation() {
        // Define
        when(productMapper.toEntity(testCreateRequest)).thenThrow(new RuntimeException("Mapping error"));

        // Execute & Verify
        var thrownException = assertThrows(RuntimeException.class, () -> productService.createProduct(testCreateRequest));

        assertThat(thrownException.getMessage()).isEqualTo("Mapping error");
        verify(productMapper).toEntity(testCreateRequest);
        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Get all products - Should return paged products successfully")
    void shouldReturnPagedProductsSuccessfully() {
        // Define
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductEntity> entities = List.of(testProductEntity);
        Page<ProductEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(productRepository.findAll(pageable)).thenReturn(entityPage);
        when(productMapper.toResponse(testProductEntity)).thenReturn(testProductResponse);

        // Execute
        ProductPagedResponse result = productService.getAllProducts(pageable);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();

        verify(productRepository).findAll(pageable);
        verify(productMapper).toResponse(testProductEntity);
    }

    @Test
    @DisplayName("Get all products - Should return empty page when no products exist")
    void shouldReturnEmptyPageWhenNoProductsExist() {
        // Define
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(productRepository.findAll(pageable)).thenReturn(emptyPage);

        // Execute
        ProductPagedResponse result = productService.getAllProducts(pageable);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.isEmpty()).isTrue();

        verify(productRepository).findAll(pageable);
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Get Product By ID - Should return product when found")
    void shouldReturnProductWhenFound() {
        // Define
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProductEntity));
        when(productMapper.toResponse(testProductEntity)).thenReturn(testProductResponse);

        // Execute
        ProductResponse result = productService.getProductById(productId);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Test Product");

        verify(productRepository).findById(productId);
        verify(productMapper).toResponse(testProductEntity);
    }

    @Test
    @DisplayName("Get Product By ID - Should throw ServiceException when product not found")
    void shouldThrowServiceExceptionWhenProductNotFound() {
        // Define
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Execute & Verify
        var thrownException = assertThrows(ServiceException.class, () -> productService.getProductById(productId));

        assertThat(thrownException.getErrorType()).isEqualTo(ServiceErrorType.PRODUCT_NOT_FOUND);
        verify(productRepository).findById(productId);
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Update Product - Should update product price successfully")
    void shouldUpdateProductPriceSuccessfully() {
        // Define
        Long productId = 1L;
        BigDecimal newPrice = new BigDecimal("149.99");
        UpdatePriceRequest updateRequest = new UpdatePriceRequest("UpdatePriceRequest", newPrice);

        ProductEntity updatedEntity = generateProductEntityV1();
        updatedEntity.setPrice(newPrice);

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProductEntity));
        when(productRepository.save(testProductEntity)).thenReturn(updatedEntity);
        when(productMapper.toResponse(updatedEntity)).thenReturn(testProductResponse);

        // Execute
        ProductResponse result = productService.updateProduct(productId, updateRequest);

        // Verify
        assertThat(result).isNotNull();
        assertThat(testProductEntity.getPrice()).isEqualTo(newPrice);

        verify(productRepository).findById(productId);
        verify(productRepository).save(testProductEntity);
        verify(productMapper).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Update Product - Should update product stock successfully")
    void shouldUpdateProductStockSuccessfully() {
        // Define
        Long productId = 1L;
        Integer newQuantity = 25;
        UpdateStockRequest updateRequest = new UpdateStockRequest("UpdateStockRequest", newQuantity);

        ProductEntity updatedEntity = generateProductEntityV1();
        updatedEntity.setQuantity(newQuantity);

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProductEntity));
        when(productRepository.save(testProductEntity)).thenReturn(updatedEntity);
        when(productMapper.toResponse(updatedEntity)).thenReturn(testProductResponse);

        // Execute
        ProductResponse result = productService.updateProduct(productId, updateRequest);

        // Verify
        assertThat(result).isNotNull();
        assertThat(testProductEntity.getQuantity()).isEqualTo(newQuantity);

        verify(productRepository).findById(productId);
        verify(productRepository).save(testProductEntity);
        verify(productMapper).toResponse(updatedEntity);
    }

    @Test
    @DisplayName("Update Product - Should throw ServiceException for unsupported update type")
    void shouldThrowServiceExceptionForUnsupportedUpdateType() {
        // Define
        Long productId = 1L;
        UnsupportedUpdateRequest updateRequest = new UnsupportedUpdateRequest("UnsupportedType");

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProductEntity));

        // Execute & Verify
        var thrownException = assertThrows(ServiceException.class, () -> productService.updateProduct(productId, updateRequest));

        assertThat(thrownException.getErrorType()).isEqualTo(ServiceErrorType.UNSUPPORTED_UPDATE_TYPE);

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Update Product - Should throw ServiceException when product not found for update")
    void shouldThrowServiceExceptionExecuteProductNotFoundForUpdate() {
        // Define
        Long productId = 999L;
        UpdatePriceRequest updateRequest = new UpdatePriceRequest("UpdatePriceRequest", new BigDecimal("149.99"));

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Execute & Verify
        var thrownException = assertThrows(ServiceException.class, () -> productService.updateProduct(productId, updateRequest));

        assertThat(thrownException.getErrorType()).isEqualTo(ServiceErrorType.PRODUCT_NOT_FOUND);

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Delete Product - Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        // Define
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProductEntity));

        // Execute
        productService.deleteProduct(productId);

        // Verify
        verify(productRepository).findById(productId);
        verify(productRepository).delete(testProductEntity);
    }

    @Test
    @DisplayName("Delete Product - Should throw ServiceException when product not found for deletion")
    void shouldThrowServiceExceptionExecuteProductNotFoundForDeletion() {
        // Define
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Execute & Verify
        var thrownException = assertThrows(ServiceException.class, () -> productService.deleteProduct(productId));

        assertThat(thrownException.getErrorType()).isEqualTo(ServiceErrorType.PRODUCT_NOT_FOUND);

        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(any());
    }

    private ProductEntity generateProductEntityV1() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setName("Test Product");
        productEntity.setDescription("Test Description");
        productEntity.setCategory("Test Category");
        productEntity.setPrice(new BigDecimal("99.99"));
        productEntity.setQuantity(10);
        productEntity.setCreatedAt(LocalDateTime.now());
        productEntity.setUpdatedAt(LocalDateTime.now());
        return productEntity;
    }

    private ProductResponse generateProductResponseV1(LocalDateTime createdAt, LocalDateTime updatedAt) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Test Product");
        productResponse.setDescription("Test Description");
        productResponse.setCategory("Test Category");
        productResponse.setPrice(new BigDecimal("99.99"));
        productResponse.setQuantity(10);
        productResponse.setCreatedAt(createdAt);
        productResponse.setUpdatedAt(updatedAt);
        return productResponse;
    }

    private ProductCreateRequest generateProductCreateRequestV1() {
        ProductCreateRequest productCreateRequest = new ProductCreateRequest();
        productCreateRequest.setName("Test Product");
        productCreateRequest.setDescription("Test Description");
        productCreateRequest.setCategory("Test Category");
        productCreateRequest.setPrice(new BigDecimal("99.99"));
        productCreateRequest.setQuantity(10);
        return productCreateRequest;
    }
}
