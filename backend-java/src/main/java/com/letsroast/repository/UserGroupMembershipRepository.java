package com.letsroast.repository;

import com.letsroast.model.UserGroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupMembershipRepository extends JpaRepository<UserGroupMembership, Long> {
    @Query("SELECT COUNT(m) > 0 FROM UserGroupMembership m WHERE m.userId = :userId AND m.groupId = :groupId")
    boolean existsByUserIdAndGroupId(@Param("userId") String userId, @Param("groupId") String groupId);
}

