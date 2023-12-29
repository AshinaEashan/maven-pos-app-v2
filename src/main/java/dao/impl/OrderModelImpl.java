package dao.impl;

import db.DBConnection;
import dto.OrderDto;
import dao.OrderModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderModelImpl implements OrderModel {
    OrderDetailModelImpl OrderDetailModel = new OrderDetailModelImpl();

    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException {
        Connection connection=null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            String sql = "INSERT INTO orders VALUES(?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, dto.getOrderId());
            pstm.setString(2, String.valueOf(dto.getDate()));
            pstm.setString(3, dto.getCustomerId());
            if (pstm.executeUpdate() > 0) {

                boolean isDetailSaved = OrderDetailModel.saveOrderDetails(dto.getList());
                if (isDetailSaved) {
                    connection.commit();
                    return true;
                }
            }
        }catch (SQLException | ClassNotFoundException ex){
            connection.rollback();
            ex.printStackTrace();
        }finally {
            connection.setAutoCommit(true);
        }
        return false;

    }

    @Override
    public List<OrderDto> allOrders() throws SQLException, ClassNotFoundException {
        List<OrderDto> list = new ArrayList<>();

        String sql = "SELECT * FROM orders";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()){
            list.add(new OrderDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    null
            ));
        }

        return list;
    }

    @Override
    public boolean deleteOrder(String id) throws SQLException, ClassNotFoundException {

        boolean isDeleted = OrderDetailModel.deleteDetails(id);
        boolean completelyDeleted = false;
        if (isDeleted){
            String sql = "DELETE FROM orders WHERE id=?";
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
            pstm.setString(1,id);
             completelyDeleted = pstm.executeUpdate() > 0;
        }
        return completelyDeleted;
    }

}
