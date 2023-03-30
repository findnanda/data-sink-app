package uk.co.data.sink.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.data.sink.model.User;

@Repository
public interface UserRepository extends CassandraRepository<User, String> {
    @Query("update user SET city = ?0 , WHERE user_id = ?1")
    void updateUser(String city, String userId);

    User findByUserId(String userId);
}
