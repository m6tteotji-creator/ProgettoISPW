package com.example.onetour.dao;

import com.example.onetour.dao.db.DBConnection;
import com.example.onetour.dao.db.Queries;
import com.example.onetour.enumeration.RoleEnum;
import com.example.onetour.exception.UserNotFoundException;
import com.example.onetour.model.UserAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    public UserAccount findPerson(String email, String password)
            throws SQLException, UserNotFoundException {

        String normalized = email.trim().toLowerCase();

        UserAccount user = findUser(normalized, password);
        if (user != null) return user;

        UserAccount guide = findGuide(normalized, password);
        if (guide != null) return guide;

        throw new UserNotFoundException("User not found");
    }

    private UserAccount findUser(String email, String password) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(
                Queries.FIND_USER_LOGIN,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.first()) return null;

                return new UserAccount(
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("user_email"),
                        RoleEnum.USER
                );

            }
        }
    }

    private UserAccount findGuide(String email, String password) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(
                Queries.FIND_GUIDE_LOGIN,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.first()) return null;

                return new UserAccount(
                        rs.getString("guide_id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("guide_email"),
                        RoleEnum.TOURISTGUIDE
                );

            }
        }
    }
}
