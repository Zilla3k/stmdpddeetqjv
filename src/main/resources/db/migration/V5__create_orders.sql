CREATE TABLE orders (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    total_amount NUMERIC(15,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_orders_client
        FOREIGN KEY (client_id)
            REFERENCES clients(id)
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(15,2) NOT NULL,
    subtotal NUMERIC(15,2) NOT NULL,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id)
            REFERENCES products(id)
);
