-- V1__create_product_table.sql
-- Create product table with constraints and indexes

-- Create product table
CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Add constraints
    CONSTRAINT product_price_positive CHECK (price > 0),
    CONSTRAINT product_quantity_non_negative CHECK (quantity >= 0),
    CONSTRAINT product_name_not_empty CHECK (TRIM(name) != '')
);

-- Create indexes
CREATE INDEX IF NOT EXISTS product_name_idx ON product(name);
CREATE INDEX IF NOT EXISTS product_category_idx ON product(category) WHERE category IS NOT NULL;
