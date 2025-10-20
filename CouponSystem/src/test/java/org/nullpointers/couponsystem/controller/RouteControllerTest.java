package org.nullpointers.couponsystem.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nullpointers.couponsystem.service.CouponService;
import org.nullpointers.couponsystem.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteController.class)
class RouteControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DataService dataService;

  @MockitoBean
  private CouponService couponService;

  @Test
  @DisplayName("GET / returns welcome message")
  void getRootReturnsWelcomeMessage() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Welcome")));
  }
}

