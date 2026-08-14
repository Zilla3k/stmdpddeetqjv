CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    price NUMERIC(15,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    category_id UUID NOT NULL,
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
);