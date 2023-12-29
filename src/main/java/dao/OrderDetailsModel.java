package dao;

import dto.OrderDetailsDto;
import dto.OrderDto;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailsModel {
     boolean saveOrderDetails(List<OrderDetailsDto> list) throws SQLException, ClassNotFoundException;

     List<OrderDetailsDto> getAllOrderDetails() throws SQLException, ClassNotFoundException;

     boolean deleteDetails(String id) throws SQLException, ClassNotFoundException;

}
