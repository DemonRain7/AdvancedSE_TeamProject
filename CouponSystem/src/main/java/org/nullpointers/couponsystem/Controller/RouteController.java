package org.nullpointers.couponsystem.Controller;

import org.nullpointers.couponsystem.Model.Entities.User;
import org.nullpointers.couponsystem.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to handle routing for the application.
 */
@RestController
public class RouteController {
  @autowired
  private DataService dataService;
  @Autowired
  private CouponService couponService;

  @GetMapping("/", "/index")
  public String home(Model model) {
}