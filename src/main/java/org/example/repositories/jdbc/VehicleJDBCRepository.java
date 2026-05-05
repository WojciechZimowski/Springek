package org.example.repositories.jdbc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.db.JdbcConnectionManager;
import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.IVehicleRepository;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VehicleJDBCRepository implements IVehicleRepository {
    private final Gson gson = new com.google.gson.Gson();
    private final Type mapType = new TypeToken<Map<String,Object>>(){}.getType();
    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicle";
        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                vehicles.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return vehicles;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "Select * from vehicle where id = ?";
        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if(vehicle.getId()==null|| vehicle.getId().isBlank()){
            vehicle.setId(UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb) " +
                "ON CONFLICT (id) DO UPDATE SET price = EXCLUDED.price, attributes = EXCLUDED.attributes";
        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,vehicle.getId());
            stmt.setString(2,vehicle.getCategory());
            stmt.setString(3,vehicle.getBrand());
            stmt.setString(4,vehicle.getModel());
            stmt.setInt(5,vehicle.getYear());
            stmt.setString(6,vehicle.getPlate());
            stmt.setDouble(7,vehicle.getPrice());
            stmt.setString(8,gson.toJson(vehicle.getAttributes()));
            stmt.executeUpdate();
            return vehicle;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";
        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private Vehicle mapRow(ResultSet rs)throws SQLException{
        String attrJson=rs.getString("attributes");
        Map<String,Object> attributes = gson.fromJson(attrJson,mapType);
        return Vehicle.builder().id(rs.getString("id")).
        category(rs.getString("category"))
                .brand(rs.getString("brand"))
                .model(rs.getString("model"))
                .year(rs.getInt("year"))
                .plate(rs.getString("plate"))
                .price(rs.getDouble("price"))
                .attributes(attributes != null ? attributes : new HashMap<>())
                .build();

    }
}
