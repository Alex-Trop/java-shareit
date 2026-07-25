package ru.practicum.shareit.user;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromUserDto(UserDto userDto, @MappingTarget User user);
}
