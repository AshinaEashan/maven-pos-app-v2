package bo.custom.impl;

import bo.custom.CustomerBo;
import dao.custom.CustomerDao;
import dao.custom.impl.CustomerDaoImpl;
import dto.CustomerDto;
import entity.Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerBoImpl implements CustomerBo<CustomerDto> {
    CustomerDao customerDao = new CustomerDaoImpl();
    @Override
    public boolean saveCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException {
        return customerDao.save(new Customer(
                dto.getId(),
                dto.getName(),
                dto.getAddress(),
                dto.getSalary()
        ));
    }

    @Override
    public boolean deleteCustomer(String value) throws SQLException, ClassNotFoundException {
        return customerDao.delete(value);
    }

    @Override
    public boolean updateCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException {
        return customerDao.update(new Customer(
                dto.getId(),
                dto.getName(),
                dto.getAddress(),
                dto.getSalary()
        ));
    }

    @Override
    public List<CustomerDto> allCustomers() throws SQLException, ClassNotFoundException {
        List<Customer> entityList = customerDao.getAll();
        List<CustomerDto> list = new ArrayList<>();

        for (Customer customer:entityList) {
            list.add(new CustomerDto(
                    customer.getId(),
                    customer.getName(),
                    customer.getAddress(),
                    customer.getSalary()
            ));
        }

        return list;
    }
}