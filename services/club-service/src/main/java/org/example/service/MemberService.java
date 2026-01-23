package org.example.service;

import org.example.entity.Member;

import java.util.List;

public interface MemberService {
    Member createMember(Member member);
    void deleteMember(Integer memberId);
    Member updateMember(Member member);
    List<Member> queryMembers();
    Member getMemberById(Integer memberId);
    List<Member> getMembersByClubId(Integer clubId);
    List<Member> getMembersByUserId(Integer userId);
}