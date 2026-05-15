package com.letsroast.service;

import com.letsroast.model.Group;
import com.letsroast.model.UserGroupMembership;
import com.letsroast.repository.GroupRepository;
import com.letsroast.repository.UserGroupMembershipRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("postgres")
public class PostgresGroupService implements GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupMembershipRepository membershipRepository;

    public PostgresGroupService(GroupRepository groupRepository, UserGroupMembershipRepository membershipRepository) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
    }

	@Override
	public Group createGroup(String name, String createdBy) {
		Group group = new Group(name, createdBy);
		group = groupRepository.save(group);
		// Creator auto-joins
		membershipRepository.save(new UserGroupMembership(createdBy, group.getId()));
		return group;
	}

	@Override
	public List<Group> listAllGroups() {
		return groupRepository.findAll();
	}

	@Override
	public List<Group> listGroupsForUser(String userId) {
		List<String> groupIds = membershipRepository.findGroupIdsByUserId(userId);
		if (groupIds.isEmpty()) {
			return List.of();
		}
		return groupRepository.findAllById(groupIds);
	}

	@Override
	public void joinGroup(String groupId, String userId) {
		if (groupRepository.existsById(groupId) && !membershipRepository.existsByUserIdAndGroupId(userId, groupId)) {
			membershipRepository.save(new UserGroupMembership(userId, groupId));
		}
	}

	@Override
	public boolean isMember(String groupId, String userId) {
		return membershipRepository.existsByUserIdAndGroupId(userId, groupId);
	}

	@Override
	public Group getGroupById(String groupId) {
		return groupRepository.findById(groupId).orElse(null);
	}
}

