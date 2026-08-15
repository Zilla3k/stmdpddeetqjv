package dev.henriquepelanda.api_pedidos;

import dev.henriquepelanda.api_pedidos.category.dto.CategoryResponseDTO;
import dev.henriquepelanda.api_pedidos.client.dto.ClientResponseDTO;
import dev.henriquepelanda.api_pedidos.order.dto.OrderResponseDTO;
import dev.henriquepelanda.api_pedidos.order.entity.OrderStatus;
import dev.henriquepelanda.api_pedidos.product.dto.ProductResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ApiPedidosApplicationTests {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.14-alpine")
            .withDatabaseName("api_pedidos_test")
            .withUsername("root")
            .withPassword("admin123");

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoadsAndFlywayRuns() {
        Integer count = jdbcTemplate.queryForObject("select count(*) from migration_check", Integer.class);

        assertThat(count).isEqualTo(0);
    }

    @Test
    void shouldCreateAndReadMainFlowThroughHttp() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        CategoryResponseDTO category = jsonMapper.readValue(
                restTemplate.postForEntity(
                                "/categories",
                                new HttpEntity<>("""
                                        {
                                          "name": "Electronics",
                                          "description": "General electronic products"
                                        }
                                        """, headers),
                                String.class)
                        .getBody(),
                CategoryResponseDTO.class
        );

        ClientResponseDTO client = jsonMapper.readValue(
                restTemplate.postForEntity(
                                "/clients",
                                new HttpEntity<>("""
                                        {
                                          "name": "Henrique Pelanda",
                                          "email": "henrique@example.com",
                                          "document": "12345678900",
                                          "password": "12345678",
                                          "confirmPassword": "12345678"
                                        }
                                        """, headers),
                                String.class)
                        .getBody(),
                ClientResponseDTO.class
        );

        ProductResponseDTO product = jsonMapper.readValue(
                restTemplate.postForEntity(
                                "/products",
                                new HttpEntity<>("""
                                        {
                                          "name": "T-Shirt",
                                          "description": "Basic black t-shirt",
                                          "price": 79.90,
                                          "categoryId": "%s",
                                          "stockQuantity": 10
                                        }
                                        """.formatted(category.id()), headers),
                                String.class)
                        .getBody(),
                ProductResponseDTO.class
        );

        OrderResponseDTO order = jsonMapper.readValue(
                restTemplate.postForEntity(
                                "/orders",
                                new HttpEntity<>("""
                                        {
                                          "clientId": "%s",
                                          "items": [
                                            {
                                              "productId": "%s",
                                              "quantity": 2
                                            }
                                          ]
                                        }
                                        """.formatted(client.id(), product.id()), headers),
                                String.class)
                        .getBody(),
                OrderResponseDTO.class
        );

        ResponseEntity<String> orderRead = restTemplate.getForEntity("/orders/%s".formatted(order.id()), String.class);

        assertThat(category.name()).isEqualTo("Electronics");
        assertThat(client.email()).isEqualTo("henrique@example.com");
        assertThat(product.categoryId()).isEqualTo(category.id());
        assertThat(order.clientId()).isEqualTo(client.id());
        assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.totalAmount()).isEqualByComparingTo(new BigDecimal("159.80"));
        assertThat(orderRead.getStatusCode().value()).isEqualTo(200);
    }
}
