package com.zk.demo.stripeController;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.zk.demo.model.CustomerData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StripeController {


    @Value("${stripe.apiKey}")
    String stripeKey;

    @PostMapping("/createCustomer")
    public CustomerData createCustomer(@RequestBody CustomerData data) throws StripeException {
        Stripe.apiKey = stripeKey;

        Map<String, Object> params = new HashMap<>();
        params.put("name", data.getName());
        params.put("email", data.getEmail());
        Customer customer = Customer.create(params);
        data.setCustomerId(customer.getId());

        return data;
    }

    @GetMapping("/getAllCustomer")
    public List<CustomerData> getAllCustomer() throws StripeException {
        Stripe.apiKey = stripeKey;

        Map<String, Object> params = new HashMap<>();
        // limit is optional here, default is 10
        params.put("limit", 3);
        CustomerCollection customers = Customer.list(params);
        List<CustomerData> allCustomer = new ArrayList<CustomerData>();
        for (int i = 0; i < customers.getData().size(); i++) {
            CustomerData customerData = new CustomerData();
            customerData.setCustomerId(customers.getData().get(i).getId());
            customerData.setName(customers.getData().get(i).getName());
            customerData.setEmail(customers.getData().get(i).getEmail());
            allCustomer.add(customerData);
        }

        return allCustomer;

    }

    @DeleteMapping("/customer/{id}")
    public String deleteCustomer(@PathVariable("id") String id) throws StripeException {
        Stripe.apiKey = stripeKey;

        Customer customer = Customer.retrieve(id);

        Customer deletedCustomer = customer.delete();

        return "successfully deleted";
    }

    @GetMapping("/customer/{id}")
    public CustomerData getCustomer(@PathVariable("id") String id) throws StripeException {
        Stripe.apiKey = stripeKey;

        Customer customer = Customer.retrieve(id);

        CustomerData customerData = new CustomerData();
        customerData.setCustomerId(customer.getId());
        customerData.setEmail(customer.getEmail());
        customerData.setName(customer.getName());

        return customerData;
    }
}
