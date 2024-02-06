package dao.custom;

import dto.OrderDetailsDto;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailsDao {
     boolean saveOrderDetails(List<OrderDetailsDto> list) throws SQLException, ClassNotFoundException;

     List<OrderDetailsDto> getAllOrderDetails() throws SQLException, ClassNotFoundException;

     boolean deleteDetails(String id) throws SQLException, ClassNotFoundException;

}
