package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {


//    ## Lv 2. N+1 문제  `필수`
//  - `TodoController`와 `TodoService`를 통해 `Todo` 관련 데이터를 처리합니다.
//  - 여기서 N+1 문제가 발생할 수 있는 시나리오는 `getTodos` 메서드에서 모든 Todo를 조회할 때,
//   각 Todo와 연관된 엔티티를 개별적으로 가져오는 경우이고 현재 특정 기능을 활용해서 N+1 발생을 방지한 상태입니다.
//  요구사항:
//  - JPQL 특정 기능을 사용하여 N+1 문제를 해결하고 있는 `TodoRepository`가 있습니다. 해당 Repository가 어떤 기능을 활용해서 N+1을 해결하고 있는지 분석 해보세요.
//  - 이를 동일한 동작을 하는 `@EntityGraph` 기반의 구현으로 수정해주세요.

//    fetch join 방식
//    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
//    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    // EntityGraph 방식
    @EntityGraph(attributePaths = {"user"})
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);



    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN FETCH t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    int countById(Long todoId);
}
