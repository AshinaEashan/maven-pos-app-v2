package dao.custom.impl;

import db.DBConnection;
import dto.ItemDto;
import dao.custom.ItemDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDaoImpl implements ItemDao {
    @Override
    public boolean saveItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO item VALUES(?,?,?,?)";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,dto.getCode());
        pstm.setString(2,dto.getDesc());
        pstm.setDouble(3,Double.parseDouble("" + dto.getUnitPrice()));
        pstm.setInt(4, Integer.parseInt("" + dto.getQty()));

        return pstm.executeUpdate() > 0;
    }

    @Override
    public boolean updateItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE item SET description=?, unitPrice=?, qtyOnHand=? WHERE code=?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,dto.getDesc());
        pstm.setDouble(2,dto.getUnitPrice());
        pstm.setInt(3,dto.getQty());
        pstm.setString(4,dto.getCode());

        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean deleteItem(String code) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM item WHERE code=?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,code);

        return pstm.executeUpdate() > 0;
    }

    @Override
    public List<ItemDto> allItem() throws SQLException, ClassNotFoundException {
        List<ItemDto> list = new ArrayList<>();

        String sql = "SELECT * FROM item";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()){
            list.add(new ItemDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getDouble(3),
                    resultSet.getInt(4)
            ));
        }

        return list;
    }


}


















