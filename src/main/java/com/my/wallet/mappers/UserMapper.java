package com.my.wallet.mappers;

import com.my.wallet.models.User;
import com.my.wallet.vos.UserRequest;
import com.my.wallet.vos.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
  User map(UserRequest source);

  UserResponse mapResponse(User source);
}
