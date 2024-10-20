package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Test
    @DisplayName("todo를 정상적으로 등록합니다.")
    void saveTodo() {
        //given
        String weather = "Sunny";
        given(weatherClient.getTodayWeather()).willReturn(weather);

        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("Title","Contents");
        Todo savedTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );

        long savedTodoId = 1L;
        //ReflectionTestUtils를 사용하면 private 같은 객체의 필드 값에, 직접 접근하여 값을 수정할 수 있다.
        //주로 테스트 코드에서 특정 객체의 private 필드 값을 설정하거나 변경할 때 사용합니다.
        ReflectionTestUtils.setField(savedTodo, "id", savedTodoId);
        given(todoRepository.save(any())).willReturn(savedTodo);

        //when
        TodoSaveResponse response = todoService.saveTodo(authUser, todoSaveRequest);

        //then
        assertNotNull(response);
        assertEquals(savedTodo.getId(), response.getId());

    }
}
