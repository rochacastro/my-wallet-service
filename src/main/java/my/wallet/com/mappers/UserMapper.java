package my.wallet.com.mappers;

import my.wallet.com.models.User;
import my.wallet.com.vos.UserRequest;
import my.wallet.com.vos.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
  User map(UserRequest source);

  UserResponse mapResponse(User source);
}
