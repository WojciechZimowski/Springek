package org.example.repositories.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
@Profile("jdbc")
public class RentalJDBCRepository implements RentalRepository {
    private final DataSource dataSource;

    public RentalJDBCRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql =  "SELECT * FROM rental";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(   PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ){
         while(rs.next()){
             rentals.add(mapRow(rs));
         }
        }
         catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {

            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql =  "SELECT * FROM rental Where id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try(   PreparedStatement stmt = conn.prepareStatement(sql);

        ){
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET return_date = EXCLUDED.return_date";
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rental.getId());
            stmt.setString(2, rental.getVehicleId());
            stmt.setString(3, rental.getUserId());
            stmt.setString(4, rental.getRentDateTime());
            stmt.setString(5, rental.getReturnDate());

            stmt.executeUpdate();
            return rental;
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu wypożyczenia", e);
        }finally {

            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql =  "DELETE FROM rental Where id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try( PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setString(1,id);
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql =  "SELECT * FROM rental Where vehicle_id = ? AND return_date IS NULL";
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try( PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setString(1,vehicleId);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);

        }
        return Optional.empty();
    }
    private Rental mapRow(ResultSet rs) throws SQLException {
        Vehicle tempVehicle = Vehicle.builder().id(rs.getString("vehicle_id")).build();
        User tempUser = User.builder().id(rs.getString("user_id")).build();

        return Rental.builder()
                .id(rs.getString("id"))
                .vehicle(tempVehicle)
                .user(tempUser)
                .rentDateTime(rs.getString("rent_date"))
                .returnDate(rs.getString("return_date"))
                .build();
    }
}
