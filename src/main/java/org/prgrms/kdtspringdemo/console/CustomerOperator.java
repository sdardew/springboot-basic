package org.prgrms.kdtspringdemo.console;

import org.prgrms.kdtspringdemo.customer.Customer;
import org.prgrms.kdtspringdemo.customer.service.CustomerService;
import org.prgrms.kdtspringdemo.wallet.WalletService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerOperator implements CommandOperator<Customer> {
    private final CustomerService customerService;
    private final WalletService walletService;

    public CustomerOperator(CustomerService customerService, WalletService walletService) {
        this.customerService = customerService;
        this.walletService = walletService;
    }

    // todo : VO로 CustomerData 전달하기
    @Override
    public boolean create(String[] splitList) {
        if (!validationCreateCheck(splitList)) return false;
        var name = splitList[0];
        var email = splitList[1];
        var type = splitList[2];
        Customer customer = customerService.saveCustomer(name, email, type);

        return customer != null;
    }

    @Override
    public void delete(String[] splitList) {

    }

    @Override
    public List<Customer> getAllitems() {
        return customerService.getAllCustomers();
    }

    public Customer findCustomer(String[] splitList) {
        if (!validationFindCheck(splitList)) return null;
        var voucherId = splitList[0];
        Customer customer = customerService.findCustomer(voucherId);
        return customer;
    }

    public List<Customer> getAllBlacklist() {
        return customerService.getAllBlacklist();
    }

    public boolean validationCreateCheck(String[] splitList) {
        if (splitList.length != 3) return false;
        if (!splitList[2].equals("NORMAL") && !splitList[2].equals("BLACK")) return false;

        return true;
    }

    private boolean validationFindCheck(String[] splitList) {
        if (splitList.length != 1) return false;
        return true;
    }
}