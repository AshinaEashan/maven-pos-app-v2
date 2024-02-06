package controller;

import bo.custom.CustomerBo;
import bo.custom.impl.CustomerBoImpl;
import com.jfoenix.controls.JFXTextField;
import dto.CustomerDto;
import dto.tm.CustomerTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import dao.custom.CustomerDao;
import dao.custom.impl.CustomerDaoImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class CustomerFormController {

    @FXML
    private BorderPane customerPane;

    @FXML
    private JFXTextField customerIDText;

    @FXML
    private JFXTextField customerNameText;

    @FXML
    private JFXTextField customerAddressText;

    @FXML
    private JFXTextField customerSalaryText;

    @FXML
    private TableView<CustomerTm> tblCustomer;

    @FXML
    private TableColumn colId;

    @FXML
    private TableColumn colName;

    @FXML
    private TableColumn colAddress;

    @FXML
    private TableColumn colSalary;

    @FXML
    private TableColumn colOption;
    private CustomerBo<CustomerDto> customerBo = new CustomerBoImpl();

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        loadCustomerTable();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(CustomerTm newValue) {
        if (newValue != null){
            customerIDText.setEditable(false);
            customerIDText.setText(newValue.getId());
            customerNameText.setText(newValue.getName());
            customerAddressText.setText(newValue.getAddress());
            customerSalaryText.setText(String.valueOf(newValue.getSalary()));
        }
    }

    private void loadCustomerTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();

        try {
            List<CustomerDto> dtoList = customerBo.allCustomers();
            
            for (CustomerDto dto : dtoList){
                Button btn = new Button("Delete");

                CustomerTm cust = new CustomerTm(
                        dto.getId(),
                        dto.getName(),
                        dto.getAddress(),
                        dto.getSalary(), 
                        btn
                );
                
                btn.setOnAction(actionEvent -> {
                    deleteCustomer(cust.getId());
                });
                
                tmList.add(cust);
            }

            tblCustomer.setItems(tmList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void clearFields() {
        tblCustomer.refresh();
        customerIDText.clear();
        customerNameText.clear();
        customerAddressText.clear();
        customerSalaryText.clear();
        customerIDText.setEditable(true);
    }
    private void deleteCustomer(String id) {
        try {
            boolean isDeleted = customerBo.deleteCustomer(id);
            if (isDeleted){
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted!").show();
                loadCustomerTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something Went Wrong").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void BackButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) customerPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.centerOnScreen();
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void RefreshButtonOnAction(ActionEvent event) {
        loadCustomerTable();
        tblCustomer.refresh();
        clearFields();
    }

    @FXML
    void SaveButtonOnAction(ActionEvent event) {
        if(isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields are Empty").show();
        }
        try {
            boolean isSaved = customerBo.saveCustomer(new CustomerDto(
                    customerIDText.getText(),
                    customerNameText.getText(),
                    customerAddressText.getText(),
                    Double.parseDouble(customerSalaryText.getText())
            ));
            if(isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Customer Saved Successfully");
                loadCustomerTable();
                clearFields();
            }
        } catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException | RuntimeException e) {
            new Alert(Alert.AlertType.INFORMATION,"Empty Fields..!").show();
        }


    }



    @FXML
    void UpdateButtonOnAction(ActionEvent event) {
        try {
            boolean isUpdated = customerBo.updateCustomer(new CustomerDto(
                    customerIDText.getText(),
                    customerNameText.getText(),
                    customerAddressText.getText(),
                    Double.parseDouble(customerSalaryText.getText())
            ));
            if (isUpdated){
                new Alert(Alert.AlertType.INFORMATION,"Customer Updated!").show();
                loadCustomerTable();
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException | RuntimeException e){
            new Alert(Alert.AlertType.INFORMATION,"Select a Customer").show();
        }
    }
    private boolean isEmpty(){
        if(customerIDText.getText() != null && customerNameText != null && customerAddressText != null && customerSalaryText != null){
            return false;
        }
        return true;
    }

}
