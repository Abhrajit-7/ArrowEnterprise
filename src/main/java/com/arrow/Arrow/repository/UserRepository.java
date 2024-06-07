package com.arrow.Arrow.repository;

import com.arrow.Arrow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameAndPassword(String username, String password);
    User findByUsername(String username);
    //List<User> findByParentId(Long parentId);


   // @EntityGraph(attributePaths = "children")
    //Optional<User> findById(Long id);


    //List<User> findChildrenById(Long id);

    //@Query("SELECT u FROM User u WHERE u.username = :username")
    //User findParentUser(String username);

}
