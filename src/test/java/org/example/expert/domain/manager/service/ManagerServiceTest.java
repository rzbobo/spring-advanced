package org.example.expert.domain.manager.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

// 1번 케이스
// 테스트 패키지 package org.example.expert.domain.manager.service; 의 ManagerServiceTest 의 클래스에 있는
// manager_목록_조회_시_Todo가_없다면_NPE_에러를_던진다() 테스트가 성공하고 컨텍스트와 일치하도록
// 테스트 코드와 테스트 코드 메서드 명을 수정해 주세요.


// 3번 케이스
// 테스트 패키지 org.example.expert.domain.manager.service의 ManagerServiceTest 클래스에 있는
// todo의_user가_null인_경우_예외가_발생한다() 테스트가 성공할 수 있도록 서비스 로직을 수정해 주세요.

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;


// 1번 케이스
// 테스트 패키지 package org.example.expert.domain.manager.service; 의 ManagerServiceTest 의 클래스에 있는
// manager_목록_조회_시_Todo가_없다면_NPE_에러를_던진다() 테스트가 성공하고 컨텍스트와 일치하도록
// 테스트 코드와 테스트 코드 메서드 명을 수정해 주세요.
// 1. NPE가 아니라 Invalid오류이므로 테스트 메서드 명을 변경하기
// 2. 오류출력 메세지를 서비스 코드에서 설정해 놓은 오류 출력 메세지와 동일하게 설정하기
    @Test
    public void manager_목록_조회_시_Todo가_없다면_InvalidRequestException_에러를_던진다() {
        // given
        long todoId = 1L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.getManagers(todoId));
        assertEquals("Todo not found", exception.getMessage());
    }

// 3번 케이스
// 테스트 패키지 org.example.expert.domain.manager.service의 ManagerServiceTest 클래스에 있는
// todo의_user가_null인_경우_예외가_발생한다() 테스트가 성공할 수 있도록 서비스 로직을 수정해 주세요.
// 그럼 일단 해당 테스트 코드가 틀린 게 아니라는 가정을 하고 수정을 진행해야 함
// Unexpected exception type thrown, expected: <org.example.expert.domain.common.exception.InvalidRequestException> but was: <java.lang.NullPointerException>
// 해당 오류가 나오는데, Invalid가 나와야하는데 NPE가 나온다라는 뜻.
// 서비스 코드에서 USER가 NULL인 경우를 if문으로 가정해 예외 지정을 해줘야 할 듯 함.
// 확인해보니 save할 때, user에 대한 유효성 검사를 진행하지 않고
// user에서 값을 받아온 값에 대해서만 유효성 검사를 진행함 <<- user가 null일 가능성을 배제
// -> 따라서 user 유효성 검사 로직 추가
    @Test
    void todo의_user가_null인_경우_예외가_발생한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        long todoId = 1L;
        long managerUserId = 2L;

        Todo todo = new Todo();
        ReflectionTestUtils.setField(todo, "user", null);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
            managerService.saveManager(authUser, todoId, managerSaveRequest)
        );

        assertEquals("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test // 테스트코드 샘플
    public void manager_목록_조회에_성공한다() {
        // given
        long todoId = 1L;
        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        Manager mockManager = new Manager(todo.getUser(), todo);
        List<Manager> managerList = List.of(mockManager);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findByTodoIdWithUser(todoId)).willReturn(managerList);

        // when
        List<ManagerResponse> managerResponses = managerService.getManagers(todoId);

        // then
        assertEquals(1, managerResponses.size());
        assertEquals(mockManager.getId(), managerResponses.get(0).getId());
        assertEquals(mockManager.getUser().getEmail(), managerResponses.get(0).getUser().getEmail());
    }

    @Test // 테스트코드 샘플
    void todo가_정상적으로_등록된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId); // request dto 생성

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));
        given(managerRepository.save(any(Manager.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ManagerSaveResponse response = managerService.saveManager(authUser, todoId, managerSaveRequest);

        // then
        assertNotNull(response);
        assertEquals(managerUser.getId(), response.getUser().getId());
        assertEquals(managerUser.getEmail(), response.getUser().getEmail());
    }
}
