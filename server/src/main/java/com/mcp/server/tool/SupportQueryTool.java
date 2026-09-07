package com.mcp.server.tool;

import com.mcp.server.dto.SupportDtos.*;
import com.mcp.server.entity.Customer;
import com.mcp.server.entity.CustomerOrder;
import com.mcp.server.entity.Payment;
import com.mcp.server.repository.*;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SupportQueryTool {

    private final CustomerRepository customers;
    private final ProductRepository products;
    private final OrderRepository orders;
    private final PaymentRepository payments;
    private final SupportTicketRepository tickets;
    private final CustomerRepository customerRepository;

    public SupportQueryTool(CustomerRepository customers,
                            ProductRepository products,
                            OrderRepository orders,
                            PaymentRepository payments,
                            SupportTicketRepository tickets, CustomerRepository customerRepository) {
        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.payments = payments;
        this.tickets = tickets;
        this.customerRepository = customerRepository;
    }

    @McpTool(name = "lookup_customer_by_email",
            description = "Look up a customer account by their email address. Returns name, "
                    + "contact details, preferred language, and loyalty tier. Use this first to "
                    + "identify who sent the email and how to address them.")
    public CustomerInfo lookupCustomerByEmail(
            @McpToolParam(description = "The customer's email address, e.g. sarah.mitchell@example.com")
            String email) {
        Customer c=customerRepository.findByEmailIgnoreCase(email).orElse(null);
        return customerInfo(c);

    }

    @McpTool(name = "get_customer_orders",
            description = "List all orders placed by a customer (most recent first), each with "
                    + "its line items and payments. Use this to find the order an email is about, "
                    + "or to see the customer's purchase history.")
    public List<OrderDetails> getCustomerOrders(
            @McpToolParam(description = "The customer's email address")
            String email) {
        return orders.findByCustomerEmailIgnoreCaseOrderByOrderDateDesc(email.trim())
                .stream()
                .map(this::toOrderDetails)
                .toList();
    }

    private CustomerInfo customerInfo(Customer c) {
        return new CustomerInfo(c.getId(), c.getFullName(), c.getEmail(), c.getPhone(),
                c.getPreferredLanguage(), c.getLoyaltyTier().name());
    }

    private OrderDetails toOrderDetails(CustomerOrder o) {
        List<OrderItemInfo> items = o.getItems().stream()
                .map(i -> new OrderItemInfo(i.getProduct().getSku(), i.getProduct().getName(),
                        i.getQuantity(), i.getUnitPrice()))
                .toList();
        List<PaymentInfo> pays = o.getPayments().stream().map(this::toPaymentInfo).toList();
        Customer c = o.getCustomer();
        return new OrderDetails(o.getOrderNumber(), c.getFullName(), c.getEmail(), o.getOrderDate(),
                o.getStatus().name(), o.getShippingAddress(), o.getTotalAmount(), o.getCurrency(),
                items, pays);
    }

    private PaymentInfo toPaymentInfo(Payment p) {
        return new PaymentInfo(p.getId(), p.getAmount(), p.getCurrency(), p.getPaymentMethod(),
                p.getTransactionRef(), p.getStatus().name(), p.getChargedAt());
    }


}
