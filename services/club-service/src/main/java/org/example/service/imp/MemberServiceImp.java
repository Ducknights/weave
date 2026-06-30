package org.example.service.imp;

import jakarta.annotation.Resource;
import org.example.model.entity.Member;
import org.example.mapper.MemberMapper;
import org.example.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImp implements MemberService {

    @Resource
    private MemberMapper memberMapper;

    @Override
    public void createMember(Member member) {
        memberMapper.insert(member);
    }

    @Override
    public void deleteMember(Integer memberId) {
        memberMapper.deleteMember(memberId);
    }

    @Override
    public Member updateMember(Member member) {
        return memberMapper.updateMember(member);
    }

    @Override
    public List<Member> queryMembers() {
        return memberMapper.queryMembers();
    }

    @Override
    public Member getMemberById(Integer memberId) {
        return memberMapper.getMemberById(memberId);
    }

    @Override
    public List<Member> getMembersByClubId(Integer clubId) {
        return memberMapper.getMembersByClubId(clubId);
    }

    @Override
    public List<Member> getMembersByUserId(Integer userId) {
        return memberMapper.getMembersByUserId(userId);
    }
}