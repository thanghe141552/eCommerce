package com.example.demo.controller;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class OrderControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    UserRepository userRepository;

    private final HttpHeaders httpHeaders = new HttpHeaders();

    @Before
    public void setup() throws Exception {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("thang");
        user.setPassword("password1234");
        user.setConfirmPassword("password1234");

        User result = userRepository.findByUsername(user.getUsername());
        String endpoint = Objects.isNull(result) ? "create" : "login";
        MvcResult response = mvc.perform(
                        post("/api/user/".concat(endpoint))
                                .content(mapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, response.getResponse().getHeader("Authorization"));
    }

    @Test
    public void submit() throws Exception {
        mvc.perform(
                        post("/api/order/submit")
                                .headers(httpHeaders))
                .andExpect(status().isOk());
    }

    @Test
    public void getOrdersForUser() throws Exception {
        mvc.perform(
                        get("/api/order/history")
                                .headers(httpHeaders))
                .andExpect(status().isOk());
    }
}