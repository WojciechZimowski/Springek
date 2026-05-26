package org.example.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Table(name="rental")
public class Rental {
    @Id
    @Column(nullable = false,unique = true)
    private String id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id",nullable = false)
    private Vehicle vehicle;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id",nullable = false)
    private User user;
    @Column(name="rent_date",nullable = false)
    private String rentDateTime;
    @Column(name="return_date")
    private String returnDate;

    public Rental copy() {
        return Rental.builder()
                .id(id)
                .vehicle(vehicle)
                .user(user)
                .rentDateTime(rentDateTime)
                .returnDate(returnDate)
                .build();
    }

    public boolean isActive() {
        return returnDate == null || returnDate.isBlank();
    }
    public String getVehicleId() {
        return vehicle==null?null:vehicle.getId();
    }
    public String getUserId() {
        return user==null?null:user.getId();
    }
    public Vehicle getVehicle() {
        return this.vehicle;
    }
    public User getUser() {
        return this.user;
    }
}