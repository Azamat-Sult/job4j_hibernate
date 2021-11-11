package ru.job4j.integration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class OrdersStoreTest {
    private BasicDataSource pool = new BasicDataSource();

    @Before
    public void setUp() throws SQLException {
        pool.setDriverClassName("org.hsqldb.jdbcDriver");
        pool.setUrl("jdbc:hsqldb:mem:tests;sql.syntax_pgs=true");
        pool.setUsername("sa");
        pool.setPassword("");
        pool.setMaxTotal(2);
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("./db/update_001.sql")))
        ) {
            br.lines().forEach(line -> builder.append(line).append(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.getConnection().prepareStatement(builder.toString()).executeUpdate();
    }

    @After
    public void cleanUp() throws SQLException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("./db/update_002.sql")))
        ) {
            br.lines().forEach(line -> builder.append(line).append(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.getConnection().prepareStatement(builder.toString()).executeUpdate();
    }

    @Test
    public void whenSaveOrderAndFindAllOneRowWithDescription() {
        OrdersStore store = new OrdersStore(pool);

        store.save(Order.of("name1", "description1"));

        List<Order> all = (List<Order>) store.findAll();

        assertEquals(all.size(), 1);
        assertEquals(all.get(0).getDescription(), "description1");
        assertEquals(all.get(0).getId(), 1);
    }

    @Test
    public void whenSaveThenUpdate() {
        OrdersStore store = new OrdersStore(pool);

        Order order = store.save(Order.of("name1", "description1"));

        order.setName("name1 NEW");
        order.setDescription("description1 NEW");

        store.update(order);

        List<Order> all = (List<Order>) store.findAll();

        assertEquals(all.size(), 1);
        assertEquals(all.get(0).getName(), "name1 NEW");
        assertEquals(all.get(0).getDescription(), "description1 NEW");
        assertEquals(all.get(0).getId(), 1);
    }

    @Test
    public void whenSaveThenFindById() {
        OrdersStore store = new OrdersStore(pool);

        Order order1 = store.save(Order.of("name1", "description1"));
        Order order2 = store.save(Order.of("name2", "description2"));
        Order order3 = store.save(Order.of("name3", "description3"));

        Order foundById = store.findById(order2.getId());

        assertEquals(foundById, order2);
    }

    @Test
    public void whenSaveThenFindByName() {
        OrdersStore store = new OrdersStore(pool);

        Order order1 = store.save(Order.of("name1", "description1"));
        Order order2 = store.save(Order.of("name2", "description2"));
        Order order3 = store.save(Order.of("name3", "description3"));

        Order foundByName = store.findByName(order3.getName());

        assertEquals(foundByName, order3);
    }
}