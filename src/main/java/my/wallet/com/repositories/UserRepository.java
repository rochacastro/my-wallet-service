package my.wallet.com.repositories;

import java.util.Optional;
import java.util.UUID;
import my.wallet.com.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByCpf(String cpf);
}
