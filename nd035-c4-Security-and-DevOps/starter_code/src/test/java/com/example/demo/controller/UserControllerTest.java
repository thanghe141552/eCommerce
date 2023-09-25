package com.example.demo.controller;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper;

    @Autowired
    UserRepository userRepository;

    private final HttpHeaders httpHeaders = new HttpHeaders();

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        createUser();
    }

    public void createUser() throws Exception {
        CreateUserRequest user = getUserRequest();
        MvcResult response = mvc.perform(
                        post("/api/user/create")
                                .content(mapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, response.getResponse().getHeader("Authorization"));
    }

    @Test
    public void login() throws Exception {
        User user = getUserLogin();
        MvcResult response = mvc.perform(
                        post("/login")
                                .content(mapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, response.getResponse().getHeader("Authorization"));
    }

    @Test
    public void findById() throws Exception {
        setAuthHeader();
        mvc.perform(get("/api/user/id/1").headers(httpHeaders))
                .andExpect(status().isOk());
    }

    @Test
    public void findByUserName() throws Exception {
        setAuthHeader();
        mvc.perform(get("/api/user/thang").headers(httpHeaders))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserInfo() throws Exception {
        setAuthHeader();
        mvc.perform(get("/api/user/userInfo").headers(httpHeaders))
                .andExpect(status().isOk());
    }

    private CreateUserRequest getUserRequest() {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("thang");
        user.setPassword("password1234");
        user.setConfirmPassword("password1234");
        return user;
    }

    private User getUserLogin() {
        User user = new User();
        user.setUsername("thang");
        user.setPassword("password1234");
        return user;
    }

    private void setAuthHeader() throws Exception {
        CreateUserRequest user = getUserRequest();
        User result = userRepository.findByUsername(user.getUsername());
        if (Objects.isNull(result)) createUser();
        else login();
    }
}