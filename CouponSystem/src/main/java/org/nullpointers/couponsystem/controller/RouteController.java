package org.nullpointers.couponsystem.controller;


import org.nullpointers.couponsystem.service.CouponService;
import org.nullpointers.couponsystem.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle routing for the application.
 */
@RestController
public class RouteController {
  @Autowired
  private DataService dataService;
  @Autowired
  private CouponService couponService;

  @GetMapping({"/", "/index"})
  public String index(Model model) {
    return "Welcome to the Coupon Management System! "
        + "Direct your browser or Postman to an endpoint to make API calls.";
  }
}