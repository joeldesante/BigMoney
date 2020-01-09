package com.yooogle.bigmoney.storage;

import com.yooogle.bigmoney.BigMoney;
import com.yooogle.bigmoney.account.Account;
import com.yooogle.bigmoney.account.AccountStatus;
import com.yooogle.bigmoney.response.DatabaseResponse;
import com.yooogle.bigmoney.response.ResponseType;

import java.sql.*;
import java.util.UUID;

public class Database {

    private String file_url;
    private BigMoney plugin;

    public Database(String file_path, BigMoney plugin) {
        this.file_url = "jdbc:sqlite:" + file_path;
        this.plugin = plugin;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(this.file_url);
    }

    public DatabaseResponse init() {
        // Initialize a table
        String sql = "CREATE TABLE IF NOT EXISTS accounts (\n" +
                "id integer PRIMARY KEY AUTOINCREMENT,\n" +
                "ownerUUID text NOT NULL,\n" +
                "balance real NOT NULL,\n" +
                "status text NOT NULL\n" +
                ");";

        try {
            Statement s = this.connect().createStatement();

            try {
                boolean didExecute = s.execute(sql);

                if(didExecute) {
                    return new DatabaseResponse(ResponseType.SUCCESS, "Table created");
                }

                return new DatabaseResponse(ResponseType.SUCCESS, "Table not created");

            } catch (Exception e) {
                e.printStackTrace();
                return new DatabaseResponse(ResponseType.FAILURE, e.getMessage());
            } finally {
              s.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new DatabaseResponse(ResponseType.FAILURE, e.getMessage());
        }
    }

    public DatabaseResponse fetchAccount(UUID ownerUUID) {

        String sql = "SELECT * FROM accounts WHERE ownerUUID = ?;";

        try {

            PreparedStatement s = this.connect().prepareStatement(sql);
            s.setString(1, ownerUUID.toString());

            try {
                ResultSet result = s.executeQuery();

                if (!result.isClosed()) {
                    Account a = new Account(ownerUUID, this.plugin);
                    a.setBalance(result.getDouble("balance"));
                    a.setAccountStatus(AccountStatus.valueOf(result.getString("status")));
                    return new DatabaseResponse(a, ResponseType.SUCCESS, "Account found");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return new DatabaseResponse(ResponseType.FAILURE, e.getMessage());
            } finally {
                s.close();
            }

        } catch(Exception e) {
            e.printStackTrace();
            return new DatabaseResponse(ResponseType.FAILURE, e.getMessage());
        }

        return new DatabaseResponse(ResponseType.FAILURE, "Account not found");
    }

    public DatabaseResponse insert(UUID ownerUUID, double balance, AccountStatus status) {
        String sql = "INSERT INTO accounts(ownerUUID, balance, status) VALUES(?,?,?);";

        DatabaseResponse accountExists = this.fetchAccount(ownerUUID);

        if(accountExists.isSuccess()) {
            return new DatabaseResponse(ResponseType.FAILURE, "Account exists");
        }

        try {

            PreparedStatement s = this.connect().prepareStatement(sql);
            s.setString(1, ownerUUID.toString());
            s.setDouble(2, balance);
            s.setString(3, status.toString());

            try {
                int response = s.executeUpdate();

                if(response <= 0) {
                    return new DatabaseResponse(ResponseType.FAILURE, "Insert failed");
                }

                return new DatabaseResponse(ResponseType.SUCCESS, "Insert success");

            } catch (Exception e) {
                e.printStackTrace();
                return new DatabaseResponse(ResponseType.FAILURE, e.getMessage());
            } finally {
                s.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new DatabaseResponse(ResponseType.FAILURE, e.getMessage());
        }
    }

    public DatabaseResponse update(UUID ownerUUID, double balance, AccountStatus status) {
        String sql = "UPDATE accounts SET balance = ?, status = ? WHERE ownerUUID = ?;";

        try {

            PreparedStatement s = this.connect().prepareStatement(sql);
            s.setDouble(1, balance);
            s.setString(2, status.toString());
            s.setString(3, ownerUUID.toString());

            try {
                int response = s.executeUpdate();

                if(response > 0) {
                    return new DatabaseResponse(ResponseType.SUCCESS, "Updated Account");
                }

                return new DatabaseResponse(ResponseType.FAILURE, "No account found");

            } catch (Exception e) {
                e.printStackTrace();
                return new DatabaseResponse(ResponseType.FAILURE, e.getMessage());
            } finally {
                s.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new DatabaseResponse(ResponseType.FAILURE, e.getMessage());
        }
    }

}
