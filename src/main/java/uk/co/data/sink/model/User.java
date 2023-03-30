package uk.co.data.sink.model;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @PrimaryKeyColumn(name="user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String userId;
    @PrimaryKeyColumn(name="firstname", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    private String firstname;
    private String lastname;
    private String city;

}