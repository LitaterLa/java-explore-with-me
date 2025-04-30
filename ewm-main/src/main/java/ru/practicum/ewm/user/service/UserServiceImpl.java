package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest userRequest) {
        if (repository.existsByEmail(userRequest.getEmail())) {
            throw new DataConflictException("Электронная почта пользователя должна быть уникальной");
        }
        User saved = repository.save(mapper.toUseModel(userRequest));
        return mapper.toUserDto(saved);
    }

    @Override
    public UserDto findUserById(Integer id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        return mapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll(int from, int size, List<Integer> usersIds) {
        int pageNumber = from >= size ? from / size : 0;
        PageRequest pageable = PageRequest.of(pageNumber, size);

        List<User> users = (usersIds == null || usersIds.isEmpty())
                ? repository.findAll(pageable).getContent()
                : repository.findByIdIn(usersIds, pageable).getContent();

        return users.stream().map(mapper::toUserDto).toList();
    }


    @Override
    @Transactional
    public void delete(Integer id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        repository.delete(user);
    }
}
